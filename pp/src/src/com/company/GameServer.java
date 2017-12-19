package src.com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class GameServer extends JFrame implements KeyListener,Runnable,WindowListener {
    private static final long serialVersionUID = 1L;
    
    private static final String TITLE  = "server";
    private static final int    WIDTH  = 800;		  
    private static final int    HEIGHT = 460;		 

    boolean isRunning = false;
    boolean check = true;
    boolean initgame = false;
    
    Ball movingBALL;
    private PlayerServer playerS;
    private PlayerClient playerC;

    private int ballVEL   = 4;		// - Vận tốc bóng- //
    private int barRS      = 30;	
    private int playerHS   = 120; 	
    private int max_Score = 9; 		
    private int mPLAYERS   = 20; 		
    private boolean Restart   = false;  // - Check Restart - //
    private boolean restartON = false;

    private static Socket clientSoc  = null;
    private static ServerSocket serverSoc  = null;
    private int portAdd;

    private Graphics g;
    private Font sFont = new Font("TimesRoman",Font.BOLD,90);
    private Font mFont = new Font("TimesRoman",Font.BOLD,50);
    private Font nFont = new Font("TimesRoman",Font.BOLD,32);
    private Font rFont = new Font("TimesRoman",Font.BOLD,18);
    private String[] message;	
    private Thread movB;

    public GameServer(String servername, String portAdd){

        playerS = new PlayerServer();
        playerC = new PlayerClient("");
        playerS.setName(servername);

        this.portAdd = Integer.parseInt(portAdd);
        this.isRunning = true;
        this.setTitle(TITLE + "::port number["+portAdd+"]");
        this.setSize(WIDTH,HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        movingBALL = new Ball(playerS.getBallx(),playerS.getBally(),ballVEL,ballVEL,45,WIDTH,HEIGHT);
        addKeyListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        // Server Socket //
        try {
            serverSoc = new ServerSocket(portAdd);
            System.out.println("Đang đợi người chơi kết nối...");
            playerS.setImessage("Waiting ...");
            clientSoc = serverSoc.accept();

            System.out.println("Connected a player...");

            if(clientSoc.isConnected()){ // - If connected a player start to loop - //

                movB = new Thread(movingBALL);
                boolean notchecked = true; 
                while(true){

                    if(playerS.getScoreP() >= max_Score || playerS.getScoreS()>= max_Score && Restart==false){

                        if(playerS.getScoreS()>playerS.getScoreP()){
                            playerS.setOmessage("Won               Loss-Play Again: Nhấn N để thoát");
                            playerS.setImessage("Won               Loss-Play again? ");
                            Restart = true;
                        }
                        else{
                            playerS.setImessage("Loss              Won-Play Again: Nhấn N để thoát");
                            playerS.setOmessage("Loss              Won-Play Again: Nhấn N để thoát");
                            Restart = true;
                        }
                        movB.suspend();	// - Stop the ball object - //
                    }


                    // - Check -> is client ready... //
                    if(playerC.ok && notchecked){
                        playerS.setImessage("");
                        movB.start();
                        notchecked = false;
                    }

                    // - Update Ball - //
                    updateBall();

                    // - Creating Streams - //
                    ObjectInputStream getObj = new ObjectInputStream(clientSoc.getInputStream());
                    playerC = (PlayerClient) getObj.readObject();
                    getObj = null;

                    // - Send Object to Client - //
                    ObjectOutputStream sendObj = new ObjectOutputStream(clientSoc.getOutputStream());
                    sendObj.writeObject(playerS);
                    sendObj = null;

                    // - Check Restart Game - //
                    if(restartON){

                        if(playerC.restart){
                            playerS.setScoreP(0);
                            playerS.setScoreS(0);
                            playerS.setOmessage("");
                            playerS.setImessage("");
                            Restart = false;
                            playerS.setRestart(false);
                            playerS.setBallx(380);
                            playerS.setBally(230);
                            movingBALL.setX(380);
                            movingBALL.setY(230);
                            movB.resume();
                            restartON = false;
                        }
                    }
                    // - Repaint - //
                    repaint();
                }
            }
            else{
                System.out.println("Disconnected...");
            }
        }
        catch (Exception e) {System.out.println(e);}
    }

    private Image createImage() throws IOException{
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
        g.fillRect(playerS.getX(), playerS.getY(), barRS, playerHS);
        g.setColor(Color.RED);
        g.fillRect(playerC.getX(), playerC.getY(), barRS, playerHS);

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
        try {
            g.drawImage(createImage(), 0, 0, this);
        } catch (IOException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        playerC.ok = true;
    }

    public void updateBall(){

        // - Checking collisions - //
        checkCol();

        // - update the ball - //
        playerS.setBallx(movingBALL.getX());
        playerS.setBally(movingBALL.getY());

    }

    // - Move bar to Up - //
    public void playerUP(){
        if(playerS.getY() - mPLAYERS > playerHS/2-10){
            playerS.setY(playerS.getY()-mPLAYERS);
        }
    }

    // - Move bar to Down - //
    public void playerDOWN(){
        if(playerS.getY() + mPLAYERS < HEIGHT - playerHS - 30){
            playerS.setY(playerS.getY()+mPLAYERS);
        }
    }
    
    public void checkCol(){
        // - Checking ball side, when a player got a score check -> false * if ball behind of the players check -> true
        if(playerS.getBallx() < playerC.getX() && playerS.getBallx() > playerS.getX()){
            check = true;
        }

        // - Server Player Score - //
        if(playerS.getBallx()>playerC.getX() && check){

            playerS.setScoreS(playerS.getScoreS()+1);
            movingBALL.setX(playerS.getX());
            movingBALL.setY(playerS.getY());
            //movingBALL.setXv(movingBALL.getXv()*-1);
            check = false;
        }

        // - Client Player Score - //
        else if (playerS.getBallx()<=playerS.getX() && check){

            playerS.setScoreP(playerS.getScoreP()+1);
            movingBALL.setX(playerC.getX());
            movingBALL.setY(playerC.getY());
            //movingBALL.setXv(movingBALL.getXv()*-1);
            check = false;

        }


        // - Checking Server Player Bar - //
        if(movingBALL.getX()<=playerS.getX()+barRS && movingBALL.getY()+movingBALL.getRadius()>= playerS.getY() && movingBALL.getY()<=playerS.getY()+playerHS ){
            movingBALL.setX(playerS.getX()+barRS);
            playerS.setBallx(playerS.getX()+barRS);
            movingBALL.setXv(movingBALL.getXv()*-1);
        }


        // - Checking Client Player Bar - //
        if(movingBALL.getX()+movingBALL.getRadius()>=playerC.getX() && movingBALL.getY() + movingBALL.getRadius() >= playerC.getY() && movingBALL.getY()<=playerC.getY()+playerHS ){
            movingBALL.setX(playerC.getX()-movingBALL.getRadius());
            playerS.setBallx(playerC.getX()-movingBALL.getRadius());
            movingBALL.setXv(movingBALL.getXv()*-1);
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        // TODO Auto-generated method stub
        int keycode = arg0.getKeyCode();
        if(keycode == KeyEvent.VK_UP){
            playerUP();
            repaint();
        }
        if(keycode == KeyEvent.VK_DOWN){
            playerDOWN();
            repaint();
        }
        if(Restart == true){
            restartON = true;
            playerS.setRestart(true);
        }

        if(keycode == KeyEvent.VK_N || keycode == KeyEvent.VK_ESCAPE && Restart == true){
            try {
                this.setVisible(false);
                serverSoc.close();
                System.exit(EXIT_ON_CLOSE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    @Override
    public void windowOpened(WindowEvent e) {

    }
    @SuppressWarnings("deprecation")
    @Override
    public void windowClosing(WindowEvent e) {

        Thread.currentThread().stop();
        this.setVisible(false);
        try {
            serverSoc.close();
        } catch (IOException er) {
            er.printStackTrace();
        }
        System.exit(1);
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
