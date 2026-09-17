package com.pixelpennant.engine;

import java.util.ArrayList;
import java.util.List;

/** Rendering-independent baseball state and rules for Pixel Pennant. */
public final class BaseballGame {
    private final int scheduledInnings;
    private final List<Integer> awayByInning = new ArrayList<>();
    private final List<Integer> homeByInning = new ArrayList<>();
    private int inning = 1, balls, strikes, outs, awayRuns, homeRuns, awayHits, homeHits;
    private Half half = Half.TOP;
    private boolean first, second, third, gameOver;

    public BaseballGame(int innings) {
        if (innings != 3 && innings != 6 && innings != 9) throw new IllegalArgumentException("Choose 3, 6, or 9 innings");
        scheduledInnings = innings;
        ensureInning();
    }

    public void recordBall() {
        requirePlaying();
        if (++balls >= 4) { forceAdvance(); resetCount(); }
    }
    public void recordStrike() {
        requirePlaying();
        if (++strikes >= 3) recordOut();
    }
    public void recordOut() {
        requirePlaying(); resetCount();
        if (++outs >= 3) changeSides();
    }
    public void recordHit(int bases) {
        requirePlaying();
        if (bases < 1 || bases > 4) throw new IllegalArgumentException("Hit bases must be 1..4");
        if (isTorontoBatting()) homeHits++; else awayHits++;
        advance(bases); resetCount();
    }
    public void recordHomeRun() { recordHit(4); }

    private void forceAdvance() {
        if (first) {
            if (second) {
                if (third) scoreRun();
                third = true;
            }
            second = true;
        }
        first = true;
    }
    private void advance(int n) {
        boolean[] old = {first, second, third}; first = second = third = false;
        for (int i = 2; i >= 0; i--) if (old[i]) placeOrScore(i + 1 + n);
        placeOrScore(n);
    }
    private void placeOrScore(int base) {
        if (base >= 4) scoreRun();
        else if (base == 1) first = true;
        else if (base == 2) second = true;
        else third = true;
    }
    private void scoreRun() {
        if (isTorontoBatting()) { homeRuns++; homeByInning.set(inning - 1, homeByInning.get(inning - 1) + 1); }
        else { awayRuns++; awayByInning.set(inning - 1, awayByInning.get(inning - 1) + 1); }
    }
    private void changeSides() {
        outs = 0; resetCount(); first = second = third = false;
        if (half == Half.TOP) {
            half = Half.BOTTOM;
        } else {
            if (inning >= scheduledInnings && homeRuns != awayRuns) { gameOver = true; return; }
            half = Half.TOP; inning++; ensureInning();
        }
    }
    private void ensureInning() {
        while (awayByInning.size() < inning) awayByInning.add(0);
        while (homeByInning.size() < inning) homeByInning.add(0);
    }
    private void resetCount() { balls = strikes = 0; }
    private void requirePlaying() { if (gameOver) throw new IllegalStateException("Game is over"); }

    public int scheduledInnings() { return scheduledInnings; }
    public int inning() { return inning; }
    public Half half() { return half; }
    public int balls() { return balls; }
    public int strikes() { return strikes; }
    public int outs() { return outs; }
    public int awayRuns() { return awayRuns; }
    public int homeRuns() { return homeRuns; }
    public int awayHits() { return awayHits; }
    public int homeHits() { return homeHits; }
    public boolean[] bases() { return new boolean[]{first, second, third}; }
    public boolean isTorontoBatting() { return half == Half.BOTTOM; }
    public boolean isGameOver() { return gameOver; }
    public LineScore lineScore() { return new LineScore(awayByInning, homeByInning, awayHits, homeHits); }
}
