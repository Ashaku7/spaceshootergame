import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SpaceShooterGame extends Application {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int SHIP_WIDTH = 50;
    private static final int SHIP_HEIGHT = 80;
    private static final int BULLET_WIDTH = 5;
    private static final int BULLET_HEIGHT = 20;

    private ImageView playerShip;
    private ImageView enemyShip;
    private ImageView background;
    private double playerX = WIDTH / 2.0 - SHIP_WIDTH / 2.0;
    private double playerY = HEIGHT - SHIP_HEIGHT - 20;
    private boolean moveLeft, moveRight, shoot;
    private double enemyY = -SHIP_HEIGHT;
    private Random random = new Random();
    private boolean gameOver = false;
    private int score = 0;
    private Label scoreLabel;
    private double enemySpeed = 5; // Initial speed of the enemy ship
    private List<ImageView> bullets = new ArrayList<>();
    private Button retryButton;
    private Button exitButton;
    private Label gameOverLabel;
    private HBox buttonContainer;

    @Override
    public void start(Stage primaryStage) {
        // Root Pane
        Pane root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);

        // Background
        background = new ImageView(new Image("background.jpg")); // Replace with actual image path
        background.setFitWidth(WIDTH);
        background.setFitHeight(HEIGHT);

        // Player Ship
        playerShip = new ImageView(new Image("spaceship.png")); // Replace with actual image path
        playerShip.setFitWidth(SHIP_WIDTH);
        playerShip.setFitHeight(SHIP_HEIGHT);
        playerShip.setX(playerX);
        playerShip.setY(playerY);

        // Enemy Ship
        enemyShip = new ImageView(new Image("alien.png")); // Replace with actual image path
        enemyShip.setFitWidth(SHIP_WIDTH);
        enemyShip.setFitHeight(SHIP_HEIGHT);
        resetEnemy();

        // Score Label
        scoreLabel = new Label("Score: 0");
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setStyle("-fx-font-size: 24px;");
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(10);

        // Game Over Label
        gameOverLabel = new Label("Game Over!");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        gameOverLabel.setLayoutX(WIDTH / 2.0 - 100);
        gameOverLabel.setLayoutY(HEIGHT / 2.0 - 60);
        gameOverLabel.setVisible(false);

        // Retry Button
        retryButton = new Button("Retry");
        retryButton.setVisible(false);
        retryButton.setOnAction(e -> retryGame(root));

        // Exit Button
        exitButton = new Button("Exit");
        exitButton.setVisible(false);
        exitButton.setOnAction(e -> System.exit(0));

        // Button Container
        buttonContainer = new HBox(20, retryButton, exitButton);
        buttonContainer.setLayoutX(WIDTH / 2.0 - 60);
        buttonContainer.setLayoutY(HEIGHT / 2.0);
        buttonContainer.setVisible(false);

        // Add elements to root
        root.getChildren().addAll(background, playerShip, enemyShip, scoreLabel, gameOverLabel, buttonContainer);

        // Scene Setup
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) moveLeft = true;
            if (event.getCode() == KeyCode.RIGHT) moveRight = true;
            if (event.getCode() == KeyCode.SPACE) shoot = true;
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT) moveLeft = false;
            if (event.getCode() == KeyCode.RIGHT) moveRight = false;
            if (event.getCode() == KeyCode.SPACE) shoot = false;
        });

        // Animation Timer (Game Loop)
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) {
                    updateGame(root);
                }
            }
        };
        gameLoop.start();

        // Show Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Space Shooter Game - JavaFX");
        primaryStage.show();
    }

    private void updateGame(Pane root) {
        // Move player left and right
        if (moveLeft && playerX > 0) {
            playerX -= 5;
        }
        if (moveRight && playerX < WIDTH - SHIP_WIDTH) {
            playerX += 5;
        }
        playerShip.setX(playerX);

        // Move enemy downward
        enemyY += enemySpeed;
        enemyShip.setY(enemyY);

        // If enemy reaches bottom, end the game
        if (enemyY > HEIGHT) {
            gameOver = true;
            System.out.println("Game Over!");
            showGameOver();
        }

        // Shoot bullets
        if (shoot) {
            shootBullet(root);
        }

        // Move bullets
        moveBullets(root);

        // Collision Detection
        if (playerShip.getBoundsInParent().intersects(enemyShip.getBoundsInParent())) {
            gameOver = true;
            System.out.println("Game Over!");
            showGameOver();
        }

        // Bullet and enemy collision detection
        Iterator<ImageView> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            ImageView bullet = bulletIterator.next();
            if (bullet.getBoundsInParent().intersects(enemyShip.getBoundsInParent())) {
                root.getChildren().remove(bullet);
                bulletIterator.remove();
                resetEnemy();
                score++;
                scoreLabel.setText("Score: " + score);
                enemySpeed += 0.5; // Increase the speed of the enemy ship
                break;
            }
        }
    }

    private void shootBullet(Pane root) {
        ImageView bullet = new ImageView(new Image("bullet1.png")); // Replace with actual image path
        bullet.setFitWidth(BULLET_WIDTH);
        bullet.setFitHeight(BULLET_HEIGHT);
        bullet.setX(playerX + SHIP_WIDTH / 2.0 - BULLET_WIDTH / 2.0);
        bullet.setY(playerY - BULLET_HEIGHT);
        bullets.add(bullet);
        root.getChildren().add(bullet);
    }

    private void moveBullets(Pane root) {
        List<ImageView> bulletsToRemove = new ArrayList<>();
        for (ImageView bullet : bullets) {
            bullet.setY(bullet.getY() - 10);
            if (bullet.getY() < 0) {
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);
        root.getChildren().removeAll(bulletsToRemove);
    }

    private void resetEnemy() {
        enemyY = -SHIP_HEIGHT;
        enemyShip.setX(random.nextInt(WIDTH - SHIP_WIDTH));
        enemyShip.setY(enemyY);
    }

    private void showGameOver() {
        gameOverLabel.setVisible(true);
        retryButton.setVisible(true);
        exitButton.setVisible(true);
        buttonContainer.setVisible(true);
    }

    private void retryGame(Pane root) {
        gameOver = false;
        score = 0;
        enemySpeed = 5;
        playerX = WIDTH / 2.0 - SHIP_WIDTH / 2.0;
        playerY = HEIGHT - SHIP_HEIGHT - 20;
        playerShip.setX(playerX);
        playerShip.setY(playerY);
        resetEnemy();
        scoreLabel.setText("Score: " + score);
        gameOverLabel.setVisible(false);
        retryButton.setVisible(false);
        exitButton.setVisible(false);
        buttonContainer.setVisible(false);
        bullets.clear();
        root.getChildren().removeIf(node -> node instanceof ImageView && node != playerShip && node != enemyShip && node != background);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
