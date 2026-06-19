package PrecisionDraw;

/**
 * Card class
 * This represents a single playing card
 * A card has:
 * Rank ( 2 - 10, Jack, Queen, King, Ace)
 * Suit (Clubs, Diamonds, Hearts, Spades)
 *
 * This class stores:
 * Rank as a number (getRank)
 * Suit as a number (getSuit)
 * Value used for scoring (getValue)
 * String (toString)
 */

public class Card {

    //Index for Rank: 0 = "2", ..., 8 = "10", 9 = "Jack", 10 = "Queen", 11 = "King", 12 = "Ace"
    private final int RANK;
    //Index for Suit: 0 = "Clubs", 1 = "Diamonds", 2 = "Hearts", 3 = "Spades"
    private final int SUIT;

    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    private static final String[] SUITS = {"Clubs", "Diamonds", "Hearts", "Spades"};

    public Card(int r, int s) {
        this.RANK = r;
        this.SUIT = s;
    }

    public String getRank() {
        return RANKS[RANK];
    }
    public String getSuit() {
        return SUITS[SUIT];
    }

    /**
     * Returns internal index
     * 0 = "2", ..., 12 = "Ace"
     */
    public int getRankIndex() {
        return RANK;
    }

    /**
     * Returns value for this card:
     * 2...10 -> 2...10
     * J/Q/K -> 10
     * Ace -> 1 (Ace = 11 in Match Logic)
     */
    public int getValue() {
        if (RANK <= 8) {
            // "2" to "10": index 0...8
            return RANK + 2;
        } else if (RANK <= 11) {
            //Jack, Queen, King
            return 10;
        } else {
            //Ace
            return 1;       // Ace = 1 by default
        }
    }

    /**
     * Returns true if card is an Ace
     */
    public boolean isAce() {
        return RANK == 12;  //Index of Ace in RANKS array
    }

    @Override
    public String toString() {
        return getRank() + " of " + getSuit();
    }
}
