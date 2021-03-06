package com.techelevator.model;

public class Player {

    private String username;
    private int gameId;
    private String status;

    public Player() {

    }

    public Player(String username, String status) {
        this.username = username;
        this.status = status;
    }


    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
