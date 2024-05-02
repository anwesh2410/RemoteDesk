import tkinter as tk

class SendEvents:
    def __init__(self, cSocket, width, height):
        self.cSocket = cSocket
        self.width = width
        self.height = height
        self.w = float(width)
        self.h = float(height)

        self.cPanel = tk.Canvas(self)

        self.cPanel.bind("<Motion>", self.on_mouse_move)
        self.cPanel.bind("<Button-1>", self.on_mouse_click)
        self.cPanel.bind("<ButtonRelease-1>", self.on_mouse_release)
        self.cPanel.bind("<KeyPress>", self.on_key_press)
        self.cPanel.bind("<KeyRelease>", self.on_key_release)

        self.cPanel.pack()

    def on_mouse_move(self, event):
        x_scale = self.w / self.cPanel.winfo_width()
        y_scale = self.h / self.cPanel.winfo_height()
        x = int(event.x * x_scale)
        y = int(event.y * y_scale)
        self.send_command("MOVE_MOUSE", x, y)

    def on_mouse_click(self, event):
        button = 1  # Left mouse button
        self.send_command("PRESS_MOUSE", button)

    def on_mouse_release(self, event):
        button = 1  # Left mouse button
        self.send_command("RELEASE_MOUSE", button)

    def on_key_press(self, event):
        key_code = event.keycode
        self.send_command("PRESS_KEY", key_code)

    def on_key_release(self, event):
        key_code = event.keycode
        self.send_command("RELEASE_KEY", key_code)

    def send_command(self, command, *args):
        try:
            self.cSocket.send(f"{command}\n".encode())
            for arg in args:
                self.cSocket.send(f"{arg}\n".encode())
        except Exception as e:
            print(e)

