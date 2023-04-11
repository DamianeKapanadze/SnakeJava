import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import javax.swing.Timer;


public class GamePanel extends JPanel implements ActionListener{

    static final int Screen_Width = 200;
    static final int Screen_Height = 200;
    static final int Unit_Size = 25;
    static final int Game_Untis = Screen_Width*Screen_Height/Unit_Size;
    static final int Delay = 200;
    final int x[] = new int[Game_Untis];
    final int y[] = new int[Game_Untis];
    //int[][]  path = hamiltonianCycle(Screen_Width/Unit_Size, Screen_Height/Unit_Size);
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX, appleY;
    char direction = 'd';
    boolean running = false;
    boolean survival = true;
    boolean ai = true;
    Timer timer;
    Random random;

    //Crrate fised Hamiltonian cycle
    public static int[][] hamiltonianCycle(int width, int height){
        int[][] x = new int[height+2][width+2];
        int area = height*width;
        height+=2;
        width += 2;
        int i = 2;
        int j = 1;
        for(int k = 0; k < area; k++){
            x[i][j] = k;
            
            if(j % 2 == 1 && i == height-2 && j < width-2) j++;
            else if(j % 2 == 0 && i == 2 && j < width-2) j++;

            else if(i == 2 && j == width-2)  i--;
            else if(i == 1) j--;
            
            else if(j % 2 == 1 && i < height-2) i++;
            else if(j % 2 == 0 && i > 2) i--;
        }
        return x;
    }
    
    // convert the snake screen to maze with sankes body as a wall
    public static char[] getDir(int [] x, int [] y, int parts, int startx, int starty, int finishx, int finishy, boolean survival){
        int width = Screen_Width / Unit_Size;
        int height = Screen_Height / Unit_Size;
        startx /= Unit_Size;
        starty /= Unit_Size;
        finishx /= Unit_Size;
        finishy /= Unit_Size;
        int[][] maze= new int [height][width];
        char dir[] = new char [Game_Untis];
        
        for(int i = 1; i < parts; i++){
            //System.out.println(x[i] + " " + y[i]);
            if( y[i] >= 0 && y[i] < Screen_Height  && x[i] >= 0 && x[i] < Screen_Width){
                maze[y[i]/Unit_Size][x[i]/Unit_Size] = 5;
            }
        }
        
        Point[] path;
        try{
            maze[finishy][finishx] = 9;
            maze[starty][startx] = 1;
            if(!survival){
                path = new Maze().findPath(maze, new Point(finishx, finishy), new Point(startx, starty));
                // https://codereview.stackexchange.com/questions/205783/java-breadth-first-search-for-finding-the-shortest-path-on-a-2d-grid-with-obstac
            }else{
                path = new Maze().findPath(maze, new Point(x[x.length-1], y[y.length-1]), new Point(startx, starty));
            }
            
            int AIx[] = new int [Game_Untis];
            int AIy[] = new int [Game_Untis];
            int k = 0;
            if(path != null){
                for (int i = 0; i < path.length; i++ ) {
                    AIx[k] = path[i].x; 
                    AIy[k] = path[i].y;
                    k++;
                }
            }
            for(int i = 0; i < k-1; i++){
                if(AIx[i] == AIx[i+1] && AIy[i] + 1 == AIy[i+1]){
                    dir[i] = 'd';
                }else if(AIx[i] == AIx[i+1] && AIy[i] - 1 == AIy[i+1]){
                    dir[i] = 'u';
                }else if(AIx[i] + 1 == AIx[i+1] && AIy[i] == AIy[i+1]){
                    dir[i] = 'r';
                }else if (AIx[i] - 1 == AIx[i+1] && AIy[i] == AIy[i+1]){
                    dir[i] = 'l';
                }
            }

        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Snek head out bound, Snek ded :(");
        }



        System.out.println();
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
        
        return dir;
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
        for(int i = bodyParts; i > 0; i--){
            if(appleX == x[i] && appleY == y[i]){
                newApple();
            }   
        }
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
        char [] dir = getDir(x, y, bodyParts, x[0], y[0], appleX, appleY, survival);
        for(int i =0; i < dir.length; i++){
            direction = dir[i];
            System.out.print(direction);
            move();
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*                      Don't need this
    public void moveAI(){ //Hamiltonian fixed path
        
        int SnekX = x[0]/Unit_Size +1;
        int SnekY = y[0]/Unit_Size + 1;
        
        //Debugging

        for (int i = 0; i < 2+Screen_Height/Unit_Size; i++) {
            for (int j = 0; j < 2+Screen_Width/Unit_Size; j++) {
                if(i == SnekY && j == SnekX)
                    System.out.print("H ");

                else System.out.print(path[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println(SnekY + " " + SnekX + " " + direction);

        //System.out.println(path[y[0]/Unit_Size][x[0]/Unit_Size] + " " + path[y[0]/Unit_Size][x[0]/Unit_Size+1]);

        // mokled aq sadgac gadis out of bopunds, if-ebia gasasworebeli
        if (path[SnekY][SnekX] + 1 == path[SnekY - 1][SnekX] && direction != 'u'){
            direction = 'u';
        }
        else if (path[SnekY][SnekX] + 1 == path[SnekY + 1][SnekX] && direction != 'd'){
            direction = 'd';
        }
        else if (path[SnekY][SnekX] + 1 == path[SnekY][SnekX + 1] && direction != 'r'){
            direction = 'r';
        }
        else if (path[SnekY][SnekX] + 1 == path[SnekY][SnekX - 1] && direction !='l'){
            direction = 'l';
        }else if(path[SnekY][SnekX] == Screen_Width/Unit_Size * Screen_Height/Unit_Size -1) direction = 'd';
        
        //System.out.println(direction);
        //System.out.println(path[y[0]/Unit_Size][x[0]/Unit_Size] + 1 +"    " + path[y[0]/Unit_Size][x[0]/Unit_Size + 1]);
        move();
    }

*/
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
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
                //System.out.println("daetaka es shechvenebuli");
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