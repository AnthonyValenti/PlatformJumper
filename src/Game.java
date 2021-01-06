
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Braedon Kwan, Anthony Valenti, George Letea, Chris Kingsland
 * @version 6.01
 * @since May 10,2019
 */
public class Game extends JPanel implements KeyListener, MouseListener {
    // Declare variables

    private Jumpman jumpman;
    private Block[] block;
    private Dodger dodgeMan;
    private ArrayList<FallingBlock> fallingBlock;
    private BufferedImage background, startScreen, endScreen;
    private int screenWidth, screenHeight;
    private int level;
    private int gameState;
    private String imageName;
    private int endScreenSelector;
    private AudioInputStream audioInputStream;
    private Clip clip;
    private int counter;
    private int[] topScores;

    //Calls init(), createScreen(), and loop()
    public static void main(String[] args) {
        Game game = new Game();
        game.init();
        game.createScreen();
        game.loop();
    }

    /**
     * Pre : Private variables declared at top of class Post: Private variables
     * are defined/populated with data
     */
    public void init() {
        screenWidth = 400;
        screenHeight = 600;
        topScores = new int[3];
        counter = 0;
        level = 1;
        gameState = 0;
        endScreenSelector = 0;
        fallingBlock = new ArrayList<FallingBlock>();
        try {
            startScreen = ImageIO.read(new File("startScreen.jpg"));
            endScreen = ImageIO.read(new File("endScreen.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File("Jumpman.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setups the JFrame and JPanel pre: defined values for the private integers
     * of screenWidth and screenHeight post: Creates a JFrame and JPanel
     */
    public void createScreen() {
        JFrame frame = new JFrame();
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        this.addKeyListener(this);
        this.addMouseListener(this);
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Resets the main game, resets player's position, generates level, selects
     * background Pre: Image file downloaded in folder, jumpman declared Post:
     * reset screen and all game aspects
     */
    public void reset() {
        levelGen();
        jumpman = new Jumpman(screenWidth / 2 - 20, screenHeight - 80, 40, 60, imageName);
        Random random = new Random();
        try {
            background = ImageIO.read(new File("background" + random.nextInt(6) + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the the position of the 9 blocks randomly in a special
     * algorithm to make the levels possible Pre: Block object needs to be
     * declared Post: Each block spawns in such a way that it is within the
     * maximum x and y jump distances of the user Each block spawns in such a
     * way that there is enough space between for the player to fit through Each
     * box is surrounded by an invisible hitBox to ensure level possibility and
     * to check the stated conditions above
     */
    public void levelGen() {
        Random random = new Random();
        block = new Block[9];
        boolean next;
        block[0] = new Block(screenWidth / 2 - 40, 580, 80, 20);
        for (int i = 1; i < block.length; i += 0) {
            next = true;
            int x = random.nextInt(screenWidth - 79);
            int y = random.nextInt(screenHeight - 79) + 60;
            block[i] = new Block(x, y, 80, 20);
            for (int j = i - 1; j >= 0; j--) {
                if (block[i].spawning().intersects(block[j].hitBox())) {
                    next = false;
                    break;
                }
            }
            if (next == true) {
                i++;
            }
        }
        for (int top = 1; top < block.length; top++) {
            Block item = block[top];
            int i = top;
            while (i > 0 && item.y < block[i - 1].y) {
                block[i] = block[i - 1];
                i--;
            }
            block[i] = item;
        }
        for (int i = block.length - 1; i > 0; i--) {
            if (block[i].y - block[i - 1].y > 120) {
                levelGen();
            }
        }
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block.length; j++) {
                if (block[i].y - block[j].y < 100 && i != j) {
                    if (Math.abs(block[i].x - block[j].x) < 10) {
                        levelGen();
                    }
                }
            }
        }
    }

    /**
     * dodgeMan is a special level that happens every fifth level, the player
     * dodges blocks until a certain amount of blocks are generated Pre: Object
     * dodgeMan must be declared Post:Resets the dodgeMan special level game,
     * resets player's position, selects background
     */
    public void setupDodgeMan() {
        dodgeMan = new Dodger(screenWidth / 2 - 20, screenHeight - 60, 40, 60, imageName);
        Random random = new Random();
        try {
            background = ImageIO.read(new File("background" + random.nextInt(6) + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        spawnFallingBlock();
    }

    /**
     * Creates a new instance of the FallingBlock class Pre: FallingBlock must
     * be declared Post: Creates a new instance of fallingBlock class which adds
     * a new object to the fallingBlock array
     */
    public void spawnFallingBlock() {
        Random random = new Random();
        fallingBlock.add(new FallingBlock(random.nextInt(361), -20, 40, 20));
    }

    /**
     * Continuously loops through the methods move(), conditions(),
     * paint(Graphics g), and sleep(15) Pre: none Post: Handles graphics checks
     * for conditions, handles movement, and delays
     */
    public void loop() {
        while (true) {
            move();
            conditions();
            this.repaint();
            sleep(15); //neccesary pause to handle game movements
        }
    }

    /**
     * Responsible for changing the coordinates of all moving objects Pre:
     * variable gameState must be 1 or 3 in order to actually do something Post:
     * Move the jumpman character in both the special level and normal levels
     */
    public void move() {
        if (gameState == 1) {
            jumpman.move();
        } else if (gameState == 3) {
            dodgeMan.move();
            for (int i = 0; i < fallingBlock.size(); i++) {
                fallingBlock.get(i).fall();
            }
        }
    }

    //Checks for specific conditions that trigger certain events such as winning and collisions
    /**
     * Pre: none Post: Checks all the important values used during the game,
     * interprets them to decide what is happening in the game EX: Game
     * state(start screen, end screen, dodgeman, jumpman) EX: If you win/lose on
     * a level then change gameState
     */
    public void conditions() {
        if (gameState == 0) {
            if (imageName != null) {
                reset();
                gameState = 1;
            }
        } else if (gameState == 1) {
            jumpman.leftBlocked = false;
            jumpman.rightBlocked = false;
            jumpman.falling = true;
            blockCollision();
            wrap();
            if (jumpman.y > screenHeight) {
                lose();
            }
        } else if (gameState == 3) {
            counter++;
            dodgeMan.restrictMovement();
            Random random = new Random();
            if (random.nextInt(30) == 0) {
                spawnFallingBlock();
            }
            for (int i = 0; i < fallingBlock.size(); i++) {
                if (fallingBlock.get(i).hitBox().intersects(dodgeMan.hitBox())) {
                    lose();
                    fallingBlock.removeAll(fallingBlock);
                    counter = 0;
                } else if (fallingBlock.get(i).y > 600) {
                    fallingBlock.remove(i);
                }
            }
            if (counter == 1000) {
                win();
                fallingBlock.removeAll(fallingBlock);
                gameState = 1;
                counter = 0;
            }
        }
    }

    /**
     * Checks to see if the player is colliding with the blocks, and if so
     * checks to see what part of the player is colliding with the blocks Limits
     * the movement of the player and prevents the player from falling off
     * screen Pre: coordinates/location of the jumpman and the blocks Post:
     * Return a boolean determining if the jumpman is falling/on a block/ or
     * colliding with a block
     */
    public void blockCollision() {
        for (int i = 0; i < block.length; i++) {
            if (jumpman.topBotBox().intersects(block[i].hitBox())) {
                if (jumpman.ySpeed < 0) {
                    jumpman.y = block[i].y + block[i].height;
                } else if (jumpman.ySpeed > 0) {
                    jumpman.falling = false;
                    jumpman.y = block[i].y - jumpman.height + 1;
                    if (i == 0) {
                        win();
                    }
                }
                jumpman.ySpeed = 0;
            }
            if (jumpman.rightBox().intersects(block[i].hitBox())) {
                jumpman.x = block[i].x - jumpman.width + 1;
                jumpman.rightBlocked = true;
            } else if (jumpman.leftBox().intersects(block[i].hitBox())) {
                jumpman.x = block[i].x + block[i].width - 1;
                jumpman.leftBlocked = true;
            }
        }
    }

    /**
     * Allows the player to wrap around the sides of the screen Pre: coordinates
     * of the jumpman on the screen Post: Allow the jumpman to return to the
     * opposite side of the screen of the coordinate is greater than screenWidth
     */
    public void wrap() {
        if (jumpman.x <= -jumpman.width) {
            jumpman.x = screenWidth;
        } else if (jumpman.x >= screenWidth) {
            jumpman.x = -jumpman.width;
        }
    }

    /**
     * Events that occur when the player loses the game Pre: lose method called
     * when the player falls off a block or gets hit by a block during dodgeman
     * Post: Displays the lose screen which displays high scores and allows the
     * user to quit or retry
     */
    public void lose() {
        this.repaint();
        sleep(1000);
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Highscore.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("Highscore.txt", true));
            writer.newLine();
            writer.write("" + level);
            writer.close();
            topScores[0] = Integer.parseInt(reader.readLine());
            topScores[1] = Integer.parseInt(reader.readLine());
            topScores[2] = Integer.parseInt(reader.readLine());
            while (true) {
                String score = reader.readLine();
                if (score == null) {
                    break;
                }
                if (Integer.parseInt(score) > topScores[0]) {
                    topScores[0] = Integer.parseInt(score);
                }
                for (int i = 0; i < topScores.length; i++) {
                    for (int j = i + 1; j < topScores.length; j++) {
                        if (topScores[i] > topScores[j]) {
                            int temp = topScores[i];
                            topScores[i] = topScores[j];
                            topScores[j] = temp;
                        }
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        level = 1;
        gameState = 2;
    }

    /**
     * Events that occur when the player wins the game Pre: Determined if the
     * jumpan reaches the golden block or passes the dodgeman special level
     * Post: Setup the next level of randomlly generated blocks and increase the
     * level
     */
    public void win() {
        this.repaint();
        sleep(1000);
        level++;
        if (level % 5 == 0) {
            gameState = 3;
            setupDodgeMan();
        } else {
            reset();
        }
    }

    /**
     * Handles all graphics
     *
     * @param g imports graphics to allow draw boxes,jumpman, backgrounds
     * Pre:Game state/level number determines which graphics to draw postion of
     * jumpman/blocks Post: if playing the game the jumpman is drawn at new
     * position if lose the losing screen is displayed if win the next level is
     * drawn if level is divisible by 5, the special dodgeman level is played
     *
     */
    public void paint(Graphics g) {
        if (gameState == 0) {
            g.drawImage(startScreen, 0, 0, screenWidth, screenHeight, null);
        } else if (gameState == 1) {
            g.drawImage(background, 0, 0, screenWidth, screenHeight, null);
            jumpman.render(g);
            for (int i = 0; i < block.length; i++) {
                block[i].render(g);
            }
            g.setColor(Color.BLACK);
            g.fillRect(block[0].x, block[0].y, block[0].width, block[0].height);
            g.setColor(Color.YELLOW);
            g.fillRect(block[0].x + 2, block[0].y + 2, block[0].width - 4, block[0].height - 4);
            g.setColor(Color.RED);
            g.setFont(new Font("Courier", Font.BOLD, 20));
            g.drawString("Level: " + level, 280, 30);
        } else if (gameState == 2) {
            g.drawImage(endScreen, 0, 0, screenWidth, screenHeight, null);
            g.setColor(Color.WHITE);
            if (endScreenSelector == 0) {
                g.drawRect(100, 255, 200, 70);
            } else {
                g.drawRect(100, 355, 200, 70);
            }
            g.setFont(new Font("Courier", Font.BOLD, 30));
            g.setColor(Color.YELLOW);
            g.drawString("Highscores", 110, 470);
            g.setColor(Color.WHITE);
            g.drawString("1st: " + topScores[2], 150, 520);
            g.drawString("2nd: " + topScores[1], 70, 570);
            g.drawString("3rd: " + topScores[0], 230, 570);
        } else if (gameState == 3) {
            g.drawImage(background, 0, 0, screenWidth, screenHeight, null);
            dodgeMan.render(g);
            for (int i = 0; i < fallingBlock.size(); i++) {
                fallingBlock.get(i).render(g, Color.RED);
            }
            g.setColor(Color.RED);
            g.setFont(new Font("Courier", Font.BOLD, 20));
            g.drawString("Level: " + level, 280, 30);
        }
    }

    /**
     * Checks to see if a key is pressed Pre: Import key logger Post: Return a
     * boolean if a key is pressed
     *
     * @param e automatically generated syntax
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameState == 1) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                jumpman.left = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                jumpman.right = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                jumpman.up = true;
            }
        } else if (gameState == 2) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                endScreenSelector = 0;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                endScreenSelector = 1;
            }
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (endScreenSelector == 0) {
                    reset();
                    gameState = 1;
                } else {
                    System.exit(0);
                }
            }
        } else if (gameState == 3) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                dodgeMan.left = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                dodgeMan.right = true;
            }
        }
    }

    /**
     * Checks to see if a key is released Pre: Import key logger. Post: Return a
     * boolean if a key is released.
     *
     * @param e automatically generated syntax
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (gameState == 1) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                jumpman.left = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                jumpman.right = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                //jumpman.up = false;
            }
        } else if (gameState == 3) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                dodgeMan.left = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                dodgeMan.right = false;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    //Creates delay equal to the argument passed in milliseconds
    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Checks to see if a key is pressed. Pre: Import mouse listener. Post:
     * Return a boolean if mouse is clicked.
     *
     * @param e automatically generated syntax
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (gameState == 0) {
            if (e.getY() >= 380 && e.getY() <= 510) {
                if (e.getX() >= 5 && e.getX() <= 85) {
                    imageName = new String("player0.png");
                } else if (e.getX() >= 95 && e.getX() <= 175) {
                    imageName = new String("player1.png");
                } else if (e.getX() >= 220 && e.getX() <= 300) {
                    imageName = new String("player2.png");
                } else if (e.getX() >= 315 && e.getX() <= 395) {
                    imageName = new String("player3.png");
                }
            } else if (e.getX() >= 340 && e.getX() <= 380 && e.getY() >= 565 && e.getY() <= 595) {
                System.exit(0);
            }
        }
    }

    //Checks to see if the mouse has been released
    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
