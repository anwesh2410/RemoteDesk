import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.zip.*;
import java.beans.PropertyVetoException;
import javax.imageio.ImageIO;


public class Main {
    static String port = "4907";

    public static void main(String args[]) {
        String ip = JOptionPane.showInputDialog("Please enter server ip");
        new Main().initialize(ip, Integer.parseInt(port));
    }

    public void initialize(String ip, int port) {
        try {
            Socket sc = new Socket(ip, port);
            System.out.println("Connecting to the Server");
            Authenticate frame1 = new Authenticate(sc);

            frame1.setSize(300, 80);
            frame1.setLocation(500, 300);
            frame1.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class Authenticate extends JFrame implements ActionListener {
    private Socket cSocket = null;
    DataOutputStream psswrchk = null;
    DataInputStream verification = null;
    String verify = "";
    JButton SUBMIT;
    JPanel panel;
    JLabel label, label1;
    final JTextField text1;

    Authenticate(Socket cSocket) {
        label1 = new JLabel();
        label1.setText("Password");
        text1 = new JTextField(15);
        this.cSocket = cSocket;

        label = new JLabel();
        label.setText("");
        this.setLayout(new BorderLayout());

        SUBMIT = new JButton("SUBMIT");

        panel = new JPanel(new GridLayout(2, 1));
        panel.add(label1);
        panel.add(text1);
        panel.add(label);
        panel.add(SUBMIT);
        add(panel, BorderLayout.CENTER);
        SUBMIT.addActionListener(this);
        setTitle("LOGIN FORM");
    }

    public void actionPerformed(ActionEvent ae) {
        String value1 = text1.getText();

        try {
            psswrchk = new DataOutputStream(cSocket.getOutputStream());
            verification = new DataInputStream(cSocket.getInputStream());
            psswrchk.writeUTF(value1);
            verify = verification.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (verify.equals("valid")) {
            String width = "", height = "";
            try {
                width = verification.readUTF();
                height = verification.readUTF();

            } catch (IOException e) {
                e.printStackTrace();
            }
            CreateFrame abc = new CreateFrame(cSocket, width, height);
            dispose();
        } else {
            System.out.println("enter the valid password");
            JOptionPane.showMessageDialog(this, "Incorrect  password", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }

    }

}

enum Commands {
    PRESS_MOUSE(-1),
    RELEASE_MOUSE(-2),
    PRESS_KEY(-3),
    RELEASE_KEY(-4),
    MOVE_MOUSE(-5);

    private int abbrev;

    Commands(int abbrev) {
        this.abbrev = abbrev;
    }

    public int getAbbrev() {
        return abbrev;
    }
}

class CreateFrame extends Thread {
    String width = "", height = "";
    private JFrame frame = new JFrame();
    private JDesktopPane desktop = new JDesktopPane();
    private Socket cSocket = null;
    private JInternalFrame interFrame = new JInternalFrame("Server Screen", true, true, true);
    private JPanel cPanel = new JPanel();

    public CreateFrame(Socket cSocket, String width, String height) {

        this.width = width;
        this.height = height;
        this.cSocket = cSocket;
        start();
    }

    public void drawGUI() {
        frame.add(desktop, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        interFrame.setLayout(new BorderLayout());
        interFrame.getContentPane().add(cPanel, BorderLayout.CENTER);
        interFrame.setSize(100, 100);
        desktop.add(interFrame);

        try {
            interFrame.setMaximum(true);
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }

        cPanel.setFocusable(true);
        interFrame.setVisible(true);

    }

    public void run() {
        InputStream in = null;
        drawGUI();

        try {
            in = cSocket.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        new ReceiveScreen(in, cPanel);
        new SendEvents(cSocket, cPanel, width, height);
    }
}

class ReceiveScreen extends Thread {
    private ObjectInputStream cObjectInputStream = null;
    private JPanel cPanel = null;
    private boolean continueLoop = true;
    InputStream oin = null;
    Image image1 = null;

    public ReceiveScreen(InputStream in, JPanel p) {
        oin = in;
        cPanel = p;
        start();
    }

    public void run() {
        try {
            while (continueLoop) {
                byte[] bytes = new byte[1024 * 1024];
                int count = 0;
                do {
                    count += oin.read(bytes, count, bytes.length - count);
                } while (!(count > 4 && bytes[count - 2] == (byte) -1 && bytes[count - 1] == (byte) -39));

                image1 = ImageIO.read(new ByteArrayInputStream(bytes));
                image1 = image1.getScaledInstance(cPanel.getWidth(), cPanel.getHeight(), Image.SCALE_FAST);

                Graphics graphics = cPanel.getGraphics();
                graphics.drawImage(image1, 0, 0, cPanel.getWidth(), cPanel.getHeight(), cPanel);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

class SendEvents implements KeyListener, MouseMotionListener, MouseListener {
    private Socket cSocket = null;
    private JPanel cPanel = null;
    private PrintWriter writer = null;
    String width = "", height = "";
    double w;
    double h;

    SendEvents(Socket s, JPanel p, String width, String height) {
        cSocket = s;
        cPanel = p;
        this.width = width;
        this.height = height;
        w = Double.valueOf(width.trim()).doubleValue();
        h = Double.valueOf(width.trim()).doubleValue();

        cPanel.addKeyListener(this);
        cPanel.addMouseListener(this);
        cPanel.addMouseMotionListener(this);

        try {
            writer = new PrintWriter(cSocket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        double xScale = (double) w / cPanel.getWidth();
        double yScale = (double) h / cPanel.getHeight();
        writer.println(Commands.MOVE_MOUSE.getAbbrev());
        writer.println((int) (e.getX() * xScale));
        writer.println((int) (e.getY() * yScale));
        writer.flush();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        writer.println(Commands.PRESS_MOUSE.getAbbrev());
        int button = e.getButton();
        int xButton = 16;
        if (button == 3) {
            xButton = 4;
        }
        writer.println(xButton);
        writer.flush();
    }

    public void mouseReleased(MouseEvent e) {
        writer.println(Commands.RELEASE_MOUSE.getAbbrev());
        int button = e.getButton();
        int xButton = 16;
        if (button == 3) {
            xButton = 4;
        }
        writer.println(xButton);
        writer.flush();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        writer.println(Commands.PRESS_KEY.getAbbrev());
        writer.println(e.getKeyCode());
        writer.flush();
    }

    public void keyReleased(KeyEvent e) {
        writer.println(Commands.RELEASE_KEY.getAbbrev());
        writer.println(e.getKeyCode());
        writer.flush();
    }
}