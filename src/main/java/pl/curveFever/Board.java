package pl.curveFever;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.*;


public class Board extends Application {
    protected static final int WIDTH = 900;
    protected static final int HEIGHT = 700;
    private static final double KEYFRAME_DURATION_TIME = 0.026; // seconds
    private static final int NUMBER_OF_PLAYERS = 2;
    private static final long START_DRAWING_DELAY = 1500; // milliseconds
    private static final long MIN_TIME_OF_DRAWING = 1000; // milliseconds
    private static final long MAX_TIME_OF_DRAWING = 2000; // milliseconds
    private static final long MIN_TIME_OF_DELAY = 200; // milliseconds
    private static final long MAX_TIME_OF_DELAY = 400; // milliseconds
    private static final double END_CIRCLE_RADIUS = 1.0;
    private static final double LINE_WIDTH = 2.0;
    private static final double BOUNDS_WIDTH = 4.0;
    private static final String TITLE = "Curve fever!";
    private static final Color BOUNDS_COLOR = Color.BLACK;
    private final Timer timer = new Timer();
    private final Timeline timeline = new Timeline();
    private Group root = new Group();
    private Scene scene = new Scene(root, WIDTH, HEIGHT);
    private Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private Game game;

    @Override
    public void start(Stage primaryStage) {
        root.getChildren().add(canvas);
        game = new Game(NUMBER_OF_PLAYERS);
        game.nextStep();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                startDrawing();
            }
        }, START_DRAWING_DELAY);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(KEYFRAME_DURATION_TIME), event -> {
            if (game.nextStep()) {
                showResults();
            }
            gc.clearRect(0, 0, WIDTH, HEIGHT);
            List<Player> players = game.getPlayers();
            players.forEach(this::drawLines);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        scene.setOnKeyPressed(event -> {
            if (event.getCode().ordinal() == KeyCode.LEFT.ordinal() && NUMBER_OF_PLAYERS > 0) {
                game.playerTurnLeft(0);
            } else if (event.getCode().ordinal() == KeyCode.RIGHT.ordinal() && NUMBER_OF_PLAYERS > 0) {
                game.playerTurnRight(0);
            }
            if (event.getCode().ordinal() == KeyCode.A.ordinal() && NUMBER_OF_PLAYERS > 1) {
                game.playerTurnLeft(1);
            } else if (event.getCode().ordinal() == KeyCode.D.ordinal() && NUMBER_OF_PLAYERS > 1) {
                game.playerTurnRight(1);
            }
            if (event.getCode().ordinal() == KeyCode.N.ordinal() && NUMBER_OF_PLAYERS > 2) {
                game.playerTurnLeft(2);
            } else if (event.getCode().ordinal() == KeyCode.M.ordinal() && NUMBER_OF_PLAYERS > 2) {
                game.playerTurnRight(2);
            }
            if (event.getCode().ordinal() == KeyCode.NUMPAD8.ordinal() && NUMBER_OF_PLAYERS > 3) {
                game.playerTurnLeft(3);
            } else if (event.getCode().ordinal() == KeyCode.NUMPAD9.ordinal() && NUMBER_OF_PLAYERS > 3) {
                game.playerTurnRight(3);
            }
            event.consume();
        });

        scene.setOnKeyReleased(event -> {
            if(event.getCode().ordinal() == KeyCode.LEFT.ordinal() && NUMBER_OF_PLAYERS > 0) {
                game.playerGoStraight(0);
            }
            else if (event.getCode().ordinal() == KeyCode.RIGHT.ordinal() && NUMBER_OF_PLAYERS > 0) {
                game.playerGoStraight(0);
            }
            if(event.getCode().ordinal() == KeyCode.A.ordinal() && NUMBER_OF_PLAYERS > 1) {
                game.playerGoStraight(1);
            }
            else if (event.getCode().ordinal() == KeyCode.D.ordinal() && NUMBER_OF_PLAYERS > 1) {
                game.playerGoStraight(1);
            }
            if(event.getCode().ordinal() == KeyCode.N.ordinal() && NUMBER_OF_PLAYERS > 2) {
                game.playerGoStraight(2);
            }
            else if (event.getCode().ordinal() == KeyCode.M.ordinal() && NUMBER_OF_PLAYERS > 2) {
                game.playerGoStraight(2);
            }
            if(event.getCode().ordinal() == KeyCode.NUMPAD8.ordinal() && NUMBER_OF_PLAYERS > 3) {
                game.playerGoStraight(3);
            }
            else if (event.getCode().ordinal() == KeyCode.NUMPAD9.ordinal() && NUMBER_OF_PLAYERS > 3) {
                game.playerGoStraight(3);
            }
            event.consume();
        });

        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
        timeline.play();
    }

    private void startDrawing() {
        game.setDraw(true);
        final long time = Math.round(MIN_TIME_OF_DRAWING + random() * (MAX_TIME_OF_DRAWING - MIN_TIME_OF_DRAWING));
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                stopDrawing();
            }
        }, time);
    }

    private void stopDrawing() {
        game.setDraw(false);
        final long time = Math.round(MIN_TIME_OF_DELAY + random() * (MAX_TIME_OF_DELAY - MIN_TIME_OF_DELAY));
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                startDrawing();
            }
        }, time);
    }



    private void showResults() {
        int winnerIndex = game.getWinnerIdx();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(TITLE);
        alert.setContentText("Player " + (winnerIndex+1) + " won!");
        timeline.stop();
        alert.show();
    }

    private void drawLines(Player player) {
        gc.setStroke(BOUNDS_COLOR);
        gc.setLineWidth(BOUNDS_WIDTH);
        gc.strokeLine(0, 0, 0, HEIGHT);
        gc.strokeLine(0, HEIGHT, WIDTH, HEIGHT);
        gc.strokeLine(WIDTH, HEIGHT, WIDTH, 0);
        gc.strokeLine(WIDTH, 0, 0, 0);
        List<Point> visited = player.getVisited();
        if (visited.size() > 1) {
            gc.setStroke(player.getColor());
            gc.setLineWidth(LINE_WIDTH);
            for (int i = 0; i < visited.size() - 1; i++) {
                if (!visited.get(i).isGap()) {
                    gc.strokeLine(visited.get(i).getX(), visited.get(i).getY(), visited.get(i + 1).getX(), visited.get(i + 1).getY());
                }
            }
        }
        if (player.isNowPlaying()) {
            double r = END_CIRCLE_RADIUS * LINE_WIDTH;
            gc.setFill(player.getColor());
            gc.fillOval(player.getCurrentX() - r, player.getCurrentY() - r, 2 * r, 2 * r);
        }
    }
}