import tkinter as tk
from PIL import Image, ImageTk
import io

class ReceiveScreen(tk.Frame):
    def __init__(self, cSocket, width, height):
        self.cSocket = cSocket
        self.width = width
        self.height = height
        super().__init__()

        self.cPanel = tk.Canvas(self)
        self.image_label = tk.Label(self, image=None)

        self.receive_screen()

    def receive_screen(self):
        try:
            while True:
                bytes_data = self.cSocket.recv(1024 * 1024)

                image_data = Image.open(io.BytesIO(bytes_data))
                image_data = image_data.resize((self.width, self.height))
                img = ImageTk.PhotoImage(image_data)

                self.image_label.configure(image=img)
                self.image_label.image = img

                self.cPanel.create_image(0, 0, anchor="nw", image=img)
                self.cPanel.pack()

        except Exception as e:
            print(e)
