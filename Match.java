package PrecisionDraw;

import java.util.Random;
import java.util.Scanner;

/**
 * Match class (Lvl1-3 version)
 * This class is responsible
 * - Level 1: a single round for one player (testing)
 * - Level 2: a single round for two players (same deck)
 * - Level 3: a full 4-round match with:
 *      * dynamic target
 *      * ace optimisation
 *      * cumulative scores
 *      * match winner
 */

public class Match {

    private Deck deck;
    private int target;

    /**
     * Helper class to store the result of a single player's round:
     * - total: sum of card values after ace optimisation
     * - score: score based on rules and current target
     */
    private static class RoundResult {
        int total;
        int score;
    }

    /**
     * Constructor for Lvl1
     * Target starts at 40 and a new shuffled deck is created.
     */
    public Match() {
        this.target = 40;
        this.deck = new Deck(); //Will be replaced each round in full match
    }

    /**
     * Plays a single round for player
     * Now uses the same Ace optimisation logic that Level 3 requires
     *
     * @param player        the player taking the turn
     * @param cardsToDraw   how many cards the player wants to draw
     * @return              the score for this round
     */
    public int playSingleRound(Player player, int cardsToDraw) {
        RoundResult result = playRoundForPlayer(player, cardsToDraw);
        return result.score;
    }

