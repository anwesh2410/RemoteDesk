import tkinter as tk
from create_frame import CreateFrame

class Authenticate(tk.Frame):
    def __init__(self, cSocket):
        self.cSocket = cSocket
        self.verify = ""
        super().__init__()

        self.label1 = tk.Label(self, text="Password")
        self.text1 = tk.Entry(self, width=15)
        self.label = tk.Label(self, text="")
        self.submit_btn = tk.Button(self, text="SUBMIT", command=self.on_submit)

        self.label1.pack()
        self.text1.pack()
        self.label.pack()
        self.submit_btn.pack()

        self.pack()

    def on_submit(self):
        value1 = self.text1.get()

        try:
            psswrchk = self.cSocket.makefile(mode='w')
            verification = self.cSocket.makefile(mode='r')

            psswrchk.write(value1 + '\n')
            psswrchk.flush()
            self.verify = verification.readline().strip()

        except Exception as e:
            print(e)

        if self.verify == "valid":
            try:
                width = verification.readline().strip()
                height = verification.readline().strip()
            except Exception as e:
                print(e)

            CreateFrame(self.cSocket, width, height)
            self.destroy()
        else:
            print("Enter the valid password")
            tk.messagebox.showerror("Error", "Incorrect password")
            self.destroy()
