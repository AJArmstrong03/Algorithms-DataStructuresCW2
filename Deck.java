package PrecisionDraw;

import java.util.Random;

/**
 * Deck Class
 * This represents a standard deck of 52 cards
 * It creates all cards in order, shuffles them, and allows one card to be dealt at a time
 *
 * Cards are stored in an array, and we deal from the "top" by decreasing the index each time
 */

public class Deck {

    private static final int NUMCARDS = 52;
    private final Card[] CARDS = new Card[NUMCARDS];
    private int cardIndex;

    /**
     * Builds new deck of unique cards
     * Rank index 0...12 (2...Ace)
     * Suit index 0...3 (Clubs...Spades
     */

    public Deck() {
        int arrayIndex = 0;

        //Build the deck for each suit and rank
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = 0; rank <13; rank++) {
                CARDS[arrayIndex] = new Card(rank, suit);
                arrayIndex++;
            }
        }

        //Start dealing from the end (top of deck)
        this.cardIndex = NUMCARDS - 1;

        //Shuffle the deck once when created
        this.shuffle();
    }

    /**
     * Random shuffle
     */

    public void shuffle() {
        Random rand = new Random();
        Card temp;
        int index;

        for (int i = CARDS.length -1; i > 0; i--) {
            index = rand.nextInt(i + 1);
            temp = CARDS[index];
            CARDS[index] = CARDS[i];
            CARDS[i] = temp;
        }
    }

    /**
     * Deals one card from the top of the deck
     * Returns the next card and moves the index down
     */

    public Card deal() {
        //No check for empty deck
        //The game logic will ensure we never run out
        return CARDS[cardIndex--];
    }

    //for quick test ONLY (Not in final program)
    public static void main(String[] args) {
        Deck d = new Deck();

        for (int i = 0; i < NUMCARDS; i++) {
            System.out.println(d.deal());
        }
    }
}
