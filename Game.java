package PrecisionDraw;

import java.util.Scanner;

/**
 * Game class (Lvl4)
 *
 * This class provides a simple menu system for the Precision Draw Game
 * It allows the user to:
 * - Create players
 * - See a list of players
 * - Play a full match between two players
 * - View a basic leaderboard based on matches won
 */
public class Game {

    public static final int MAX_PLAYERS = 10;

    private Player[] players = new Player[MAX_PLAYERS];
    private int numPlayers = 0;

    private Scanner input = new Scanner(System.in);

    /**
     * Main game loop
     */
    public void run() {

        int choice;

        do {
            System.out.println("\n=== Precision Draw Game Menu ===");
            System.out.println("1. Play a match");
            System.out.println("2. Add a player");
            System.out.println("3. List of players");
            System.out.println("4. View leaderboard");
            System.out.println("5. Compare two players");
            System.out.println("6. List of players with more than (x) match  wins");
            System.out.println("7. Run a simulation");
            System.out.println("8. Play match vs simulated player");
            System.out.println("0. Exit");
            System.out.print("\nChoose an option: ");

            choice = readInt();

            switch (choice) {
                case 1:
                    playMatch();
                    break;
                case 2:
                    addPlayer();
                    break;
                case 3:
                    listPlayers();
                    break;
                case 4:
                    showLeaderboard();
                    break;
                case 5:
                    compareTwoPlayers();
                    break;
                case 6:
                    listPlayersWithMinWins();
                    break;
                case 7:
                    runSimulation();
                    break;
                case 8:
                    playMatchVsSim();
                    break;
                case 0:
                    System.out.println("\nExiting game. Goodbye!");
                    break;
                default:
                    System.out.println("\nInvalid choice. Try again.");
            }
        } while (choice != 0);
    }

    /**
     * Safely read an int from the user
     */
    private int readInt() {
        while (!input.hasNextInt()) {
            System.out.print("\nPlease enter a number: ");
            input.next();   //Discard invalid input
        }
        int value = input.nextInt();
        input.nextLine();   //Clear end of line
        return value;
    }

    /**
     * Lets the user choose two players and plays a full match
     */
    private void playMatch() {
        if (numPlayers < 2) {
            System.out.println("\nYou need at least two players to play a match. \nAdd players in: 2. Add a player");
            return;
        }

        System.out.println("\nChoose Player 1: ");
        Player p1 = selectPlayer();
        if (p1 == null) return;

        System.out.println("\nChoose Player 2: ");
        Player p2 = selectPlayer();
        if (p2 == null) return;

        if (p1 == p2) {
            System.out.println("\nYou must choose two different players.");
            return;
        }

        Match match = new Match();
        match.playFullMatch(p1, p2, input);
    }

    /**
     * Helper to select a player by name
     * Keeps asking until a valid name is entered
     */
    private Player selectPlayer() {
        if (numPlayers == 0) {
            System.out.println("\nNo players have been added yet");
            return null;
        }

        while (true) {
            System.out.print("Enter player name: ");
            String name = input.nextLine().trim();

            for (int i = 0; i < numPlayers; i++) {
                if (players[i].getName().equalsIgnoreCase(name)) {
                    return players[i];
                }
            }

            System.out.println("No player found with that name. Please try again.");
        }
    }

    /**
     * Add a new player if there is space
     */
    private void addPlayer() {
        if (numPlayers >= MAX_PLAYERS) {
            System.out.println("\nPlayer list is full. Cannot add more players.");
            return;
        }

        while (true) {
            System.out.print("\nEnter new player name: ");
            String name = input.nextLine().trim();


            //Check for duplicates
            boolean exists = false;
            for (int i = 0; i < numPlayers; i++) {
                if (players[i].getName().equalsIgnoreCase(name)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                System.out.println("\nA player with that name already exists. \nPlease choose a different name.");
                //Loop continues, ask again
            } else {
                players[numPlayers] = new Player(name);
                numPlayers++;
                System.out.println("\nPlayer \"" + name + "\" added.");
                //Leave the loop and return to the menu
                break;
            }
        }
    }

    /**
     * Prints all players and their basic stats
     */
    private void listPlayers() {
        if (numPlayers == 0) {
            System.out.println("\nNo players have been added yet.");
            return;
        }

        System.out.println("\n=== Players ===");
        for (int i = 0; i < numPlayers; i++) {
            System.out.println((i + 1) + ". " + players[i]);
        }
    }

    /**
     * Simple leaderboard based on matches won
     * Uses a copy of the players array and sorts it by wins (descending)
     */
    private void showLeaderboard() {
        if (numPlayers == 0) {
            System.out.println("\nNo players to show on the leaderboard.");
            return;
        }

        //Copy current players into a temporary array
        Player[] leaderboard = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            leaderboard[i] = players[i];
        }

        //Simple selection sort by matches won (descending)
        for (int i = 0; i < numPlayers - 1; i++) {
            int bestIndex = i;
            for (int j = i + 1; j < numPlayers; j++) {
                if (leaderboard[j].getMatchesWon() > leaderboard[bestIndex].getMatchesWon()) {
                    bestIndex = j;
                }
            }

            //Swap
            if (bestIndex != i) {
                Player temp = leaderboard[i];
                leaderboard[i] = leaderboard[bestIndex];
                leaderboard[bestIndex] = temp;
            }
        }

        System.out.println("\n=== Leaderboard (by matches won) ===");
        for (int i = 0; i < numPlayers; i++) {
            Player p =  leaderboard[i];
            System.out.println((i + 1) + ". " + p.getName() + " - Wins: " + p.getMatchesWon() + ", Played: " + p.getMatchesPlayed());
        }
    }

