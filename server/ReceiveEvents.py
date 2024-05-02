import threading
import pyautogui

class ReceiveEvents(threading.Thread):
    def __init__(self, socket):
        super().__init__()
        self.socket = socket
        self.continueLoop = True

    def run(self):
        while self.continueLoop:
            command = self.socket.recv(1024)
            command = int(command.decode())
            if command == -1:
                pyautogui.mouseDown()
            elif command == -2:
                pyautogui.mouseUp()
            elif command == -3:
                pyautogui.keyDown()
            elif command == -4:
                pyautogui.keyUp()
            elif command == -5:
                x = int(self.socket.recv(1024).decode())
                y = int(self.socket.recv(1024).decode())
                pyautogui.moveTo(x, y)
