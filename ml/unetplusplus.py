# oil_spill_segmentation.py

import os
import numpy as np
from PIL import Image
import torch
import torch.nn as nn
from torch.utils.data import Dataset, DataLoader, random_split
import albumentations as A
from albumentations.pytorch import ToTensorV2
import segmentation_models_pytorch as smp
from sklearn.metrics import jaccard_score
import matplotlib.pyplot as plt

# ======================
# CONFIG
# ======================
IMG_SIZE = 512
BATCH_SIZE = 8
EPOCHS = 30
LR = 3e-4

IMAGE_DIR = "/kaggle/input/oil-spill/oil-spill/train/images/"
MASK_DIR  = "/kaggle/input/oil-spill/oil-spill/train/labels/"

DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# ======================
# COLOR MAPPING
# ======================
COLOR_MAPPING = {
    (0, 0, 0): 0,        # Sea
    (0, 255, 255): 1,    # Oil
    (255, 0, 0): 2,      # Look-alikes
    (153, 76, 0): 3,     # Ships
    (0, 153, 0): 4       # Land
}

def rgb_to_class(mask):
    mask = mask.astype(np.uint8)
    class_mask = np.zeros(mask.shape[:2], dtype=np.int64)

    for rgb, cls in COLOR_MAPPING.items():
        matches = np.all(mask == rgb, axis=-1)
        class_mask[matches] = cls

    return class_mask

# ======================
# AUGMENTATIONS
# ======================
def get_train_transforms():
    return A.Compose([
        A.Resize(IMG_SIZE, IMG_SIZE),
        A.HorizontalFlip(p=0.5),
        A.VerticalFlip(p=0.5),
        A.Rotate(limit=15, p=0.5),
        A.RandomBrightnessContrast(p=0.3),
        A.HueSaturationValue(p=0.3),
        A.Normalize(mean=(0.5,0.5,0.5), std=(0.5,0.5,0.5)),
        ToTensorV2()
    ])

def get_val_transforms():
    return A.Compose([
        A.Resize(IMG_SIZE, IMG_SIZE),
        A.Normalize(mean=(0.5,0.5,0.5), std=(0.5,0.5,0.5)),
        ToTensorV2()
    ])

# ======================
# DATASET
# ======================
class OilDataset(Dataset):
    def __init__(self, img_dir, mask_dir, transforms=None):
        self.img_dir = img_dir
        self.mask_dir = mask_dir
        self.transforms = transforms

        self.images = sorted(os.listdir(img_dir))
        self.masks  = sorted(os.listdir(mask_dir))

    def __len__(self):
        return len(self.images)

    def __getitem__(self, idx):
        img = np.array(Image.open(os.path.join(self.img_dir, self.images[idx])).convert("RGB"))
        mask = np.array(Image.open(os.path.join(self.mask_dir, self.masks[idx])).convert("RGB"))

        mask = rgb_to_class(mask)

        if self.transforms:
            aug = self.transforms(image=img, mask=mask)
            img = aug["image"]
            mask = aug["mask"]

        return img, mask.long()

# ======================
# LOAD DATA + SPLIT
# ======================
full_dataset = OilDataset(IMAGE_DIR, MASK_DIR, transforms=None)

train_size = int(0.7 * len(full_dataset))
val_size   = int(0.15 * len(full_dataset))
test_size  = len(full_dataset) - train_size - val_size

train_ds, val_ds, test_ds = random_split(full_dataset, [train_size, val_size, test_size])

train_ds.dataset.transforms = get_train_transforms()
val_ds.dataset.transforms   = get_val_transforms()
test_ds.dataset.transforms  = get_val_transforms()

train_loader = DataLoader(train_ds, batch_size=BATCH_SIZE, shuffle=True, num_workers=4)
val_loader   = DataLoader(val_ds, batch_size=BATCH_SIZE, shuffle=False, num_workers=4)
test_loader  = DataLoader(test_ds, batch_size=BATCH_SIZE, shuffle=False, num_workers=4)

# ======================
# MODEL
# ======================
model = smp.UnetPlusPlus(
    encoder_name="efficientnet-b5",
    encoder_weights="imagenet",
    classes=5,
    activation=None
).to(DEVICE)

# ======================
# LOSS (Dice + Focal)
# ======================
dice_loss = smp.losses.DiceLoss(mode="multiclass")
focal_loss = smp.losses.FocalLoss(mode="multiclass")

def loss_fn(pred, target):
    return dice_loss(pred, target) + focal_loss(pred, target)

# ======================
# OPTIMIZER + SCHEDULER
# ======================
optimizer = torch.optim.AdamW(model.parameters(), lr=LR)

scheduler = torch.optim.lr_scheduler.ReduceLROnPlateau(
    optimizer, mode='min', patience=3, factor=0.5
)

# ======================
# TRAINING FUNCTIONS
# ======================
def train_epoch(loader):
    model.train()
    total_loss = 0

    for imgs, masks in loader:
        imgs, masks = imgs.to(DEVICE), masks.to(DEVICE)

        optimizer.zero_grad()
        preds = model(imgs)

        loss = loss_fn(preds, masks)
        loss.backward()
        optimizer.step()

        total_loss += loss.item()

    return total_loss / len(loader)

def val_epoch(loader):
    model.eval()
    total_loss = 0

    with torch.no_grad():
        for imgs, masks in loader:
            imgs, masks = imgs.to(DEVICE), masks.to(DEVICE)

            preds = model(imgs)
            loss = loss_fn(preds, masks)

            total_loss += loss.item()

    return total_loss / len(loader)

# ======================
# TRAIN LOOP
# ======================
best_val = float("inf")

for epoch in range(EPOCHS):
    train_loss = train_epoch(train_loader)
    val_loss = val_epoch(val_loader)

    scheduler.step(val_loss)

    print(f"Epoch {epoch+1}/{EPOCHS} | Train: {train_loss:.4f} | Val: {val_loss:.4f}")

    if val_loss < best_val:
        best_val = val_loss
        torch.save(model.state_dict(), "best_model.pth")

# ======================
# EVALUATION
# ======================
def evaluate(loader):
    model.eval()
    preds_all, labels_all = [], []

    with torch.no_grad():
        for imgs, masks in loader:
            imgs = imgs.to(DEVICE)

            preds = torch.argmax(model(imgs), dim=1).cpu().numpy()
            labels = masks.numpy()

            preds_all.extend(preds.flatten())
            labels_all.extend(labels.flatten())

    iou = jaccard_score(labels_all, preds_all, average='macro')
    print("Mean IoU:", iou)

evaluate(test_loader)

# ======================
# VISUALIZATION
# ======================
def visualize(image, pred):
    plt.figure(figsize=(10,5))

    plt.subplot(1,2,1)
    plt.imshow(image)
    plt.title("Input Image")

    plt.subplot(1,2,2)
    plt.imshow(pred, cmap='jet')
    plt.title("Prediction")

    plt.show()

# Example inference
sample_img = np.array(Image.open(os.path.join(IMAGE_DIR, os.listdir(IMAGE_DIR)[0])).convert("RGB"))

transform = get_val_transforms()
aug = transform(image=sample_img)
input_tensor = aug["image"].unsqueeze(0).to(DEVICE)

model.eval()
with torch.no_grad():
    pred = torch.argmax(model(input_tensor), dim=1).squeeze().cpu().numpy()

visualize(sample_img, pred)