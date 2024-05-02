import tkinter as tk

class CreateFrame(tk.Tk):
    def __init__(self, cSocket, width, height):
        self.width = width
        self.height = height
        self.cSocket = cSocket
        super().__init__()

        self.desktop = tk.Canvas(self)
        self.interFrame = tk.Toplevel(self)

        self.draw_gui()

    def draw_gui(self):
        self.desktop.pack(expand=True, fill=tk.BOTH)
        self.interFrame.title("Server Screen")
        self.interFrame.geometry("300x300")

        try:
            self.interFrame.attributes('-zoomed', True)
        except Exception as ex:
            print(ex)

        self.interFrame.protocol("WM_DELETE_WINDOW", self.on_close)

    def on_close(self):
        self.cSocket.close()
        self.destroy()
