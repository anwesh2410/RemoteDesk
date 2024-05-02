import threading
from PIL import ImageGrab

class SendScreen(threading.Thread):
    def __init__(self, socket, rectangle):
        super().__init__()
        self.socket = socket
        self.rectangle = rectangle
        self.continueLoop = True

    def run(self):
        while self.continueLoop:
            image = ImageGrab.grab(self.rectangle)
            image_bytes = image.tobytes()
            self.socket.sendall(image_bytes)
