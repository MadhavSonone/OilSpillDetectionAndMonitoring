import cv2
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import load_model
import os
from datetime import datetime

class OilSpillDetector:
    def __init__(self, model_path: str):
        self.model_path = model_path
        self.model = None
        self.load_engine()

    def load_engine(self):
        try:
            if os.path.exists(self.model_path):
                self.model = load_model(self.model_path)
                print(f"AI Engine Loaded: {self.model_path}")
            else:
                print(f"Warning: Model not found at {self.model_path}")
        except Exception as e:
            print(f"Critical error loading AI engine: {e}")

    def preprocess(self, image_bytes):
        # Decode image from bytes
        nparr = np.frombuffer(image_bytes, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        img_resized = cv2.resize(img, (512, 512))
        img_normalized = img_resized / 255.0
        return np.expand_dims(img_normalized, axis=0), img.shape

    def detect(self, image_bytes):
        if self.model is None:
            return None

        input_data, original_shape = self.preprocess(image_bytes)
        
        # Predict mask
        prediction = self.model.predict(input_data, verbose=0)
        mask = np.argmax(prediction, axis=-1)[0]

        # Calculate metrics
        # (Simplified: Counting pixels in class 1 - Oil Spill)
        spill_pixel_count = np.sum(mask == 1)
        # Assuming each pixel at 512x512 represents a specific area (placeholder)
        area_sq_km = round(spill_pixel_count * 0.005, 2) 
        confidence = round(float(np.max(prediction)), 2)

        return {
            "id": f"S-{datetime.now().strftime('%M%S')}",
            "area": f"{area_sq_km} sq km",
            "confidence": f"{int(confidence * 100)}%",
            "timestamp": "Just now",
            "status": "CRITICAL" if area_sq_km > 5 else "WARNING"
        }

# Singleton instance
detector = OilSpillDetector(os.path.join(os.path.dirname(__file__), "models", "unet_model.keras"))
