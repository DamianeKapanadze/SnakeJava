import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

    static final int Screen_Width = 1200;
    static final int Screen_Height = 800;
    static final int Unit_Size = 25;
    static final int Game_Untis = Screen_Width*Screen_Height/Unit_Size;
    static final int Delay = 75;
    final int x[] = new int[Game_Untis];
    final int y[] = new int[Game_Untis];
    int[][]  path = hamiltonianCycle(Screen_Width/Unit_Size, Screen_Height/Unit_Size);
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX, appleY;
    char direction = 'd';
    boolean running = false;
    boolean ai = true;
    Timer timer;
    Random random;

    public static int[][] hamiltonianCycle(int width, int height){
        int[][] x = new int[height][width];
        int i = 1;
        int j = 0;
        for(int k = 0; k < height * width; k++){
            x[i][j] = k;
            
            if(j % 2 == 0 && i == height-1 && j < width-1) j++;
            else if(j % 2 == 1 && i == 1 && j < width-1) j++;

            else if(i == 1 && j == width-1)  i--;
            else if(i == 0) j--;
            
            else if(j % 2 == 0 && i<height-1) i++;
            else if(j % 2 == 1 && i > 1) i--;
        }
        return x;
    }

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(Screen_Width, Screen_Height));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        bodyParts = 6;
        applesEaten = 0;
        direction = 'd';
        x[0] = 0;
        y[0] = 25;
        newApple();
        running = true;
		timer = new Timer(Delay,this);
        timer.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if(running){
            /*
            for(int i = 0; i < Screen_Height/Unit_Size; i++){
                g.drawLine(i*Unit_Size, 0, i*Unit_Size, Screen_Height);
                g.drawLine(0, i*Unit_Size, Screen_Width, i*Unit_Size);
            }*/

            g.setColor(Color.red);
            g.fillRect(appleX, appleY, Unit_Size, Unit_Size);

            for(int i = 0; i < bodyParts; i++){
                if(i == 0){
                    g.setColor(Color.GREEN);
                }else{
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], Unit_Size, Unit_Size);
            }
            //write score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+ applesEaten, (Screen_Width - metrics.stringWidth("Score: "+ applesEaten))/2, g.getFont().getSize());
        }else{
            gameOver(g);
        }
    }

    public void newApple(){
        appleX = random.nextInt((int)(Screen_Width/Unit_Size)) * Unit_Size;
        appleY = random.nextInt((int)(Screen_Height/Unit_Size)) * Unit_Size;
    }

    public void move(){
        for(int i = bodyParts; i > 0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction){
            case 'u':
                y[0] = y[0] - Unit_Size;
                break;
            case 'd':
                y[0] = y[0] + Unit_Size;
                break;
            case 'r':
                x[0] = x[0] + Unit_Size;
                break;
            case 'l':
                x[0] = x[0] - Unit_Size;
                break;
        }
    }
    public void moveAI(){
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 20; j++) {
                System.out.print(path[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(y[0] + " " + x[0]);
        System.out.println(path[y[0]][x[0]] + " " + path[y[0] + 1][x[0]]);

        if (path[y[0]][x[0]] + 1 == path[y[0] - 1][x[0]]){
            direction = 'u';
        }
        else if (path[y[0]][x[0]] + 1 == path[y[0] + 1][x[0]]){
            direction = 'd';
        }
        else if (path[y[0]][x[0]] + 1 == path[y[0]][x[0] + 1]){
            direction = 'r';
        }
        else if (path[y[0]][x[0]] + 1 == path[y[0]][x[0] - 1]){
            direction = 'l';
         }
        move();
    }
    
    public void checkApples(){
        if(x[0] == appleX && y[0] == appleY){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollision(){
        // check snake collision
        for(int i = bodyParts; i > 0; i--){
            if(x[0] == x[i] && y[0] == y[i]){
                running = false;
            }
        }

        // check left border
        if(x[0] < 0 || x[0] > Screen_Width || y[0] < 0 || y[0] > Screen_Height){
            running = false;
        }

        if(!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics g){
        //Write Game over
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (Screen_Width - metrics.stringWidth("Game Over"))/2, Screen_Height/2);

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+ applesEaten, (Screen_Width - metrics1.stringWidth("Score: "+ applesEaten))/2, g.getFont().getSize());

        //add button to restart
        
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running && ai){
            moveAI();
            checkApples();
            checkCollision();
        }
        else if(running && !ai){
            move();
            checkApples();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction != 'r')
                        direction = 'l';
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'l')
                        direction = 'r';
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'u')
                        direction = 'd';
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'd')
                        direction = 'u';
                    break;
                case KeyEvent.VK_R:
                    if(!running){
                        timer.stop();
                        startGame();
                    }
            }
        }
    }
}