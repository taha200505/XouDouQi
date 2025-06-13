package org.ensah.service;
import org.ensah.dao.PlayerDAO;
import org.ensah.model.Player;
import java.sql.SQLException;
import java.util.List;
public class PlayerService {
    private final PlayerDAO playerDAO;

    public PlayerService() {
        this.playerDAO = new PlayerDAO();
    }

    public Player register(String username, String password) throws SQLException {
        if (playerDAO.getPlayerByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        return playerDAO.createPlayer(username, password);
    }

    public Player login(String username, String password) throws SQLException {
        Player player = playerDAO.getPlayerByUsername(username);
        if (player == null || !player.getPassword().equals(password)) { // Comparaison directe
            throw new IllegalArgumentException("Invalid username or password");
        }
        return player;
    }

    public List<Player> getLeaderboard() throws SQLException {
        return playerDAO.getAllPlayers();
    }

    public void updateStats(Player winner, Player loser) throws SQLException {
        winner.setWins(winner.getWins() + 1);
        loser.setLosses(loser.getLosses() + 1);
        playerDAO.updatePlayerStats(winner);
        playerDAO.updatePlayerStats(loser);
    }

    public void updateDrawStats(Player player1, Player player2) throws SQLException {
        player1.setDraws(player1.getDraws() + 1);
        player2.setDraws(player2.getDraws() + 1);
        playerDAO.updatePlayerStats(player1);
        playerDAO.updatePlayerStats(player2);
    }
}