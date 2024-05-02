import socket
import tkinter as tk

class Start:
    def __init__(self):
        self.port = "4907"

    def main(self):
        ip = input("Please enter server ip: ")
        self.initialize(ip, int(self.port))

    def initialize(self, ip, port):
        try:
            sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sc.connect((ip, port))
            print("Connecting to the Server")
        
            # Authenticate class is responsible for security purposes
            frame1 = Authenticate(sc)

            frame1.setSize(300, 80)
            frame1.setLocation(500, 300)
            frame1.setVisible(True)
        except Exception as ex:
            print(ex)

if __name__ == "__main__":
    start = Start()
    start.main()
