# Remote Desktop Application

This document provides instructions for executing the client and server JAR files of the Remote Desktop Application on both Windows and Unix-based operating systems.

## Prerequisites

- **Java Runtime Environment (JRE):**
  - Ensure that you have Java Runtime Environment (JRE) installed on your system.
  - You can download and install the latest version of JRE from the official [Java website](https://www.oracle.com/java/technologies/javase-jre8-downloads.html).

- **Operating System Compatibility:**
  - The JAR files are compatible with Windows, macOS, Linux, and other Unix-based operating systems.
  - Ensure that your system meets the minimum requirements for running Java applications.

- **Network Connectivity:**
  - Both the client and server machines should be connected to the same network.
  - Ensure that there are no firewall restrictions preventing communication between the client and server.

## Server Side

The server side of the Remote Desktop Application is responsible for managing client connections, authenticating users, capturing the server's screen, and sending screen updates to connected clients.

## Client Side

The client side of the Remote Desktop Application is responsible for authenticating users, displaying the server's desktop interface, and handling user input events.


## Steps to Execute

### For Windows:

1. **Download JAR Files:**
   - Ensure you have downloaded the `client.jar` and `server.jar` files from the JAR folder in the repository.

2. **Open JAR Files:**
   - Directly open the respective jar files that you need


### For Unix-Based Systems (Linux, macOS, etc.):

1. **Download JAR Files:**
   - Ensure you have downloaded the `client.jar` and `server.jar` files from the JAR folder in the repository.

2. **Server Execution:**
   - Open Terminal.
   - Navigate to the directory containing the `server.jar` file.
   - Execute the following command:
     ```
     java -jar server.jar
     ```

3. **Client Execution:**
   - Open another Terminal window.
   - Navigate to the directory containing the `client.jar` file.
   - Execute the following command:
     ```
     java -jar client.jar
     ```

4. **Enter Server IP and Password:**
   - On the client-side, enter the server's IP address and the password set on the server-side for authentication.

5. **Access Remote Desktop:**
   - Once authenticated, the client will display the server's desktop, and you can start controlling it remotely.

## Dependencies

- None. The JAR files contain all necessary dependencies bundled within them.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, feel free to open an issue or create a pull request.
