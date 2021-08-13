package com.techelevator.dao;

import com.techelevator.model.*;
import com.techelevator.services.WebApiService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JdbcGameDao implements GameDao{

    // objects to access database
    private JdbcTemplate jdbcTemplate;


    public JdbcGameDao (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int createGame(Game game) {
        int newGameId = 0;

        if(this.findGameByName(game.getGameName())) {
            String games = "INSERT INTO games (game_name, host, end_date) " +
                    "VALUES (?, ?, ?) RETURNING game_id;";
            String userStatus = "INSERT INTO user_status (game_id, username, user_status) " +
                    "VALUES (? ,?, ?);";
            String balances = "INSERT INTO balances (game_id, username) VALUES (?, ?);";
            String status = "Accepted";
            try {
                newGameId = jdbcTemplate.queryForObject(games, Integer.class, game.getGameName(), game.getHost(), LocalDate.parse(game.getEndDate()));
                jdbcTemplate.update(userStatus, newGameId, game.getHost(), status);
                jdbcTemplate.update(balances, newGameId, game.getHost());
            } catch (DataAccessException e) {
                System.out.println("Error accessing data " + e.getMessage());
            }
        }
        return newGameId;
    }

    @Override
    public List<Game> viewGames(String username, String status) {
        // viewGames returns with a list of games
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.game_id, g.game_name, g.game_active, g.host, g.start_date, g.end_date " +
                "FROM games g " +
                "JOIN user_status s ON g.game_id = s.game_id " +
                "JOIN users u ON s.username = u.username " +
                // we show only games user have joined
                "WHERE u.username = ? AND s.user_status = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, status);
            while (results.next()) {
                // helper method converts database response into Game object
                Game game = mapRowToGame(results);
                // adding each game object to the list
                games.add(game);
            }
        } catch (DataAccessException e) {
            System.out.println(e);
        }

        return games;
    }

    @Override
    public List<Player> viewUsersInTheGame(int gameId, String username) {
        List<Player> users = new ArrayList<>();
        String sql = "SELECT game_id, username, user_status FROM user_status WHERE game_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, gameId);
            while (results.next()) {
                Player player = mapRowToPlayer(results);
                users.add(player);
            }
        } catch (DataAccessException e) {
            System.out.println(e);
        }

        return users;
    }

    @Override
    public boolean invitePlayers(String username, String status, int gameId, String currentUser) {
        // returning false if method fails
        boolean result = false;
        String sql;
        // default status for invited players - "Pending"
        if (status.equals("Pending") && findGameByGameId(gameId).getHost().equals(currentUser)) {
            sql = "INSERT INTO user_status (game_id, username, user_status) VALUES (?, ?, ?);";
            String sqlToBalance = "INSERT INTO balances (game_id, username) VALUES (?, ?);";
            try {
                jdbcTemplate.update(sql, gameId, username, status);
                jdbcTemplate.update(sqlToBalance, gameId, username);
                // if successful - turning result boolean to true
                result = true;
            } catch (DataAccessException e) {
                System.out.println("Error accessing data " + e.getMessage());
            }
        } else if (status.equals("Accepted") && username.equals(currentUser)) {
            sql = "UPDATE user_status SET user_status = 'Accepted' WHERE username = ? AND game_id = ?;";
            try {
                jdbcTemplate.update(sql, username, gameId);
                // if successful - turning result boolean to true
                result = true;
            } catch (DataAccessException e) {
                System.out.println("Error accessing data " + e.getMessage());
            }
        } else if (status.equals("Declined") && username.equals(currentUser)) {
            sql = "UPDATE user_status SET user_status = 'Declined' WHERE username = ? AND game_id = ?;";
            try {
                jdbcTemplate.update(sql, username, gameId);
                // if successful - turning result boolean to true
                result = true;
            } catch (DataAccessException e) {
                System.out.println("Error accessing data " + e.getMessage());
            }
        }
        return result;
    }


    @Override
    // this method created to help createGame method identify if game name available
    public boolean findGameByName(String gameName) {
        // default value true confirming that name available
        boolean result = true;
        String sql = "SELECT game_name FROM games WHERE game_name = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, gameName);
            // checking if our query has returned with anything
            if (results.next()) {
                // if there is a game with that name - turning our boolean to false meaning this name is not available
                result = false;
            }
        } catch (DataAccessException e) {
            System.out.println(e);
        }

        return result;
    }

    @Override
    public Game findGameByGameId(int gameId) {
        Game game = new Game();
        String sql = "SELECT game_id, game_name, game_active, host, start_date, end_date FROM games WHERE game_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, gameId);
            if(results.next()) {
                game = mapRowToGame(results);
            }
        } catch (DataAccessException e) {
            System.out.println("Data Access error! " + e.getMessage());
        }
        return game;
    }

    @Override
    public List<Balance> getBalancesByGameId(int gameId, String username) {
        List<Balance> balances = new ArrayList<>();
        String sql = "SELECT balance_id, game_id, username, amount FROM balances WHERE game_id = ? AND username = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, gameId, username);
            while(results.next()) {
                Balance balance = mapRowToBalance(results);
                balances.add(balance);
            }
        } catch (DataAccessException j) {
            System.out.println("Data Access error! " + j.getMessage());
        }
        return balances;
    }

    @Override
    public List<Balance> leaderboard(int gameId, String username) {
        List<Balance> leaders = new ArrayList<>();
        boolean userInTheGame = false;

        WebApiService price = new WebApiService();
         List<Player> users = new ArrayList<>();
        //getting list of players in the game
        String sqlForUsersInGame = "SELECT username, game_id, user_status " +
                "FROM user_status " +
                "WHERE game_id = ? AND user_status = 'Accepted';";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sqlForUsersInGame, gameId);
            while (results.next()) {
                Player player = mapRowToPlayer(results);
                users.add(player);
                if(player.getUsername().equals(username)) {
                    userInTheGame = true;
                }
            }
        } catch (DataAccessException e) {
            System.out.println(e);
        }

        if(userInTheGame) {
            // getting the list of all stocks in the game
//        String sqlForTickersInGame = "SELECT stock_ticker, shares FROM trades WHERE game_id = ?;";
            String sqlForTickersInGame = "SELECT stock_ticker, shares FROM stocks WHERE game_id = ? AND shares > 0";
            List<Share> allStocksInGame = new ArrayList<>();
            try {
                SqlRowSet results = jdbcTemplate.queryForRowSet(sqlForTickersInGame, gameId);
                while (results.next()) {
                    Share share = mapRowToShare(results);
                    System.out.println("Line 196 Printing share added to shares list " + share.getTickerName());
                    allStocksInGame.add(share);
                }
            } catch (DataAccessException e) {
                System.out.println("Data access error! " + e.getMessage());
            }

            // Now I need to create map to fill with tickers and the prices
            Map<String, BigDecimal> sharePrices = new HashMap<>();
            for (Share share : allStocksInGame) {
                try {
                    System.out.println("Line 207 printing share price of which I am calling to external API " + share.getTickerName());
                    BigDecimal sharePrice = price.getPrice(share.getTickerName(), false).getPrice();
                    System.out.println("Line 209 printing sharePrice " + sharePrice);
                    sharePrices.put(share.getTickerName(), sharePrice);
                } catch (RestClientException e) {
                    System.out.println("External API is broke! " + e.getMessage());
                }
            }
            //looping through list of users to get their cash balance and stock balance
            for (Player player : users) {
                List<Share> shares = new ArrayList<>();
                Balance playersTotalBalance = new Balance();
                BigDecimal cashBalance = new BigDecimal("0");
                BigDecimal stockBalance = new BigDecimal("0");

                //getting cash balance
                String sqlForCashBalance = "SELECT balance_id, game_id, username, amount FROM balances WHERE game_id = ? AND username = ?;";
                try {
                    SqlRowSet b = jdbcTemplate.queryForRowSet(sqlForCashBalance, gameId, player.getUsername());
                    if (b.next()) {
                        Balance usersBalance = mapRowToBalance(b);
                        cashBalance = usersBalance.getAmount();
                    }
                } catch (DataAccessException e) {
                    System.out.println(e);
                }

                // getting tickers and number of shares player owns
//            String sqlForSharesOwnsAndNumber = "SELECT stock_ticker, shares FROM trades WHERE game_id = ? AND username = ?;";
                String sqlForSharesOwnsAndNumber = "SELECT stock_ticker, shares FROM stocks WHERE game_id = ? AND shares > 0 AND username = ?";
                try {
                    SqlRowSet results = jdbcTemplate.queryForRowSet(sqlForSharesOwnsAndNumber, gameId, player.getUsername());
                    while (results.next()) {
                        Share share = mapRowToShare(results);
                        shares.add(share);
                    }
                    //looping through each stock and adding the amount to stock balance
                    for (Share share : shares) {
                        BigDecimal totalPrice = price.getPrice(share.getTickerName(), false).getPrice().multiply(new BigDecimal(share.getNumber()));
                        stockBalance = stockBalance.add(totalPrice);
                    }
                } catch (RestClientException j) {
                    System.out.println("Access to api error! " + j.getMessage());
                }
                playersTotalBalance.setUsername(player.getUsername());
                // adding total amount to balance object (adding stock balance to cash balance)
                playersTotalBalance.setAmount(cashBalance.add(stockBalance));
                leaders.add(playersTotalBalance);
            }
        }
        return leaders;
    }

    @Override
    public List<Balance> getHighScores() {
        List<Balance> highScores = new ArrayList<>();
        String sql = "SELECT b.balance_id, b.game_id, b.username, b.amount " +
                "FROM balances b " +
                "JOIN games g ON b.game_id = g.game_id " +
                "WHERE g.game_active = false " +
                "ORDER BY b.amount DESC " +
                "LIMIT 10;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Balance score = mapRowToBalance(results);
                highScores.add(score);
            }
        } catch (RestClientException j) {
            System.out.println("Access to api error! " + j.getMessage());
        }
        return highScores;
    }

    // helper method to create Share object
    private Share mapRowToShare (SqlRowSet s) {
        Share share = new Share();
        share.setTickerName(s.getString("stock_ticker"));
        share.setNumber(s.getInt("shares"));
        return share;
    }

    @Override
    public void changeGameStatusByGameId(int gameId) {
        String sql = "UPDATE games SET game_active = false WHERE game_id = ? AND game_active = true ;";
        try {
            jdbcTemplate.update(sql, gameId);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    //helper method to create Player object from database response
    private Player mapRowToPlayer (SqlRowSet p) {
        Player player = new Player();
        player.setUsername(p.getString("username"));
        player.setGameId(p.getInt("game_id"));
        player.setStatus(p.getString("user_status"));
        return player;
    }

    // helper method to create Game object from database response
    private Game mapRowToGame(SqlRowSet g) {
        Game game = new Game();
        game.setGameId(g.getInt("game_id"));
        game.setGameName(g.getString("game_name"));
        game.setGameActive(g.getBoolean("game_active"));
        game.setHost(g.getString("host"));
        game.setStartDate(g.getString("start_date"));
        game.setEndDate(g.getString("end_date"));
        return game;
    }
    // helper method to create Balance object
    private Balance mapRowToBalance(SqlRowSet b) {
        Balance balance = new Balance();
        balance.setBalanceId(b.getInt("balance_id"));
        balance.setGameId(b.getInt("game_id"));
        balance.setUsername(b.getString("username"));
        balance.setAmount(b.getBigDecimal("amount"));
        return balance;
    }


}
