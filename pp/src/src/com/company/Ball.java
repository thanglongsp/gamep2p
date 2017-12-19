package src.com.company;

public class Ball extends Thread {
    private int x;			 // Tọa độ x của bóng
    private int y;			 // Tọa độ y của bóng
    private double xv;                   // Vận tốc theo chiều x
    private double yv;                      // Vận tốc theo chiều y
    private int radius;                     // Bán kính của bóng
    private int   HEIGHT;                   // Chiều cao của cửa sổ
    private int   WIDTH;                // Chiều rộng cửa sổ

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(true){
            move();
            try {
                sleep(10);                         // ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }}

    }

    public Ball(int x, int y, double xv, double yv, int radius, int WIDTH, int HEIGHT) {
        super();
        this.x = x;
        this.y = y;
        this.xv = xv;
        this.yv = yv;
        this.radius = radius;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
    }


    public void move(){
        if(x + xv > (WIDTH-radius) - 7){    // - Calibrate the screen layer - //
            x= (WIDTH-radius)-7;            // - set the position of the ball
            xv = xv * -1;                   // - set the velocity of the ball

        }

        if(x + xv < 9){                     // - Calibrate the screen layer - //
            x = 9;
            xv = xv *-1;
        }

        if(y + yv < radius/2+7){            // - Calibrate the screen layer - //
            y = 29;
            yv = yv * -1;
        }

        if(y + yv > (HEIGHT - radius) - 6) // - Calibrate the screen layer - //
        {
            y = (HEIGHT-radius)-6;
            yv = yv * -1;

        }
        x += xv;
        y += yv;

    }

    ///////////////////////////
    // - Getters & Setters - //
    ///////////////////////////

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public double getXv() {
        return xv;
    }
    public void setXv(double xv) {
        this.xv = xv;
    }
    public double getYv() {
        return yv;
    }
    public void setYv(double yv) {
        this.yv = yv;
    }
    public int getRadius() {
        return radius;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }

}
