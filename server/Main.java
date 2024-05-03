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
    static String port = "4907";
    JButton SUBMIT;
    JPanel panel;
    JLabel label1, label2;
    JTextField text1, text2;
    String value1;

    Main() {
        label1 = new JLabel();
        label1.setText("Set Password");
        text1 = new JTextField(15);

        this.setLayout(new BorderLayout());

        SUBMIT = new JButton("SUBMIT");
        SUBMIT.addActionListener(this);

        panel = new JPanel(new GridLayout(2, 1));
        panel.add(label1);
        panel.add(text1);
        panel.add(SUBMIT);

        add(panel, BorderLayout.CENTER);

        setTitle("Set Password to connect to the Client");
        setSize(300, 80);
        setLocation(500, 300);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        value1 = text1.getText();
        dispose();
        new InitConnection(Integer.parseInt(port), value1);
    }

    public static void main(String[] args) {
        new Main();
    }
}

class InitConnection {
    ServerSocket socket = null;
    DataInputStream password = null;
    DataOutputStream verify = null;
    String width = "";
    String height = "";

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
    Dimension screenSize = null;

    public ReceiveEvents(Socket socket, Robot robot) {
        this.socket = socket;
        this.robot = robot;
        this.screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        start();
    }

    public void run() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(socket.getInputStream());
            while (continueLoop) {
                int command = scanner.nextInt();
                switch (command) {
                    case -1: // Mouse press
                        int button = scanner.nextInt();
                        robot.mousePress(button);
                        break;
                    case -2: // Mouse release
                        int releaseButton = scanner.nextInt();
                        robot.mouseRelease(releaseButton);
                        break;
                    case -3: // Key press
                        int keyCode = scanner.nextInt();
                        robot.keyPress(keyCode);
                        break;
                    case -4: // Key release
                        int releaseKeyCode = scanner.nextInt();
                        robot.keyRelease(releaseKeyCode);
                        break;
                    case -5: // Mouse move
                        int x = scanner.nextInt();
                        int y = scanner.nextInt();
                        // Scale mouse coordinates based on server screen size
                        double scaleX = (double) screenSize.width / x;
                        double scaleY = (double) screenSize.height / y;
                        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                        int newX = (int) (mouseLocation.x / scaleX);
                        int newY = (int) (mouseLocation.y / scaleY);
                        robot.mouseMove(newX, newY);
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
