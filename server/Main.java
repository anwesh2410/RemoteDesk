import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;


public class Main extends JFrame implements ActionListener {
    static String port = "4906";
    JButton connectButton, terminateButton; // Buttons for connecting and terminating sharing
    JPanel panel;
    JLabel label1;
    JPasswordField passwordField;
    String password;
    ServerSocket serverSocket;
    boolean sharingStarted = false;

    Main() {
        label1 = new JLabel();
        label1.setText("Enter Password");
        passwordField = new JPasswordField(15);

        this.setLayout(new BorderLayout());

        connectButton = new JButton("Connect");
        connectButton.addActionListener(this);

        panel = new JPanel(new GridLayout(3, 1));
        panel.add(label1);
        panel.add(passwordField);
        panel.add(connectButton);

        add(panel, BorderLayout.CENTER);

        setTitle("Remote Desktop Control Server");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Initialize server socket
        try {
            serverSocket = new ServerSocket(Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == connectButton) {
            if (!sharingStarted) {
                password = new String(passwordField.getPassword());
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a password", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    new InitConnection(Integer.parseInt(port), password);
                    sharingStarted = true;
                    connectButton.setEnabled(false);
                    createTerminateDialog(); // Create separate terminate dialog
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sharing already started", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
    }

    private void createTerminateDialog() {
        JFrame terminateFrame = new JFrame("Terminate Sharing");
        terminateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel terminatePanel = new JPanel();
        terminateButton = new JButton("Terminate Sharing");
        terminateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(terminateFrame, "Are you sure you want to terminate sharing?", "Terminate Sharing", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    terminateSharing();
                    terminateFrame.dispose();
                }
            }
        });
        terminatePanel.add(terminateButton);
        terminateFrame.add(terminatePanel);
        terminateFrame.setSize(200, 100);
        terminateFrame.setLocationRelativeTo(null);
        terminateFrame.setVisible(true);
    }

    private void terminateSharing() {
        dispose();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Sharing terminated.");
    }

    public static void main(String[] args) {
        new Main();
    }
}


class InitConnection {
    ServerSocket socket = null;
    DataInputStream password = null;
    DataOutputStream verify = null;

    InitConnection(int port, String value1) {
        Robot robot = null;
        Rectangle rectangle = null;
        try {
            System.out.println("Awaiting Connection from Client");
            socket = new ServerSocket(port);

            GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gDev = gEnv.getDefaultScreenDevice();

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            String width = "" + dim.getWidth();
            String height = "" + dim.getHeight();
            rectangle = new Rectangle(dim);
            robot = new Robot(gDev);

            while (true) {
                Socket sc = socket.accept();
                password = new DataInputStream(sc.getInputStream());
                verify = new DataOutputStream(sc.getOutputStream());
                String pssword = password.readUTF();

                if (pssword.equals(value1)) {
                    verify.writeUTF("valid");
                    verify.writeUTF(width);
                    verify.writeUTF(height);
                    new SendScreen(sc, robot, rectangle);
                    new ReceiveEvents(sc, robot);
                } else {
                    verify.writeUTF("Invalid");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class SendScreen extends Thread {
    Socket socket = null;
    Robot robot = null;
    Rectangle rectangle = null;
    boolean continueLoop = true;
    OutputStream oos = null;

    public SendScreen(Socket socket, Robot robot, Rectangle rect) {
        this.socket = socket;
        this.robot = robot;
        rectangle = rect;
        start();
    }

    public void run() {
        try {
            oos = socket.getOutputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        while (continueLoop) {
            BufferedImage image = robot.createScreenCapture(rectangle);

            try {
                ImageIO.write(image, "jpeg", oos);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class ReceiveEvents extends Thread {
    Socket socket = null;
    Robot robot = null;
    boolean continueLoop = true;

    public ReceiveEvents(Socket socket, Robot robot) {
        this.socket = socket;
        this.robot = robot;
        start();
    }

    public void run() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(socket.getInputStream());
            while (continueLoop) {
                int command = scanner.nextInt();
                switch (command) {
                    case -1:
                        robot.mousePress(scanner.nextInt());
                        break;
                    case -2:
                        robot.mouseRelease(scanner.nextInt());
                        break;
                    case -3:
                        robot.keyPress(scanner.nextInt());
                        break;
                    case -4:
                        robot.keyRelease(scanner.nextInt());
                        break;
                    case -5:
                        robot.mouseMove(scanner.nextInt(), scanner.nextInt());
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
