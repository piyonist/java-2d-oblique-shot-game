package egik_atis;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class Core extends JPanel {

    public static int WIDTH = 1280;
    public static int HEIGHT = 720;
    public int DELAY = 10;

    double aci = 45;
    double mesafe;
    double hiz = 0;
    int tekrar = 1;

    double diameter = 20.0;
    double x = 0;
    double y = HEIGHT - diameter - 1;
    double x0;
    double y0;
    double gravity = 10.0;
    double ilkHiz;
    double yatayHiz;
    double dikeyHiz;
    double time = 0;
    int tekrarSayisi = 1;

    boolean vuruldu = false;
    boolean targetVar = false;
    public int targetX;
    public int targetY;

    public ArrayList<Point2D> kuyrukNoktalari = new ArrayList<Point2D>();

    public Image cannonImage;
    int offset = 5;
    public int angle;
    private int iW, iH;
    private AffineTransform transform;

    Dimension d = new Dimension(WIDTH, HEIGHT);

    @Override
    public Dimension getPreferredSize() {
        return d;
    }

    Random r = new Random();

    public Core(){

        setBackground(Color.WHITE);
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        aci = aci + 1;
                        break;
                    case KeyEvent.VK_DOWN:
                        aci = aci - 1;
                        break;
                    case KeyEvent.VK_LEFT:
                        if(hiz >= 0) hiz = hiz - 1;
                        break;
                    case KeyEvent.VK_RIGHT:
                        hiz = hiz + 1;
                        break;
                    case KeyEvent.VK_SPACE:
                        tekrarSayisi = 1;
                        time = 0;
                        x = 0;
                        y = HEIGHT - diameter - 1;
                        x0 = x;
                        y0 = y;
                        kuyrukNoktalari.clear();

                        yatayHiz = hiz*Math.cos(Math.toRadians(aci));
                        dikeyHiz = hiz*Math.sin(Math.toRadians(aci));
                        angle = (int) aci;
                        timer.start();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        cannonImage = new ImageIcon("src/egik_atis/cannon.png").getImage();
        iW = cannonImage.getWidth(this);
        iH = cannonImage.getHeight(this);
        transform = new AffineTransform();


        x0 = x;
        y0 = y;

        //ilkHiz = Math.sqrt((mesafe*gravity)/Math.sin(Math.toRadians(2*aci)));
        timer2.start();
    }

    Timer timer2 = new Timer(DELAY, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(y+diameter >= HEIGHT){
                tekrarSayisi = 1;
                time = 0;
                x = 0;
                y = HEIGHT - diameter - 1;
                x0 = x;
                y0 = y;
                kuyrukNoktalari.clear();
                timer.stop();
            }

            if(new Rectangle((int)x,(int)y,30,30).intersects(new Rectangle(targetX,HEIGHT-30,30,30))){
                targetVar = false;
                vuruldu = true;
            }

            repaint();
        }
    });

    Timer timer = new Timer(DELAY, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (y <= HEIGHT - diameter) {
                time += 0.1;
                kuyrukNoktalari.add(new Point2D.Double(x + diameter/2,y + diameter/2));
                x = x0 + yatayHiz * time;
                y = y0 - (dikeyHiz * time - (gravity / 2) * time * time);
            }
/*
            if(y + diameter >= HEIGHT && tekrarSayisi < tekrar){
                tekrarSayisi++;
                time = 0;
                x = 0;
                y = HEIGHT - diameter - 1;
                x0 = x;
                y0 = y;
                kuyrukNoktalari.clear();
            }

 */

            repaint();
        }
    });


    private void doDrawing(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(targetVar == false){

            targetX = Math.abs(r.nextInt()) % WIDTH;
            //targetY = Math.abs(r.nextInt()) % HEIGHT;
            if(vuruldu == true){
                targetX = Math.abs(r.nextInt()) % WIDTH;
                //targetY = Math.abs(r.nextInt()) % HEIGHT;
            }
            targetVar = true;
        }


        g2d.setColor(Color.BLUE);
        g2d.fillRect(targetX,HEIGHT-30,30,30);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Aci: ",30,30);
        g2d.drawString(String.valueOf(aci),60,30);
        g2d.drawString("Hiz: ",30,80);
        g2d.drawString(String.valueOf(hiz),60,80);


        g2d.setColor(Color.RED);
        g2d.fill(new Ellipse2D.Double(x, y, diameter, diameter));

        g2d.setColor(Color.BLACK);
        GeneralPath yol = new GeneralPath();
        yol.moveTo(diameter/2, HEIGHT - diameter/2);
        for(int i = 0; i < kuyrukNoktalari.size(); i++){
            yol.lineTo(kuyrukNoktalari.get(i).getX(), kuyrukNoktalari.get(i).getY());
        }
        g2d.draw(yol);

        transform.setToTranslation(0,HEIGHT-cannonImage.getHeight(this)+10+aci);
        transform.rotate(Math.toRadians(-aci), 0, iH);
        g2d.drawImage(cannonImage,transform,this);
        g2d.dispose();
        Toolkit.getDefaultToolkit().sync();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}
