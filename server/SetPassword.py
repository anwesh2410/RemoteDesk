from InitConnection import InitConnection
from tkinter import Tk, Label, Entry, Button

class SetPassword:
    def __init__(self):
        self.port = "4907"
        self.root = Tk()
        self.root.title("Set Password to connect to the Client")
        
        self.label1 = Label(self.root, text="Set Password")
        self.label1.grid(row=0, column=0)
        
        self.text1 = Entry(self.root, width=15)
        self.text1.grid(row=0, column=1)
        
        self.label = Label(self.root, text="")
        self.label.grid(row=1, columnspan=2)
        
        self.SUBMIT = Button(self.root, text="SUBMIT", command=self.submit_password)
        self.SUBMIT.grid(row=2, columnspan=2)
        
        self.root.geometry("300x120+500+300")
        self.root.mainloop()
    
    def submit_password(self):
        value1 = self.text1.get()
        self.root.destroy()
        InitConnection(self.port, value1)
