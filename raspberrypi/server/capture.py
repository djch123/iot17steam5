import time

import picamera

def capture(image_path):
	with picamera.PiCamera() as camera:
		camera.capture(image_path)