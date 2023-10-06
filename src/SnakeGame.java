import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    @Override
    public void actionPerformed(ActionEvent e) {
        if(!paused) {
            move();
            repaint();

            if (gameOver) {
                gameLoop.stop();
                restartButton.setEnabled(true);
                repaint();
            }
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    private class Tile {
        int x,y;
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    int boardWidth;
    int boardHeight;
    int tileSize = 25;
    //Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    //Food
    Tile food;
    Random random;

    //Game logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;
    JButton restartButton;

    private boolean paused = false;

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if(e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if(e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if(e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }  else if (e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused;
            if (!paused) {
                gameLoop.start();
            }
            repaint();
        }
    }

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5,5);
        snakeBody =new ArrayList<Tile>();

        food = new Tile(10,10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100,this);
        gameLoop.start();

        restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        add(restartButton);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

        if (gameOver) {
            restartButton.setBounds(boardWidth / 2 - 60, boardHeight / 2 + 20, 120, 40);
            add(restartButton);
        } else {
            remove(restartButton);
        }

        if (paused) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("PAUSE", boardWidth / 2 - 50, boardHeight / 2);
        }
    }

    public void draw(Graphics g) {
        //Food
        g.setColor(Color.red);
        g.fill3DRect(food.x*tileSize, food.y*tileSize, tileSize,tileSize,true);

        //Snake Head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x*tileSize,snakeHead.y*tileSize,tileSize,tileSize,true);

        //Snake Body
        for(int i=0;i < snakeBody.size();i++) {
            Tile snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x*tileSize,snakePart.y*tileSize,tileSize,tileSize,true);
        }

        //Score  
        g.setFont(new Font("Arial",Font.PLAIN,16));
        if(gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + String.valueOf(snakeBody.size()),boardWidth / 2 - 50, boardHeight / 2);
        } else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()),tileSize - 16, tileSize);
        }
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth/tileSize);
        food.y = random.nextInt(boardHeight/tileSize);
    }

    public boolean collision(Tile tile1 , Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void restartGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        placeFood();
        velocityX = 0;
        velocityY = 0;
        gameOver = false;

        restartButton.setEnabled(false);
        gameLoop.start();
        requestFocus();
        repaint();
        remove(restartButton);
        repaint();
    }

    public void move() {
        //Eat Food
        if(collision(snakeHead,food)) {
            snakeBody.add(new Tile(food.x , food.y));
            placeFood();
        }

        //Snake Body
        for(int i=snakeBody.size()-1;i>=0;i--) {
            Tile snakePart = snakeBody.get(i);
            if(i == 0) {
                snakePart.x = snakeHead.x;;
                snakePart.y = snakeHead.y;
            }
            else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }

        }

        //Snake Head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //game over conditions
        for(int i = 0; i <snakeBody.size();i++) {
            Tile snakePart = snakeBody.get(i);
            //collide with the snake head
            if(collision(snakeHead,snakePart)) {
                gameOver = true;
            }

            if(snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth
                    || snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight) {
                gameOver = true;
            }
        }
    }
}
