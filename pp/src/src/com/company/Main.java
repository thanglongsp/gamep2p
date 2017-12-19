package src.com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class Main extends JFrame implements KeyListener, Runnable {

    private static final long serialVersionUID = 1L;

    private static Image image;
    private Graphics g;
    private static final String TITLE = "ping-pong::network_game";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 460;
    private String servername = "servername", clientname = "clientname";

    public Main() {

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        this.setVisible(true);
        this.setTitle(TITLE);
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        this.addKeyListener(this);
    }

    public static void main(String[] args) throws IOException {
        image = ImageIO.read(new File("C:\\Users\\thanglongsp\\Documents\\NetBeansProjects\\pp\\src\\src\\background_pong.png"));
        Main newT = new Main();
        newT.run();
    }

    private Image createImage() {

        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = bufferedImage.createGraphics();
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.drawImage(image, 0, 0, this);
        return bufferedImage;

    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(createImage(), 0, 20, this);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        int keyCode = arg0.getKeyCode();
        String portAdd = null;
        String ipAdd = null;

        if (keyCode == KeyEvent.VK_S) {

            portAdd = JOptionPane.showInputDialog(null, "ex. 1024", "Enter server port:", 1);

            if (portAdd != null) {
                if (!isPort(portAdd)) {
                    JOptionPane.showMessageDialog(null, "Enter port number as a right format!", "Error!", 1);
                } else {

                    servername = JOptionPane.showInputDialog(null, "Nick name:", "Enter server name:", 1);
                    servername += "";

                    if (servername.length() > 10 || servername.length() < 3 || servername.startsWith("null")) {
                        JOptionPane.showMessageDialog(null, "Enter name as a right format!", "Error!", 1);

                    } else {

                        GameServer myServer = new GameServer(servername, portAdd);
                        Thread myServerT = new Thread(myServer);
                        myServerT.start();
                        this.setVisible(false);
                    }
                }
            }
        }

        if (keyCode == KeyEvent.VK_C) {

            ipAdd = JOptionPane.showInputDialog(null, "ex. 127.0.0.1", "Enter server ip:", 1);

            if (ipAdd != null) {

                if (!isIPAddress(ipAdd)) {
                    JOptionPane.showMessageDialog(null, "Enter ip number as a right format!", "Enter server ip:", 1);
                } else {

                    portAdd = JOptionPane.showInputDialog(null, "ex. 1024", "Enter server port number:", 1);

                    if (portAdd != null) {
                        if (!isPort(portAdd)) {
                            JOptionPane.showMessageDialog(null, "Enter port number as a right format!", "Error!:", 1);
                        } else {
                            clientname = JOptionPane.showInputDialog(null, "Nick name:", "Enter server name:", 1);
                            clientname += "";
                            if (clientname.length() > 10 || clientname.length() < 3 || clientname.startsWith("null")) {
                                JOptionPane.showMessageDialog(null, "Enter name as a right format!", "Error!", 1);
                            } // - Start a Client - //
                            else {
                                GameClient myClient = new GameClient(clientname, portAdd, ipAdd);
                                Thread myClientT = new Thread(myClient);
                                myClientT.start();
                                this.setVisible(false);
                            }
                        }
                    }
                }
            }
        }//<--end_of_the_key_cond.-->//
    }//<--end_of_the_switch-->//

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    // - Check PORT number type- //
    private boolean isPort(String str) {
        Pattern pPattern = Pattern.compile("\\d{1,4}");
        return pPattern.matcher(str).matches();
    }

    // - Check IP address type- //
    private boolean isIPAddress(String str) {
        Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        return ipPattern.matcher(str).matches();
    }

}
