package org.ensah.model;
import java.time.LocalDateTime;
public class Game {
    private int id;
    private int player1Id;
    private int player2Id;
    private Integer winnerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    // Constructeurs,getters et setters
    public Game(int player1Id,int player2Id){
        this.player1Id=player1Id;
        this.player2Id=player2Id;
    }
    // getters & setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
