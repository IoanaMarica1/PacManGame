import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import java.awt.FontMetrics;

enum GameState {
    START_MENU, PLAYING, GAME_OVER_MENU, HIGH_SCORES
}

public class PacMan extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        int startX, startY;
        char direction = 'u';
        int velocityX = 0, velocityY = 0;
        Image image;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char dir) {
            char prevDir = this.direction;
            this.direction = dir;
            updateVelocity();
            this.x += velocityX;
            this.y += velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= velocityX;
                    this.y -= velocityY;
                    this.direction = prevDir;
                    updateVelocity();
                    break;
                }
            }
        }

        void updateVelocity() {
            switch (direction) {
                case 'u' -> { velocityX = 0; velocityY = -tileSize / 4; }
                case 'd' -> { velocityX = 0; velocityY = tileSize / 4; }
                case 'l' -> { velocityX = -tileSize / 4; velocityY = 0; }
                case 'r' -> { velocityX = tileSize / 4; velocityY = 0; }
            }
        }

        void reset() {
            x = startX;
            y = startY;
            velocityX = 0;
            velocityY = 0;
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int bordWidth = columnCount * tileSize;
    private final int bordHeight = rowCount * tileSize;

    private Image wallImage, blueGhostImage, pinkGhostImage, orangeGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private String[][] levels = {
            {
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
            },
            {
                    "XXXXXXXXXXXXXXXXXXX",
                    "X P X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X X X X X X X X X X",
                    "X                 X",
                    "XXXXXXXXXXXXXXXXXXX"
            }
    };

    private HashSet<Block> walls, foods, ghosts;
    private Block pacman;
    private Timer gameLoop;
    private char[] directions = {'u','d','l','r'};
    private Random random = new Random();
    private int score = 0, lives = 3;
    private boolean gameover = false;

    private GameState state = GameState.START_MENU;
    private int selectedOption = 0;
    private int currentLevel = 0;

    public PacMan() {
        setPreferredSize(new Dimension(bordWidth, bordHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Load images
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

        gameLoop = new Timer(50, this);
    }

    private void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        String[] currentTileMap = levels[currentLevel];

        for (int r = 0; r < rowCount; r++) {
            String row = currentTileMap[r];
            for (int c = 0; c < columnCount; c++) {
                char tile = row.charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                switch (tile) {
                    case 'X' -> walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                    case 'b' -> ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                    case 'o' -> ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                    case 'p' -> ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                    case 'r' -> ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    case ' ' -> foods.add(new Block(null, x + 14, y + 14, 4, 4));
                }
            }
        }

        // Initialize ghost directions
        for (Block ghost : ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        switch (state) {
            case START_MENU -> drawStartMenu(g);
            case GAME_OVER_MENU -> drawGameOverMenu(g);
            case HIGH_SCORES -> {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("Top 5 Scores", bordWidth / 2 - 100, bordHeight / 4);
                g.drawString("Press any key to Menu", bordWidth / 2 - 150, bordHeight - 50);
            }
            case PLAYING -> drawGame(g);
        }
    }

    private void drawGame(Graphics g) {
        // Walls
        for (Block wall : walls) g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

        // Foods
        g.setColor(Color.WHITE);
        for (Block food : foods) g.fillRect(food.x, food.y, food.width, food.height);

        // Pacman & Ghosts
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block ghost : ghosts) g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

        // Score & Lives & Level
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString("Lives: x" + lives + " Score: " + score + " Level: " + (currentLevel + 1), tileSize / 2, tileSize / 2);
    }

    private void drawStartMenu(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        int titleX = (bordWidth - fm.stringWidth("PAC-MAN")) / 2;
        g.drawString("PAC-MAN", titleX, bordHeight / 4);

        g.setFont(new Font("Arial", Font.BOLD, 30));
        fm = g.getFontMetrics();

        int optionY = bordHeight / 2;
        String[] options = {"Start Game", "See Top 5 Scores"};

        for (int i = 0; i < options.length; i++) {
            int optionX = (bordWidth - fm.stringWidth(options[i])) / 2;
            if (i == selectedOption) {
                g.setColor(Color.YELLOW);
                g.drawString(">", optionX - 30, optionY + i * 50);
            }
            g.setColor(Color.WHITE);
            g.drawString(options[i], optionX, optionY + i * 50);
        }
    }

    private void drawGameOverMenu(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        int titleX = (bordWidth - fm.stringWidth("GAME OVER")) / 2;
        g.drawString("GAME OVER", titleX, bordHeight / 3);

        String scoreText = "Final Score: " + score;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        fm = g.getFontMetrics();
        int scoreX = (bordWidth - fm.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, scoreX, bordHeight / 3 + 60);

        String promptText = "Press ENTER to Menu";
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        fm = g.getFontMetrics();
        int promptX = (bordWidth - fm.stringWidth(promptText)) / 2;
        g.drawString(promptText, promptX, bordHeight / 3 + 120);
    }

    private void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            if (collision(ghost, pacman)) {
                lives--;
                if (lives == 0) {
                    gameover = true;
                    return;
                }
                resetPositions();
            }

            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    break;
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
                break;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            if (currentLevel < levels.length - 1) {
                currentLevel++;
                loadMap();
                resetPositions();
            } else {
                gameover = true;
                gameLoop.stop();
            }
        }
    }

    private boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
                a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private void resetPositions() {
        pacman.reset();
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    private void resetGame() {
        lives = 3;
        score = 0;
        gameover = false;
        currentLevel = 0;
        selectedOption = 0;
        loadMap();
        resetPositions();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state == GameState.PLAYING) {
            move();
            if (gameover) state = GameState.GAME_OVER_MENU;
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (state) {
            case START_MENU -> {
                if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) selectedOption = 1 - selectedOption;
                else if (keyCode == KeyEvent.VK_ENTER) {
                    if (selectedOption == 0) {
                        resetGame();
                        state = GameState.PLAYING;
                        gameLoop.start();
                    } else state = GameState.HIGH_SCORES;
                }
            }

            case GAME_OVER_MENU -> {
                if (keyCode == KeyEvent.VK_ENTER) state = GameState.START_MENU;
            }

            case HIGH_SCORES -> state = GameState.START_MENU;

            case PLAYING -> {
                if (keyCode == KeyEvent.VK_UP) pacman.updateDirection('u');
                else if (keyCode == KeyEvent.VK_DOWN) pacman.updateDirection('d');
                else if (keyCode == KeyEvent.VK_LEFT) pacman.updateDirection('l');
                else if (keyCode == KeyEvent.VK_RIGHT) pacman.updateDirection('r');

                pacman.image = switch (pacman.direction) {
                    case 'u' -> pacmanUpImage;
                    case 'd' -> pacmanDownImage;
                    case 'l' -> pacmanLeftImage;
                    default -> pacmanRightImage;
                };
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}