    /**
     * Level 5 compare two players
     * Shows matches played, wins and win percentage for each
     */
    private void compareTwoPlayers() {
        if (numPlayers < 2) {
            System.out.println("\nYou need at least two players to compare.");
            return;
        }

        System.out.println("\n=== Compare two players ===");

        System.out.println("\nSelect first player: ");
        Player p1 = selectPlayer();
        if (p1 == null) return;

        System.out.println("\nSelect second player: ");
        Player p2 = selectPlayer();
        if (p2 == null) return;

        if (p1 == p2) {
            System.out.println("\nYou must choose two different players.");
            return;
        }

        int played1 = p1.getMatchesPlayed();
        int played2 = p2.getMatchesPlayed();
        int wins1 = p1.getMatchesWon();
        int wins2 = p2.getMatchesWon();

        double winPct1 = (played1 > 0) ? (wins1 * 100.0 / played1) : 0.0;
        double winPct2 = (played2 > 0) ? (wins2 * 100.0 / played2) : 0.0;

        System.out.println("\n--- Player 1 ---");
        System.out.println("Name: " + p1.getName());
        System.out.println("MatchesPlayed: " + played1);
        System.out.println("Matches won: " + wins1);
        System.out.println("Win percentage: " + String.format("%.1f", winPct1) + "%");

        System.out.println("\n--- Player 2 ---");
        System.out.println("Name: " + p2.getName());
        System.out.println("MatchesPlayed: " + played2);
        System.out.println("Matches won: " + wins2);
        System.out.println("Win percentage: " + String.format("%.1f", winPct2) + "%");

        if (winPct1 > winPct2) {
            System.out.println("\n" + p1.getName() + " currently has the better win rate.");
        } else if (winPct2 > winPct1) {
            System.out.println("\n" + p2.getName() + " currently has the better win rate.");
        } else {
            System.out.println("\nBoth players currently have the same win rate.");
        }
    }

    /**
     * Level 5 list players who have more than x match wins
     * Shows only players with > x wins
     */
    private void listPlayersWithMinWins() {
        if (numPlayers == 0) {
            System.out.println("\nNo players have been added yet.");
            return;
        }

        System.out.print("\nEnter minimum number of match wins (x): ");
        int x = readInt();

        System.out.println("\nPlayers with more than " + x + " match wins: ");
        boolean anyFound = false;

        for (int i = 0; i < numPlayers; i++) {
            Player p = players[i];
            if (p.getMatchesWon() > x) {
                System.out.println("-" + p.getName() + " (Wins: " + p.getMatchesWon() + ", Played: " + p.getMatchesPlayed() + ")");
                anyFound = true;
            }
        }

        if (!anyFound) {
            System.out.println("No players currently have more than " + x + " wins.");
        }
    }

    /**
     * Level 6 Run a simulation of x matches between two simulated players.
     * Simulated players are NOT added to the main player list
     */
    private void runSimulation() {
        System.out.print("\nHow many matches should be simulated? ");
        int numMatches = readInt();

        if (numMatches <= 0) {
            System.out.println("\nNumber of matches must be positive.");
            return;
        }

        //Simulated players (not stored in players[])
        Player sim1 = new Player("SimPlayer01");
        Player sim2 = new Player("SimPlayer02");

        int sim1Wins = 0;
        int sim2Wins = 0;
        int draws = 0;

        java.util.Random rand = new java.util.Random();
        for (int i = 1; i <= numMatches; i++) {
            System.out.println("\n=== Simulation Match " + i + "===");
            Match match = new Match();

            //This will be a new method in Match
            int result = match.playFullMatchSimulation(sim1, sim2, rand);

            if (result == 1) {
                sim1Wins++;
            } else if (result == 2) {
                sim2Wins++;
            } else {
                draws++;
            }
        }

        System.out.println("\n=== Simulation Results ===");
        System.out.println("Matches simulated: " + numMatches);
        System.out.println(sim1.getName() + " wins: " + sim1Wins);
        System.out.println(sim2.getName() + " wins: " + sim2Wins);
        System.out.println("Draws: " + draws);
    }

    /**
     * Level 6 Lets a real player play a full match against a simulated player
     */
    private void playMatchVsSim() {
        if (numPlayers < 1) {
            System.out.println("\nYou need at least one real player to play against a simulated player. \nAdd players in 2. Add a player");
            return;
        }

        System.out.println("\nChoose the real players: ");
        Player realPlayer = selectPlayer();
        if (realPlayer == null) return;

        //Create a simulated opponent with the name SimPlayerYY where YY is 1...10
        java.util.Random rand = new java.util.Random();
        int simNumber = rand.nextInt(10) + 1;   // 1...10
        String simName = "SimPlayer" + (simNumber < 10 ? "0" + simNumber : simNumber);
        Player simPlayer = new Player(simName);

        System.out.println("\nStarting match: " + realPlayer.getName() + " vs " + simPlayer.getName());

        Match match = new Match();
        match.playFullMatchAgainstSim(realPlayer, simPlayer, input, rand);
    }

    /**
     * Main entry point for the whole game
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }
}
