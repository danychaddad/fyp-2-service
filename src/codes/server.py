import os
import base64
import requests
from datetime import datetime
from flask import Flask, request
from ultralytics import YOLO

try:
    import RPi.GPIO as GPIO
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(16, GPIO.OUT)
except ImportError:
    print("GPIO library not found. Using simulated pin output.")
    GPIO = None

app = Flask(__name__)
model = YOLO('fire_l.pt')
API_URL = "http://ec2-13-61-40-192.eu-north-1.compute.amazonaws.com:8080/nodes/3c:71:bf:37:4d:e8/images"

def set_pin_output(fire_detected):
    """
    Set pin output based on fire detection
    0 - No fire
    1 - Fire/Smoke detected
    """
    if GPIO:
        GPIO.output(16, GPIO.HIGH if fire_detected else GPIO.LOW)
    else:
        print(f"Simulated Pin Output: {'HIGH (Fire Detected)' if fire_detected else 'LOW (No Fire)'}")

@app.route('/upload', methods=['POST'])
def upload_image():
    file = request.files['image']
    if file:
        # Save the uploaded image
        image_path = os.path.join('received_images', file.filename)
        file.save(image_path)
        
        # Process the saved image with YOLO model
        results = model(image_path, save=True)
        
        # Get the directory where results are saved
        results_dir = model.predictor.save_dir
        processed_image_path = os.path.join(results_dir, file.filename)
        
        # Check for fire/smoke detection
        fire_detected = False
        for result in results:
            # Convert to list to get detected classes
            detected_classes = result.boxes.cls.tolist()
            
            # Assuming class 0 is fire/smoke (verify this with your model's training)
            if 0 in detected_classes:
                fire_detected = True
                break
        
        # Set pin output and print console message
        set_pin_output(fire_detected)
        print(f"Fire Detection Result: {'FIRE DETECTED' if fire_detected else 'NO FIRE'}")
        
        # Convert processed image to Base64
        with open(processed_image_path, "rb") as img_file:
            encoded_image = base64.b64encode(img_file.read()).decode('utf-8')
        
        # Prepare payload
        payload = {
            "image": "data:image/jpeg;base64," + encoded_image,
            "timestamp": datetime.utcnow().isoformat(),
            "fire_detected": fire_detected
        }
        
        # Send POST request to API
        response = requests.post(API_URL, json=payload)
        if response.status_code == 200:
            return "Image processed and sent successfully!", 200
        else:
            return f"Failed to send image: {response.text}", response.status_code
    
    return 'No image uploaded', 400

def cleanup():
    """Cleanup GPIO on exit"""
    if GPIO:
        GPIO.cleanup()

if __name__ == '__main__':
    try:
        os.makedirs('received_images', exist_ok=True)
        app.run(host='0.0.0.0', port=5000)
    except Exception as e:
        print(f"Error starting application: {e}")
    finally:
        cleanup()

