package com.pixelpennant.engine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BaseballGameTest {
    @Test void threeStrikesRecordAnOutAndResetTheCount() {
        BaseballGame game = new BaseballGame(3);
        game.recordStrike(); game.recordStrike(); game.recordStrike();
        assertEquals(1, game.outs());
        assertEquals(0, game.strikes());
        assertEquals(0, game.balls());
    }

    @Test void fourBallsWalkTheBatterAndForceHomeARun() {
        BaseballGame game = new BaseballGame(3);
        walk(game); walk(game); walk(game); walk(game);
        assertEquals(1, game.awayRuns());
        assertArrayEquals(new boolean[]{true, true, true}, game.bases());
        assertEquals(0, game.balls());
    }

    @Test void singleAdvancesRunnersAndCountsHit() {
        BaseballGame game = new BaseballGame(3);
        game.recordHit(1); game.recordHit(1); game.recordHit(1); game.recordHit(1);
        assertEquals(1, game.awayRuns());
        assertEquals(4, game.awayHits());
    }

    @Test void threeOutsSwapSidesAndSixOutsAdvanceInning() {
        BaseballGame game = new BaseballGame(3);
        threeOuts(game);
        assertEquals(Half.BOTTOM, game.half());
        assertEquals(1, game.inning());
        assertTrue(game.isTorontoBatting());
        threeOuts(game);
        assertEquals(Half.TOP, game.half());
        assertEquals(2, game.inning());
    }

    @Test void gameEndsAfterChosenInningsWhenScoreIsNotTied() {
        BaseballGame game = new BaseballGame(3);
        game.recordHomeRun();
        for (int half = 0; half < 6; half++) threeOuts(game);
        assertTrue(game.isGameOver());
        assertEquals(3, game.lineScore().innings());
        assertEquals(1, game.awayRuns());
    }

    @Test void playerRatingsAreValidatedAndIdentityIsStable() {
        Player player = new Player("tor-01", "Maya", "Finch", Position.C,
                new PlayerRatings(72, 64, 81, 69, 75));
        assertEquals("Maya Finch", player.displayName());
        assertEquals(81, player.ratings().speed());
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerRatings(101, 50, 50, 50, 50));
    }

    @Test void androidModelsDoNotRequireRecordDesugaring() {
        assertFalse(Player.class.isRecord());
        assertFalse(PlayerRatings.class.isRecord());
        assertFalse(Team.class.isRecord());
        assertFalse(LineScore.class.isRecord());
    }

    @Test void immutableClassesRetainRecordStyleValueSemantics() {
        PlayerRatings ratings = new PlayerRatings(72, 64, 81, 69, 75);
        Player first = new Player("tor-01", "Maya", "Finch", Position.C, ratings);
        Player same = new Player("tor-01", "Maya", "Finch", Position.C,
                new PlayerRatings(72, 64, 81, 69, 75));

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertEquals("PlayerRatings[contact=72, power=64, speed=81, fielding=69, pitching=75]",
                ratings.toString());
        assertTrue(first.toString().startsWith("Player[id=tor-01, firstName=Maya"));
    }

    @Test void extraBaseHitsAndScoringAreTracked() {
        BaseballGame game = new BaseballGame(3);
        game.recordHit(2);
        assertEquals(1, game.awayDoubles());
        game.recordHit(3);
        assertEquals(1, game.awayTriples());
        assertEquals(1, game.lastRunsScored());
        assertEquals(1, game.lastRbi());
        game.recordHomeRun();
        assertEquals(1, game.awayHomers());
    }

    @Test void walksAndStrikeoutsAreTrackedByBattingTeam() {
        BaseballGame game = new BaseballGame(3);
        walk(game);
        assertEquals(1, game.awayWalks());
        game.recordStrike(); game.recordStrike(); game.recordStrike();
        assertEquals(1, game.awayStrikeouts());
    }

    @Test void homeTeamCanWinImmediatelyOnWalkOffHit() {
        BaseballGame game = new BaseballGame(3);
        for (int half = 0; half < 5; half++) threeOuts(game);
        assertEquals(Half.BOTTOM, game.half());
        assertEquals(3, game.inning());
        game.recordHomeRun();
        assertTrue(game.isGameOver());
        assertEquals(1, game.homeRuns());
        assertEquals(1, game.lastRbi());
    }

    private static void walk(BaseballGame game) {
        for (int i = 0; i < 4; i++) game.recordBall();
    }
    private static void threeOuts(BaseballGame game) {
        game.recordOut(); game.recordOut(); game.recordOut();
    }
}
