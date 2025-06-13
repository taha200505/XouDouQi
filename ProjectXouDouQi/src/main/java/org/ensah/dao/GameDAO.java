package org.ensah.dao;
import org.ensah.model.Game;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class GameDAO {
    public Game createGame(int player1Id, int player2Id) throws SQLException {
        String sql = "INSERT INTO games (player1_id, player2_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, player1Id);
            pstmt.setInt(2, player2Id);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Game game = new Game(player1Id, player2Id);
                    game.setId(rs.getInt(1));
                    return game;
                }
            }
        }
        return null;
    }

    public void endGame(int gameId, Integer winnerId) throws SQLException {
        String sql = "UPDATE games SET winner_id = ?, end_time = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, winnerId, Types.INTEGER);
            pstmt.setInt(2, gameId);
            pstmt.executeUpdate();
        }
    }

    public List<Game> getPlayerGames(int playerId) throws SQLException {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games WHERE player1_id = ? OR player2_id = ? ORDER BY start_time DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Game game = new Game(
                            rs.getInt("player1_id"),
                            rs.getInt("player2_id"));
                    game.setId(rs.getInt("id"));
                    game.setWinnerId(rs.getObject("winner_id", Integer.class));
                    game.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    Timestamp endTime = rs.getTimestamp("end_time");
                    if (endTime != null) {
                        game.setEndTime(endTime.toLocalDateTime());
                    }
                    games.add(game);
                }
            }
        }
        return games;
    }
}