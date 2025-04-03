import sys
import cv2
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model
import os

# Load U-Net model once
try:
    MODEL_PATH = os.path.join(os.path.dirname(__file__), "unet_model.keras")
    unet_model = load_model(MODEL_PATH)
except Exception as e:
    print(f"Error loading model: {e}")
    sys.exit(1)

COLOR_MAP = {
    0: [0, 0, 0],      # Background
    1: [0, 255, 255],  # Oil Spill
    2: [255, 0, 0],    # Lookalike
    3: [153, 76, 0],   # Station
    4: [0, 153, 0]     # Land
}

def apply_colormap(mask):
    """Applies color mapping to the predicted segmentation mask."""
    color_mask = np.zeros((*mask.shape, 3), dtype=np.uint8)
    for class_idx, color in COLOR_MAP.items():
        color_mask[mask == class_idx] = color
    return color_mask

def get_output_path(image_path):
    """Generates an output path while keeping the original filename."""
    directory, filename = os.path.split(image_path)
    filename_wo_ext, _ = os.path.splitext(filename)
    
    # Output directory
    output_directory = "Outputs"
    os.makedirs(output_directory, exist_ok=True)
    
    # Save the file directly in Outputs with the same base filename but "_segmented" suffix
    output_path = os.path.join(output_directory, f"{filename_wo_ext}_segmented.jpg")
    return output_path

def process_image(image_path):
    """Loads and processes an image file, then applies segmentation."""
    if not os.path.exists(image_path):
        print("Error: Image file not found!")
        return ""

    image = cv2.imread(image_path, cv2.IMREAD_COLOR)
    if image is None:
        print("Error: Unable to read the image. Check the file format!")
        return ""

    image = cv2.resize(image, (512, 512))  # Resize the image to match the model input size
    image_input = np.expand_dims(image / 255.0, axis=0)  # Normalize

    # Predict mask
    try:
        predicted_mask = unet_model.predict(image_input, verbose=0)
        predicted_mask = np.argmax(predicted_mask, axis=-1)[0]
    except Exception as e:
        print(f"Error during prediction: {e}")
        return ""

    # Apply color map and save output
    segmented_image = apply_colormap(predicted_mask)
    output_path = get_output_path(image_path)
    cv2.imwrite(output_path, segmented_image)

    print(output_path)  # Output the file path for Java to read
    return output_path

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python analyze.py <image_path>")
        sys.exit(1)

    image_path = sys.argv[1]  
    process_image(image_path)
