package org.ensah.dao;
import org.ensah.model.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PlayerDAO {
    public Player createPlayer(String username, String password) throws SQLException {
        String sql = "INSERT INTO players (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Player player = new Player(username, password);
                    player.setId(rs.getInt(1));
                    return player;
                }
            }
        }
        return null;
    }

    public Player getPlayerByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM players WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Player player = new Player();
                    player.setId(rs.getInt("id"));
                    player.setUsername(rs.getString("username"));
                    player.setPassword(rs.getString("password")); // Vérifiez ceci
                    return player;
                }
            }
        }
        return null;
    }

    public void updatePlayerStats(Player player) throws SQLException {
        String sql = "UPDATE players SET wins = ?, losses = ?, draws = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, player.getWins());
            pstmt.setInt(2, player.getLosses());
            pstmt.setInt(3, player.getDraws());
            pstmt.setInt(4, player.getId());
            pstmt.executeUpdate();
        }
    }

    public List<Player> getAllPlayers() throws SQLException {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY wins DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setWins(rs.getInt("wins"));
                player.setLosses(rs.getInt("losses"));
                player.setDraws(rs.getInt("draws"));
                players.add(player);
            }
        }
        return players;
    }
}
