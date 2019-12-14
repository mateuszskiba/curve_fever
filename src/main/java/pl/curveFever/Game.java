package pl.curveFever;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.Math.PI;
import static pl.curveFever.Board.HEIGHT;
import static pl.curveFever.Board.WIDTH;

public class Game {
    private List<Player> players = new ArrayList<>();
    private int initNumberOfPlayers;
    private int currentNumberOfPlayers;

    public Game(final int initNumberOfPlayers) {
        this.initNumberOfPlayers = initNumberOfPlayers;
        this.currentNumberOfPlayers = initNumberOfPlayers;
        if (initNumberOfPlayers == 1) {
            players.add(new Player(WIDTH/2.0, HEIGHT/2.0, random()*2*PI, Color.RED));
        }
        else if (initNumberOfPlayers == 2) {
            players.add(new Player(WIDTH/3.0, HEIGHT/2.0, random()*2*PI, Color.RED));
            players.add(new Player(2*WIDTH/3.0, HEIGHT/2.0, random()*2*PI, Color.BLUE));
        }
        else if (initNumberOfPlayers == 3) {
            players.add(new Player(WIDTH/3.0, HEIGHT/2.0, random()*2*PI, Color.RED));
            players.add(new Player(2*WIDTH/3.0, HEIGHT/2.0, random()*2*PI, Color.BLUE));
            players.add(new Player(WIDTH/2.0, 2*HEIGHT/3.0, random()*2*PI, Color.GREEN));
        }
        else if (initNumberOfPlayers == 4) {
            players.add(new Player(WIDTH/3.0, HEIGHT/3.0, random()*2*PI, Color.RED));
            players.add(new Player(2*WIDTH/3.0, HEIGHT/3.0, random()*2*PI, Color.BLUE));
            players.add(new Player(WIDTH/3.0, 2*HEIGHT/3.0, random()*2*PI, Color.GREEN));
            players.add(new Player(2*WIDTH/3.0, 2*HEIGHT/3.0, random()*2*PI, Color.DARKTURQUOISE));
        }
    }

    /**
     * @return true when game is over
     */
    public boolean nextStep() {
        players.forEach(Player::generateNextLine);
        checkCollision();
        return isGameOver();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void playerTurnLeft(int idx) {
        players.get(idx).setTurn(-1);
    }

    public void playerTurnRight(int idx) {
        players.get(idx).setTurn(1);
    }

    public void playerGoStraight(int idx) {
        players.get(idx).setTurn(0);
    }

    public void setDraw(boolean draw) {
        players.forEach(player -> {
            if (draw) {
                player.markGap();
            }
            player.setDraw(draw);
        });
    }

    public int getWinnerIdx() {
        int winnerIndex = -1;
        for (int i = 0; i < initNumberOfPlayers; i++) {
            if (players.get(i).isNowPlaying()) {
                winnerIndex = i;
            }
        }
        return winnerIndex;
    }

    private void checkCollision() {
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).isNowPlaying()) continue;
            final List<Point> iVisited = players.get(i).getVisited();
            if (iVisited.size() > 1) {
                Point last = iVisited.get(iVisited.size() - 1);
                Point nextToLast = iVisited.get(iVisited.size() - 2);
                for (int j = 0; j < players.size(); j++) {
                    final List <Point> jVisited = players.get(j).getVisited();
                    final int sizeToCheck = (i == j) ? jVisited.size() - 3 : jVisited.size() - 1;
                    for (int k = 0; k < sizeToCheck; k++) {
                        Point p1 = jVisited.get(k);
                        Point p2 = jVisited.get(k + 1);
                        if (!p1.isGap() && areIntersecting(p1, p2, nextToLast, last)) {
                            players.get(i).setNowPlaying(false);
                            --currentNumberOfPlayers;
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean isGameOver() {
        return currentNumberOfPlayers <= 1;
    }

    private static boolean outOfBounds(Point p) {
        return (p.getX() > WIDTH || p.getX() < 0 || p.getY() > HEIGHT || p.getY() < 0);
    }

    private static boolean areIntersecting(Point p1, Point p2, Point q1, Point q2) {
        final boolean result;
        // special case when player move out of map
        if (outOfBounds(q2)) {
            result = true;
        }
        // other cases when sections are intersecting
        else if (p1.getX() != p2.getX() && q1.getX() != q2.getX()) {
            // count a, b parameters for two lines
            double pa = (p1.getY() - p2.getY()) / (p1.getX() - p2.getX());
            double pb = p1.getY() - pa * p1.getX();
            double qa = (q1.getY() - q2.getY()) / (q1.getX() - q2.getX());
            double qb = q1.getY() - qa * q1.getX();
            // point of intersection
            double x = (qb - pb) / (pa - qa);
            double y = pa * x + pb;
            // check if the point belongs to the sections
            boolean pCondition = min(p1.getX(), p2.getX()) <= x && x <= max(p1.getX(), p2.getX())
                    && min(p1.getY(), p2.getY()) <= y && y <= max(p1.getY(), p2.getY());
            boolean qCondition = min(q1.getX(), q2.getX()) <= x && x <= max(q1.getX(), q2.getX())
                    && min(q1.getY(), q2.getY()) <= y && y <= max(q1.getY(), q2.getY());
            result = pCondition && qCondition;
        }
        else if (p1.getX() == p2.getX() && q1.getX() == q2.getX()) {
            result = p1.getX() == q1.getX() && (min(p1.getY(), p2.getY()) <= max(q1.getY(), q2.getY())
                    || min(q1.getY(), q2.getY()) <= max(p1.getY(), p2.getY()));
        }
        else if (p1.getX() == p2.getX()) {
            double qa = (q1.getY() - q2.getY()) / (q1.getX() - q2.getX());
            double qb = q1.getY() - qa * q1.getX();
            double x = p1.getX();
            double y = qa * x + qb;
            boolean pCondition = min(p1.getY(), p2.getY()) <= x && x <= max(p1.getY(), p2.getY());
            boolean qCondition = min(q1.getX(), q2.getX()) <= x && x <= max(q1.getX(), q2.getX())
                    && min(q1.getY(), q2.getY()) <= y && y <= max(q1.getY(), q2.getY());
            result = pCondition && qCondition;
        }
        else { // (q1.getX() == q2.getX())
            double pa = (p1.getY() - p2.getY()) / (p1.getX() - p2.getX());
            double pb = p1.getY() - pa * p1.getX();
            double x = q1.getX();
            double y = pa * q1.getX() + pb;
            boolean pCondition = min(p1.getX(), p2.getX()) <= x && x <= max(p1.getX(), p2.getX())
                    && min(p1.getY(), p2.getY()) <= y && y <= max(p1.getY(), p2.getY());
            boolean qCondition = min(q2.getX(), q2.getY()) <= x && x <= max(q2.getX(), q2.getY());
            result = pCondition && qCondition;
        }
        return result;
    }
}