    /**
     * Core method to play a round for one player with Ace optimisation (1 or 11) to minimise the difference to target
     *
     * @param player        player taking the turn
     * @param cardsToDraw   number of cards to draw
     * @return roundResult  containing total and score
     */
    private RoundResult playRoundForPlayer(Player player, int cardsToDraw) {

        int total = 0;
        int aceCount = 0;

        System.out.println("\n" + player.getName() + " will draw " + cardsToDraw + " cards. Target = " + target);

        //Draw the chosen number of cards
        for (int i = 0; i < cardsToDraw; i++) {
            Card c = deck.deal();
            System.out.println(" Card " + (i + 1) + ": " + c);

            //Start by counting each Ace as 11
            if (c.isAce()) {
                total += 11;
                aceCount++;
            } else {
                total += c.getValue();  // 2-10, J/Q/K = 10
            }
        }

        //Ace optimisation:
        //While we are over the target and still have aces counted as 11, turn one Ace from 11 to 1 (subtract 10) each time
        while (total > target && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        System.out.println("\nTotal value of cards = " + total);

        //Calculate the score using the rules:
        //If total <= target: Score = target - total
        //If total > target: Score = 2 * (total - target)
        //If total == target: Score is reduced by 5 (reward)
        int score;

        if (total <= target) {
            score = target - total;
            if (total == target) {
                score -= 5;     //Perfect guess reward
            }
        } else {
            score = 2 * (total - target);
        }

        System.out.println(player.getName() + "'s score for this round is: " + score);

        RoundResult result = new RoundResult();
        result.total = total;
        result.score = score;
        return result;
    }


    /**
     * Plays a single round for two players using the same deck
     * Each player chooses how many cards to draw
     * The player with the lower score wins (closer to target is better)
     */
    public void playTwoPlayerRound(Player player1, int cardsP1, Player player2, int cardsP2) {

        System.out.println("\n=== Starting Two-Player Round ===");

        //Player 1 takes their turn
        int score1 = playSingleRound(player1, cardsP1);

        //Player 2 takes their turn (Using the same deck)
        int score2 = playSingleRound(player2, cardsP2);

        System.out.println("\n=== Round Result ===");
        System.out.println(player1.getName() + " scored: " + score1);
        System.out.println(player2.getName() + " scored: " + score2);

        if (score1 < score2) {
            System.out.println(player1.getName() + " wins the round!");
            player1.addMatchesPlayed();
            player2.addMatchesPlayed();
            player1.addMatchesWon();
        } else if (score1 > score2) {
            System.out.println(player2.getName() + " wins the round!");
            player1.addMatchesPlayed();
            player2.addMatchesPlayed();
            player2.addMatchesWon();
        } else {
            System.out.println("The round is a draw.");
            player1.addMatchesPlayed();
            player2.addMatchesPlayed();
        }
    }

    /**
     * Level 3: Full 4-round match between two players
     * Features:
     * - 4 rounds per match
     * - Fresh shuffled deck at the start of each round
     * - Random first player in Round 1, then alternate each round
     * - Ace optimisation per hand
     * - Cumulative scores tracked across rounds
     * - Target dynamically adjusted after each round:
     *      * If both totals <= target: target += 5
     *      * If both totals > target: target -= 5
     *      * Otherwise: target stays the same
     * - Match winner: player with the lowest total score
     */
    public void playFullMatch(Player player1, Player player2, Scanner input) {

        Random rand = new Random();

        int totalScoreP1 = 0;
        int totalScoreP2 = 0;

        //Randomly choose the first player for round 1
        boolean player1GoesFirst = rand.nextBoolean();

        System.out.println("\n=== Starting Full 4-Round Match ===");
        System.out.println("Initial target = " + target);

        for (int round = 1; round <= 4; round++) {
            System.out.println("\n------------------------------");
            System.out.println("ROUND " + round + " (target = " + target + ")");
            System.out.println("------------------------------");

            //New deck each round (restocked + shuffled)
            this.deck = new Deck();

            //Determine order for this round
            Player firstPlayer;
            Player secondPlayer;

            if (player1GoesFirst) {
                firstPlayer = player1;
                secondPlayer = player2;
            } else {
                firstPlayer = player2;
                secondPlayer = player1;
            }

            System.out.println("First to play this round: " + firstPlayer.getName());

            //Guess phase
            System.out.print(firstPlayer.getName() + ", how many cards do you want to draw? ");
            int guessFirst = input.nextInt();

            System.out.print(secondPlayer.getName() + ", how many cards do you want to draw? ");
            int guessSecond = input.nextInt();

            //Draw + score phase
            RoundResult firstResult = playRoundForPlayer(firstPlayer, guessFirst);
            RoundResult secondResult = playRoundForPlayer(secondPlayer, guessSecond);

            //Update cumulative scores based on which actual Player is first/second
            if (firstPlayer == player1) {
                totalScoreP1 += firstResult.score;
                totalScoreP2 += secondResult.score;
            } else {
                totalScoreP2 += firstResult.score;
                totalScoreP1 += secondResult.score;
            }

            System.out.println("\nCumulative scores after Round " + round + ":");
            System.out.println(player1.getName() + " total score: " + totalScoreP1);
            System.out.println(player2.getName() + " total score: " + totalScoreP2);

            //Target update:
            //If both totals <= target: increase by 5
            //If both totals > target: decrease by 5
            //Otherwise: leave unchanged
            boolean firstOver = firstResult.total > target;
            boolean secondOver = secondResult.total > target;

            if (!firstOver && !secondOver) {
                target += 5;
                System.out.println("\nBoth players undershot or hit the target.");
                System.out.println("\nTarget increased to: " + target);
            } else if (firstOver && secondOver) {
                target -= 5;
                System.out.println("\nBoth players overshot the target.");
                System.out.println("\nTarget decreased to: " + target);
            } else {
                System.out.println("\nMixed results: target remains at " + target);
            }

            //Alternate starting player next round
            player1GoesFirst = !player1GoesFirst;
        }


        //Match finished - declare winner
        System.out.println("\n=== MATCH OVER ===");
        System.out.println(player1.getName() + " final score: " + totalScoreP1);
        System.out.println(player2.getName() + " final score: " + totalScoreP2);

        if (totalScoreP1 < totalScoreP2) {
            System.out.println(player1.getName() + " wins the match!");
            player1.addMatchesPlayed();
            player2.addMatchesPlayed();
            player1.addMatchesWon();
        } else if (totalScoreP1 > totalScoreP2) {
            System.out.println(player2.getName() + " wins the match!");
            player2.addMatchesPlayed();
            player1.addMatchesPlayed();
            player2.addMatchesWon();
        } else {
            System.out.println("The match was a draw.");
            player1.addMatchesPlayed();
            player2.addMatchesPlayed();
        }
    }

    /**
     * Level 6 full 4-round match between two simulated players
     * - X matches are controlled by Game.runSimulation()
     * - This method plays ONE match and returns:
     *      1 if player 1 wins
     *      2 if player 2 wins
     *      0 if it's a draw
     *
     * During simulation:
     *  - In each round, the first player guesses between 3 and 7 cards
     *  - The second player always guesses exactly 2 more cards than the first
     */
    public int playFullMatchSimulation(Player player1, Player player2, java.util.Random rand) {

        int totalScoreP1 = 0;
        int totalScoreP2 = 0;

        //Reset target for this match
        this.target = 40;

        //Randomly choose starting player
        boolean player1GoesFirst = rand.nextBoolean();

        for (int round = 1; round <= 4; round++) {
            System.out.println("\n------------------------------");
            System.out.println("[SIM] ROUND " + round + " (target = " + target + ")");
            System.out.println("------------------------------");

            //New deck each round
            this.deck = new Deck();

            Player firstPlayer;
            Player secondPlayer;

            if (player1GoesFirst) {
                firstPlayer = player1;
                secondPlayer = player2;
            } else {
                firstPlayer = player2;
                secondPlayer = player1;
            }

            System.out.println("[SIM] First to play this round: " + firstPlayer.getName());

            //Guess phase (automated)
            int guessFirst = rand.nextInt(5) + 3;   //3...7
            int guessSecond = guessFirst + 2;   // 2 more cards than first

            System.out.println("\n" + firstPlayer.getName() + " (SIM) will draw " + guessFirst + " cards.");
            RoundResult firstResult = playRoundForPlayer(firstPlayer, guessFirst);

            System.out.println("\n" + secondPlayer.getName() + " (SIM) will draw " + guessSecond + " cards.");
            RoundResult secondResult = playRoundForPlayer(secondPlayer, guessSecond);

            //Update scores
            if (firstPlayer == player1) {
                totalScoreP1 += firstResult.score;
                totalScoreP2 += secondResult.score;
            } else {
                totalScoreP2 += firstResult.score;
                totalScoreP1 += secondResult.score;
            }

            System.out.println("\n[SIM] Cumulative scores after Round " + round + ":");
            System.out.println(player1.getName() + " total score: " + totalScoreP1);
            System.out.println(player2.getName() + " total score: " + totalScoreP2);

            //Target update logic
            boolean firstOver = firstResult.total > target;
            boolean secondOver = secondResult.total > target;

            if (!firstOver && !secondOver) {
                target += 5;
                System.out.println("\n[SIM] Both players undershot or hit the target.");
                System.out.println("[SIM] Target increased to: " + target);
            } else if (firstOver && secondOver) {
                target -= 5;
                System.out.println("[SIM] Both players overshot the target.");
                System.out.println("[SIM] Target decreased to: " + target);
            } else {
                System.out.println("\n[SIM] Mixed results: target remains at " + target);
            }

            //Alternate first player
            player1GoesFirst = !player1GoesFirst;
        }

        //Decide match winner
        System.out.println("\n=== SIMULATION MATCH OVER ===");
        System.out.println(player1.getName() + " final score: " + totalScoreP1);
        System.out.println(player2.getName() + " final score: " + totalScoreP2);

        if (totalScoreP1 < totalScoreP2) {
            System.out.println(player1.getName() + " wins this simulated match!");
            return 1;
        } else if (totalScoreP2 < totalScoreP1) {
            System.out.println(player2.getName() + " wins this simulated match!");
            return 2;
        } else {
            System.out.println("This simulated match is a draw.");
            return 0;
        }
    }

    /**
     * Level 6 full 4-round match between a real player and a simulated player
     *  - realPlayer uses Scanner input to choose how many cards to draw each turn
     *  - simPlayer guesses automatically
     *      * If sim goes first: random 3...7 cards
     *      * If sim goes second: real guess + 2 cards (capped at 9 just in case)
     */
    public void playFullMatchAgainstSim(Player realPlayer, Player simPlayer, java.util.Scanner input, java.util.Random rand) {

        int totalScoreReal = 0;
        int totalScoreSim = 0;

        //Reset target for this match
        this.target = 40;

        //Randomly choose starting player
        boolean realGoesFirst = rand.nextBoolean();

        System.out.println("\n=== Full Match: " + realPlayer.getName() + " vs " + simPlayer.getName() + " ===");
        System.out.println("Initial target = " + target);
        System.out.println(realPlayer.getName() + (realGoesFirst ? " will go first." : " will go second."));

        for (int round = 1; round <= 4; round++) {
            System.out.println("\n------------------------------");
            System.out.println("ROUND " + round + " (target = " + target + ")");
            System.out.println("------------------------------");

            this.deck = new Deck();

            Player firstPlayer = realGoesFirst ? realPlayer : simPlayer;
            Player secondPlayer = realGoesFirst ? simPlayer : realPlayer;

            System.out.println("First to play this round: " + firstPlayer.getName());

            int guessFirst;
            int guessSecond;
            RoundResult firstResult;
            RoundResult secondResult;

            //First player's guess + round
            if (firstPlayer == realPlayer) {
                System.out.print(realPlayer.getName() + ", how many cards do you want to draw? ");
                guessFirst = input.nextInt();
                input.nextLine();   //clear new line
                firstResult = playRoundForPlayer(realPlayer, guessFirst);
            } else {
                //Sim goes first: guess 3...7
                guessFirst = rand.nextInt(5) + 3;
                System.out.println(simPlayer.getName() + " (SIM) will draw " +  guessFirst + " cards.");
                firstResult = playRoundForPlayer(simPlayer, guessFirst);
            }

            //Second player's guess + round
            if (secondPlayer == realPlayer) {
                System.out.print("\n" + realPlayer.getName() + ", how many cards do you want to draw? ");
                guessSecond = input.nextInt();
                input.nextLine();   //Clear new line
                secondResult = playRoundForPlayer(realPlayer, guessSecond);
            } else {
                //Sim goes second: 2 more than first player's guess (limit to 9 to be safe)
                guessSecond = guessFirst + 2;
                if (guessSecond > 9) {
                    guessSecond = 9;
                }
                System.out.println(simPlayer.getName() + " (SIM) will draw " +  guessSecond + " cards.");
                secondResult = playRoundForPlayer(simPlayer, guessSecond);
            }

            //Update cumulative scores
            if (firstPlayer == realPlayer) {
                totalScoreReal += firstResult.score;
                totalScoreSim += secondResult.score;
            } else {
                totalScoreSim += firstResult.score;
                totalScoreReal += secondResult.score;
            }

            System.out.println("\nCumulative scores after Round " + round + ":");
            System.out.println(realPlayer.getName() + " total score: " + totalScoreReal);
            System.out.println(simPlayer.getName() + " total score: " + totalScoreSim);

            //Target update
            boolean firstOver = firstResult.total > target;
            boolean secondOver = secondResult.total > target;

            if (!firstOver && !secondOver) {
                target +=5;
                System.out.println("\nBoth players undershot or hit the target.");
                System.out.println("[SIM] Target increased to: " + target);
            } else if (firstOver && secondOver) {
                target -= 5;
                System.out.println("\nBoth players overshot the target.");
                System.out.println("Target decreased to: " + target);
            } else {
                System.out.println("\nMixed results: target remains at " + target);
            }

            //Alternate starting player next round
            realGoesFirst = !realGoesFirst;
        }

        //Match finished - declare winner
        System.out.println("\n=== MATCH OVER (Real vs Sim) ===");
        System.out.println(realPlayer.getName() + " final score: " + totalScoreReal);
        System.out.println(simPlayer.getName() + " final score: " + totalScoreSim);

        realPlayer.addMatchesPlayed();  //Count as a real match in stats

        if (totalScoreReal < totalScoreSim) {
            System.out.println(realPlayer.getName() + " wins the match!");
            realPlayer.addMatchesWon();
        } else if (totalScoreReal > totalScoreSim) {
            System.out.println(simPlayer.getName() + " wins the match!");
        } else {
            System.out.println("The match was a draw.");
        }
    }


    /**
     * Quick Lvl3 test harness
     * - Two player names
     * - Full 4-round match
     */
    //public static void main(String[] args) {

        //Scanner input = new Scanner(System.in);

        //System.out.println("=== Precision Draw: Level 3 Full Match ===");

        //Get player names
        //System.out.print("Enter player 1 name: ");
        //String name1 = input.nextLine();
        //Player player1 = new Player(name1);

        //System.out.print("Enter player 2 name: ");
        //String name2 = input.nextLine();
        //Player player2 = new Player(name2);

        //Match match =  new Match();
        //match.playFullMatch(player1, player2, input);
    //}
}
