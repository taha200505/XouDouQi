package org.ensah;
import org.ensah.dao.DatabaseManager;
import org.ensah.model.*;
import org.ensah.service.*;
import org.ensah.util.ConsoleUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
public class XouDouQi {
    private final PlayerService playerService;
    private final GameService gameService;
    private final Scanner scanner;
    private Player currentPlayer1;
    private Player currentPlayer2;

    public XouDouQi() {
        this.playerService = new PlayerService();
        this.gameService = new GameService();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        XouDouQi game = new XouDouQi();
        try {
            game.run();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            DatabaseManager.closeConnection();
        }
    }

    private void run() throws SQLException {
        System.out.println("=== Xou Dou Qi (Jeu de la Jungle) ===");

        while (true) {
            System.out.println("\nMenu principal:");
            System.out.println("1. S'inscrire");
            System.out.println("2. Se connecter");
            System.out.println("3. Afficher le classement");
            System.out.println("4. Quitter");
            System.out.print("Choix: ");

            int choice = ConsoleUtils.readInt(scanner, 1, 4);

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    showLeaderboard();
                    break;
                case 4:
                    System.out.println("Au revoir !");
                    return;
            }
        }
    }

    private void register() throws SQLException {
        System.out.println("\n--- Inscription ---");
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        try {
            Player player = playerService.register(username, password);
            System.out.println("Inscription réussie !");
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void login() throws SQLException {
        System.out.println("\n--- Connexion ---");
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        try {
            Player player = playerService.login(username, password);
            System.out.println("Connexion réussie ! Bienvenue " + player.getUsername());

            if (currentPlayer1 == null) {
                currentPlayer1 = player;
                System.out.println("Joueur 1 connecté. En attente du joueur 2...");
            } else if (currentPlayer2 == null && !currentPlayer1.getUsername().equals(player.getUsername())) {
                currentPlayer2 = player;
                System.out.println("Joueur 2 connecté. Démarrage du jeu...");
                startGame();
                currentPlayer1 = null;
                currentPlayer2 = null;
            }
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void showLeaderboard() throws SQLException {
        System.out.println("\n--- Classement ---");
        List<Player> players = playerService.getLeaderboard();
        System.out.printf("%-20s %-10s %-10s %-10s%n", "Joueur", "Victoires", "Défaites", "Égalités");
        for (Player player : players) {
            System.out.printf("%-20s %-10d %-10d %-10d%n",
                    player.getUsername(),
                    player.getWins(),
                    player.getLosses(),
                    player.getDraws());
        }
    }

    private void startGame() throws SQLException {
        Game game = gameService.startGame(currentPlayer1.getId(), currentPlayer2.getId());
        System.out.println("\n--- Début de la partie ---");

        // Ici vous implémenteriez la logique du jeu
        // avec le plateau, les pièces, les tours, etc.

        // Exemple simplifié
        boolean gameOver = false;
        Player currentPlayer = currentPlayer1;

        while (!gameOver) {
            System.out.println("\nTour de " + currentPlayer.getUsername());
            System.out.print("Entrez votre coup (ou 'quit' pour abandonner): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                Player winner = (currentPlayer == currentPlayer1) ? currentPlayer2 : currentPlayer1;
                endGame(game, winner);
                return;
            }

            // Traitement du coup (simplifié)
            System.out.println("Coup joué: " + input);

            // Changement de joueur
            currentPlayer = (currentPlayer == currentPlayer1) ? currentPlayer2 : currentPlayer1;

            // Condition de victoire simulée
            if (Math.random() < 0.2) { // 20% de chance que la partie se termine
                gameOver = true;
                Player winner = (Math.random() < 0.5) ? currentPlayer1 : currentPlayer2;
                endGame(game, winner);
            }
        }
    }

    private void endGame(Game game, Player winner) throws SQLException {
        gameService.endGame(game.getId(), winner.getId());

        if (winner == currentPlayer1) {
            playerService.updateStats(currentPlayer1, currentPlayer2);
            System.out.println(currentPlayer1.getUsername() + " a gagné !");
        } else if (winner == currentPlayer2) {
            playerService.updateStats(currentPlayer2, currentPlayer1);
            System.out.println(currentPlayer2.getUsername() + " a gagné !");
        } else {
            playerService.updateDrawStats(currentPlayer1, currentPlayer2);
            System.out.println("Match nul !");
        }

        System.out.println("\n--- Statistiques ---");
        System.out.println(currentPlayer1.getUsername() + ": " +
                currentPlayer1.getWins() + "V/" +
                currentPlayer1.getLosses() + "D/" +
                currentPlayer1.getDraws() + "N");
        System.out.println(currentPlayer2.getUsername() + ": " +
                currentPlayer2.getWins() + "V/" +
                currentPlayer2.getLosses() + "D/" +
                currentPlayer2.getDraws() + "N");
    }
}