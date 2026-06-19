package PrecisionDraw;

/**
 * Player class
 * This class stores information about a player in the game
 * It keeps track of:
 * - Player's name
 * - Number of matches played
 * - Number of matches won
 *
 * Other stats can be added later E.g. (Total points can be added later)
 */

public class Player {

    private String name;

    //basic statistic for later levels (Leaderboard + simulation)
    private int matchesPlayed;
    private int matchesWon;

    /**
     * Creates a new player with the given name
     * Stats start at 0
     */
    public Player (String name) {
        this.name = name;
        this.matchesPlayed = 0;
        this.matchesWon = 0;
    }

    /**
     * Returns the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Increments the number of matches this player has played
     */
    public void addMatchesPlayed() {
        matchesPlayed++;
    }

    /**
     * Increments the number of matches this player has won
     */
    public void addMatchesWon() {
        matchesWon++;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    /**
     * Win percentage (For leaderboard ordering later on)
     * If no matches played, return 0
     */
    public double getWinRate() {
        if (matchesPlayed == 0) {
            return 0;
        }
        return (matchesWon * 100.0) / matchesPlayed;
    }

    /**
     * Basic string representation of the player
     */
    @Override
    public String toString() {
        return name + " (Wins: " + matchesWon + ", Played: " +  matchesPlayed + ", Win Rate: " + String.format("%.1f", getWinRate()) + "%)";
    }
}
