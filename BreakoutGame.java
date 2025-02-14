import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BreakoutGame extends JPanel implements ActionListener {
    private Timer timer;
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks;
    private boolean inGame = true;
    private final int DELAY = 10;

    public BreakoutGame() {
        initGame();
    }

    private void initGame() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setPreferredSize(new Dimension(400, 300));
        setBackground(Color.BLACK);

        ball = new Ball();
        paddle = new Paddle();
        bricks = new Brick[30];
        int k = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                bricks[k] = new Brick(j * 60 + 30, i * 20 + 50);
                k++;
            }
        }

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inGame) {
            drawObjects(g);
        } else {
            drawGameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics g) {
        g.drawImage(ball.getImage(), ball.getX(), ball.getY(), this);
        g.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(), this);

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                g.drawImage(brick.getImage(), brick.getX(), brick.getY(), this);
            }
        }
    }

    private void drawGameOver(Graphics g) {
        String msg = "Game Over";
        Font font = new Font("Helvetica", Font.BOLD, 18);
        FontMetrics fm = getFontMetrics(font);

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ball.move();
        paddle.move();
        checkCollision();
        repaint();
    }

    private void checkCollision() {
        if (ball.getRect().getMaxY() > getHeight()) {
            inGame = false;
        }

        if ((ball.getRect()).intersects(paddle.getRect())) {
            ball.setYDir(-1);
        }

        for (int i = 0, j = 0; i < bricks.length; i++) {
            if ((ball.getRect()).intersects(bricks[i].getRect())) {
                int ballLeft = (int) ball.getRect().getMinX();
                int ballHeight = (int) ball.getRect().getHeight();
                int ballWidth = (int) ball.getRect().getWidth();
                int ballTop = (int) ball.getRect().getMinY();

                Point pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                Point pointLeft = new Point(ballLeft - 1, ballTop);
                Point pointTop = new Point(ballLeft, ballTop - 1);
                Point pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                if (!bricks[i].isDestroyed()) {
                    if (bricks[i].getRect().contains(pointRight)) {
                        ball.setXDir(-1);
                    } else if (bricks[i].getRect().contains(pointLeft)) {
                        ball.setXDir(1);
                    }

                    if (bricks[i].getRect().contains(pointTop)) {
                        ball.setYDir(1);
                    } else if (bricks[i].getRect().contains(pointBottom)) {
                        ball.setYDir(-1);
                    }

                    bricks[i].setDestroyed(true);
                }
            }
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            paddle.keyPressed(e);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Breakout Game");
            BreakoutGame game = new BreakoutGame();
            frame.add(game);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

class Ball {
    private int x = 200;
    private int y = 150;
    private int xDir = 1;
    private int yDir = -1;
    private final int BALL_SIZE = 10;
    private final Image image;

    public Ball() {
        image = new ImageIcon("ball.png").getImage();
    }

    public void move() {
        x += xDir;
        y += yDir;

        if (x <= 0) {
            setXDir(1);
        }

        if (x >= 400 - BALL_SIZE) {
            setXDir(-1);
        }

        if (y <= 0) {
            setYDir(1);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImage() {
        return image;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, BALL_SIZE, BALL_SIZE);
    }

    public void setXDir(int xDir) {
        this.xDir = xDir;
    }

    public void setYDir(int yDir) {
        this.yDir = yDir;
    }
}

class Paddle {
    private int x = 200;
    private final int y = 260;
    private final int PADDLE_WIDTH = 60;
    private final int PADDLE_HEIGHT = 10;
    private final int MOVE_SPEED = 2;
    private final Image image;
    private int xDir;

    public Paddle() {
        image = new ImageIcon("paddle.png").getImage();
    }

    public void move() {
        x += xDir;

        if (x <= 0) {
            x = 0;
        }

        if (x >= 400 - PADDLE_WIDTH) {
            x = 400 - PADDLE_WIDTH;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            xDir = 0;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            xDir = -MOVE_SPEED;
        }

        if (key == KeyEvent.VK_RIGHT) {
            xDir = MOVE_SPEED;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImage() {
        return image;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
    }
}

class Brick {
    private final int x;
    private final int y;
    private final int BRICK_WIDTH = 50;
    private final int BRICK_HEIGHT = 10;
    private boolean destroyed;
    private final Image image;

    public Brick(int x, int y) {
        this.x = x;
        this.y = y;
        this.destroyed = false;
        this.image = new ImageIcon("brick.png").getImage();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImage() {
        return image;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, BRICK_WIDTH, BRICK_HEIGHT);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
