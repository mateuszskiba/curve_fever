package pl.curveFever;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static pl.curveFever.Board.HEIGHT;
import static pl.curveFever.Board.WIDTH;

public class Game {

    public CollisionCheckResult checkCollision(Player[] players, int currentNumberOfPlayers) {
        for (int i = 0; i < players.length; i++) {
            if (!players[i].isNowPlaying()) continue;
            final List<Point> iVisited = players[i].getVisited();
            if (iVisited.size() > 1) {
                Point last = iVisited.get(iVisited.size() - 1);
                Point nextToLast = iVisited.get(iVisited.size() - 2);
                for (int j = 0; j < players.length; j++) {
                    final List <Point> jVisited = players[j].getVisited();
                    final int sizeToCheck = (i == j) ? jVisited.size() - 3 : jVisited.size() - 1;
                    for (int k = 0; k < sizeToCheck; k++) {
                        Point p1 = jVisited.get(k);
                        Point p2 = jVisited.get(k + 1);
                        if (!p1.isGap() && areIntersecting(p1, p2, nextToLast, last)) {
                            players[i].setNowPlaying(false);
                            if (--currentNumberOfPlayers <= 1) {
//                                this.showResults();
                                return CollisionCheckResult.GAME_OVER;
                            }
                        }
                    }
                }
            }
        }
        return CollisionCheckResult.CONTINUE_GAME;
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
