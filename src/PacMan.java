import java.awt.*;
//abstract window toolkit pt interfate si desen 2d
import java.awt.event.*;
//stocheaza elemente unice fara ordine
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
//clasa devine pannel personalizat
//devine un container GUI
public class PacMan extends JPanel implements ActionListener,KeyListener {
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'u';//u d l r
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image,int x, int y,int width,int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }
    void updateDirection(char direction){
            char previousDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x+=this.velocityX;
            this.y+=this.velocityY;
            for(Block wall:walls){
                if(collision(this,wall)){
                    this.x-=this.velocityX;
                    this.y-=this.velocityY;
                    this.direction=previousDirection;
                    updateVelocity();
                }
            }
    }
    void updateVelocity(){
        if(this.direction == 'u'){
            this.velocityX=0;
            this.velocityY=-tileSize/4;
}
        else if(this.direction == 'd'){
            this.velocityX=0;
            this.velocityY=tileSize/4;
        }
        else if(this.direction == 'l'){
            this.velocityX=-tileSize/4;
            this.velocityY=0;
        }
        else if(this.direction == 'r'){
            this.velocityX=tileSize/4;
            this.velocityY=0;
        }
    }
    void reset(){
            this.x=this.startX;
            this.y=this.startY;
    }
    }
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int bordWidth = columnCount * tileSize;
    private int bordHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image pinkGhostImage;
    private Image orangeGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "X       bpo       X",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;


    Timer gameLoop;
    char[] directions={'u','d','l','r'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameover = false;
    PacMan(){
        setPreferredSize(new Dimension(bordWidth, bordHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        //asculta the presses

        //load images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        loadMap();
        for(Block ghost:ghosts){
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50,this);
        //50=delay
        //this=pacman
        //20fps (1000/50)
        gameLoop.start();



    }
    public void loadMap(){
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        for(int r = 0; r < rowCount; r++){
            for(int c = 0; c < columnCount; c++){
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X'){
                    Block wall = new Block(wallImage,x,y,tileSize,tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b'){
                    Block ghost = new Block(blueGhostImage,x,y,tileSize,tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o'){
                    Block ghost = new Block(orangeGhostImage,x,y,tileSize,tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p'){
                    Block ghost = new Block(pinkGhostImage,x,y,tileSize,tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r'){
                    Block ghost = new Block(redGhostImage,x,y,tileSize,tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P'){
                    pacman = new Block(pacmanRightImage,x,y,tileSize,tileSize);
                }
                else if (tileMapChar == ' '){
                    Block food = new Block(null, x+14,y+14,4,4);
                    foods.add(food);
                }

            }
        }

    }
    //obiect ce imi permite sa desenez
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(pacman.image,pacman.x,pacman.y,pacman.width,pacman.height,null);
        for(Block ghost : ghosts){
            g.drawImage(ghost.image,ghost.x,ghost.y,ghost.width,ghost.height,null);
        }
        for(Block wall : walls){
            g.drawImage(wall.image,wall.x,wall.y,wall.width,wall.height,null);
        }
        g.setColor(Color.WHITE);
        for(Block food : foods){
            g.fillRect(food.x,food.y,food.width,food.height);
        }
        g.setFont(new Font("Arial",Font.PLAIN,18));
        if(gameover){
            g.drawString("GAME OVER " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else{
            g.drawString("x" + String.valueOf(lives) + "Score: " + String.valueOf(score), tileSize/2, tileSize/2 );
        }
    }
    public void move(){
        pacman.x+=pacman.velocityX;
        pacman.y+=pacman.velocityY;
        for(Block wall : walls){
            if(collision(pacman,wall)){
                pacman.x-=pacman.velocityX;
                pacman.y-=pacman.velocityY;
                break;
            }
        }
        for(Block ghost:ghosts){
            if(collision(ghost,pacman)){
                lives-=1;
                if(lives==0){
                    gameover = true;
                    return;
                }
                resetPositions();
            }
            if(ghost.y==tileSize*9 && ghost.direction!='u' && ghost.direction!='d'){
                ghost.updateDirection('u');
            }
            ghost.x+=ghost.velocityX;
            ghost.y+=ghost.velocityY;
            for(Block wall : walls){
                if(collision(ghost,wall)){
                    ghost.x-=ghost.velocityX;
                    ghost.y-=ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }
        Block foodEaten = null;
        for(Block food : foods){
            if(collision(pacman,food)){
                foodEaten = food;
                score+=10;

            }
        }
        foods.remove(foodEaten);
        if(foods.isEmpty()){
            loadMap();
            resetPositions();
        }
    }
    public boolean collision(Block a, Block b){
        return a.x<b.x+b.width &&
                a.x+a.width>b.x &&
                a.y<b.y+b.height&&
                a.y+a.height>b.y;
    }
    public void resetPositions(){
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for(Block ghost : ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameover){
            gameLoop.stop();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
//eveniment tip character input-rezultatul efectiv al unei taste
    @Override
    public void keyPressed(KeyEvent e) {}
//cand o tasta e apasata
    @Override
    public void keyReleased(KeyEvent e) {
//cand o tasta e eliberata
        if(gameover){
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameover = false;
            gameLoop.start();
        }
        if(e.getKeyCode() == KeyEvent.VK_UP){
            pacman.updateDirection('u');
        }

        else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            pacman.updateDirection('d');
        }

        else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            pacman.updateDirection('l');
        }

        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            pacman.updateDirection('r');
        }
        if(pacman.direction=='u'){
            pacman.image=pacmanUpImage;
        }
        else if(pacman.direction=='d'){
            pacman.image=pacmanDownImage;
        }
        else if(pacman.direction=='l'){
            pacman.image=pacmanLeftImage;
        }
        else if(pacman.direction=='r'){
            pacman.image=pacmanRightImage;
        }
    }
}
