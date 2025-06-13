package org.ensah.service;
import org.ensah.dao.GameDAO;
import org.ensah.model.Game;
import java.sql.SQLException;
import java.util.List;
public class GameService {
    private final GameDAO gameDAO;

    public GameService() {
        this.gameDAO = new GameDAO();
    }

    public Game startGame(int player1Id, int player2Id) throws SQLException {
        return gameDAO.createGame(player1Id, player2Id);
    }

    public void endGame(int gameId, Integer winnerId) throws SQLException {
        gameDAO.endGame(gameId, winnerId);
    }

    public List<Game> getPlayerHistory(int playerId) throws SQLException {
        return gameDAO.getPlayerGames(playerId);
    }
}
