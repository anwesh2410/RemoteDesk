import socket
import threading
from SendScreen import SendScreen
from ReceiveEvents import ReceiveEvents

class InitConnection:
    def __init__(self, port, value1):
        self.port = port
        self.value1 = value1

        print("Waiting for connections from clients...")

        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind(('localhost', int(port)))
        self.server_socket.listen(1)

        self.rectangle = None
        self.robot = None

        print("Server is ready to accept connections.")

        while True:
            print("Waiting for a client to connect...")
            client_socket, _ = self.server_socket.accept()
            print("Client connected.")
            password = client_socket.recv(1024).decode()
            if password == value1:
                print("Password is correct. Authenticating...")
                client_socket.sendall(b'valid')
                width = str(self.rectangle.width)
                height = str(self.rectangle.height)
                client_socket.sendall(width.encode())
                client_socket.sendall(height.encode())
                print("Sending screen to client...")
                SendScreen(client_socket, self.rectangle).start()
                print("Receiving events from client...")
                ReceiveEvents(client_socket).start()
            else:
                print("Invalid password. Closing connection...")
                client_socket.sendall(b'Invalid')
