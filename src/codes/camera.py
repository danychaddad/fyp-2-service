import cv2
import requests
import time

# Define the Raspberry Pi's IP address (replace with the actual IP address)
RPI_IP = 'http://192.168.1.106:5000/upload'

def capture_and_send_image():
    # Initialize the camera
    cap = cv2.VideoCapture(0)

    if not cap.isOpened():
        print("Error: Cannot access the camera")
        return

    while True:
        # Capture a frame
        ret, frame = cap.read()
        if ret:
            # Save the captured image locally
            image_path = 'laptop_image.jpg'
            cv2.imwrite(image_path, frame)

            # Send the image to Raspberry Pi
            with open(image_path, 'rb') as img:
                files = {'image': img}
                try:
                    response = requests.post(RPI_IP, files=files)
                    print(response.text)
                except Exception as e:
                    print(f"Failed to send image: {e}")

        # Wait for 10 seconds before capturing and sending the next image
        time.sleep(10)

    cap.release()

if __name__ == '__main__':
    capture_and_send_image()
