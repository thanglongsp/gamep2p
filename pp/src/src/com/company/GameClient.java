package src.com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient extends JFrame implements KeyListener,Runnable,WindowListener {
    private static final long serialVersionUID = 1L;

    // - Frame - //
    private static final String TITLE  = "client";
    private static final int    WIDTH  = 800;	
    private static final int    HEIGHT = 460;		  
    boolean isRunning = false;


    private PlayerServer playerS;
    private PlayerClient playerC;
    private int barR      = 30;		
    private int playerH   = 120; 	
    private int mPLAYER   = 5; 		

    private static Socket clientSoc;
    private int portAdd;
    private String ipAdd;
    private boolean reset = false;
    private int countS = 0;


    private Graphics g;
    private Font sFont = new Font("TimesRoman",Font.BOLD,90);
    private Font mFont = new Font("TimesRoman",Font.BOLD,50);
    private Font nFont = new Font("TimesRoman",Font.BOLD,32);
    private Font rFont = new Font("TimesRoman",Font.BOLD,18);
    private String[] message;	



    public GameClient(String clientname, String portAdd, String ipAdd){

    
        playerS = new PlayerServer();
        playerC = new PlayerClient(clientname);
        //playerS.setName(clientname);


        this.ipAdd = ipAdd;
        this.portAdd = Integer.parseInt(portAdd);
        this.isRunning = true;

        // - Frame - //
        this.setTitle(TITLE);
        this.setSize(WIDTH,HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        addKeyListener(this);
        //addWindowListener(this);
        // -
    }


    @Override
    public void run() {
        // Server Socket //
        try {

            System.out.println("Đang tìm máy chủ..."+ipAdd+":"+portAdd);
            clientSoc = new Socket(ipAdd, portAdd);
            System.out.println("Đã kết nối với máy chủ...");

            if(clientSoc.isConnected()){
                System.out.println("Connected!");
                while(true){
                    ObjectOutputStream sendObj = new ObjectOutputStream(clientSoc.getOutputStream());
                    sendObj.writeObject(playerC);
                    sendObj = null;

                    ObjectInputStream getObj = new ObjectInputStream(clientSoc.getInputStream());
                    playerS = (PlayerServer) getObj.readObject();
                    getObj = null;

                    if(reset){

                        if(countS>5){
                            playerC.restart = false;
                            reset = false;
                            countS = 0;
                        }
                    }
                    countS++;
                    repaint();
                }

            }
            else{
                System.out.println("Disconnected...");
            }


        }
        catch (Exception e) {System.out.println(e);}

    }

    private Image createImage(){

        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = bufferedImage.createGraphics();

        // - Table - //
        g.setColor(Color.blue);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // - Lines - //
        g.setColor(Color.black);
        g.fillRect(WIDTH/2-5, 0, 5, HEIGHT);
        g.fillRect(WIDTH/2+5, 0, 5, HEIGHT);

        // - Score - //
        g.setColor(Color.green);
        g.setFont(sFont);
        g.drawString(""+playerS.getScoreS(), WIDTH/2-60, 120);
        g.drawString(""+playerS.getScoreP(), WIDTH/2+15, 120);

        // - Player Names - //
        g.setFont(nFont);
        g.setColor(Color.green);
        g.drawString(playerS.getName(),WIDTH/10,HEIGHT-20);
        g.drawString(playerC.getName(),600,HEIGHT-20);


        // - Player's Bar - //
        g.setColor(Color.RED);
        g.fillRect(playerS.getX(), playerS.getY(), barR, playerH);
        g.setColor(Color.RED);
        g.fillRect(playerC.getX(), playerC.getY(), barR, playerH);

        // - Ball - //
        g.setColor(Color.YELLOW);
        g.fillOval(playerS.getBallx(), playerS.getBally(), 45, 45);
        g.setColor(Color.YELLOW);
        g.fillOval(playerS.getBallx()+5, playerS.getBally()+5, 45-10, 45-10);

        // - Message - //
        message = playerS.getImessage().split("-");
        g.setFont(mFont);
        g.setColor(Color.white);
        if(message.length!=0){
            g.drawString(message[0],WIDTH/4-31,HEIGHT/2+38);
            if(message.length>1){
                if(message[1].length()>6){
                    g.setFont(rFont);
                    g.setColor(new Color(228,38,36));
                    g.drawString(message[1],WIDTH/4-31,HEIGHT/2+100);
                }
            }
        }
        return bufferedImage;
    }

    public void paint(Graphics g){
        g.drawImage(createImage(), 0, 0, this);
        playerC.ok = true;
    }

    public void playerC_UP(){
        if(playerC.getY() - mPLAYER > playerH/2-10){

            playerC.setY(playerC.getY()-mPLAYER);
        }
    }

    public void playerC_DOWN(){
        if(playerC.getY() + mPLAYER < HEIGHT - playerH - 30){

            playerC.setY(playerC.getY()+mPLAYER);
        }
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        int keycode = arg0.getKeyCode();
        if(keycode == KeyEvent.VK_W){
            playerC_UP();
            repaint();
        }
        if(keycode == KeyEvent.VK_S){
            playerC_DOWN();
            repaint();
        }
        
        if(playerS.isRestart()){
            playerC.restart = true;
            reset = true;
        }
        if(keycode == KeyEvent.VK_ESCAPE || keycode == KeyEvent.VK_N && playerS.isRestart()){
            try {
                this.setVisible(false);
                clientSoc.close();
                System.exit(EXIT_ON_CLOSE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        Thread.currentThread().stop();
        this.setVisible(false);
        try {
            clientSoc.close();
        } catch (IOException er) {
            er.printStackTrace();
        }

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
