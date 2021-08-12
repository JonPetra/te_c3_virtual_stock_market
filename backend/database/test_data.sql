TRUNCATE users, games, user_status, balances, trades CASCADE;

INSERT INTO users (user_id, username, password_hash, role)
VALUES (1001, 'testuser1', '$2a$10$.iJYmcFRAeBIJArsAvGwi.vWIs34aU8ddFBd5DK2FEPu9lpq/PdqC', 'ROLE_USER'),
       (1002, 'testuser2', '$2a$10$.iJYmcFRAeBIJArsAvGwi.vWIs34aU8ddFBd5DK2FEPu9lpq/PdqC', 'ROLE_USER'),
       (1003, 'testuser3', '$2a$10$.iJYmcFRAeBIJArsAvGwi.vWIs34aU8ddFBd5DK2FEPu9lpq/PdqC', 'ROLE_USER');

INSERT INTO games (game_id, game_name, game_active, host, start_date, end_date) 
VALUES (2001, 'testgame1', FALSE, 'testuser1', '2021-08-01', '2021-08-02'),
       (2002, 'testgame2', FALSE, 'testuser2', '2021-08-02', '2021-08-03'),
       (2003, 'testgame3', FALSE, 'testuser1', '2021-08-08', '2021-08-10'),
       (2004, 'testgame4', TRUE, 'testuser1', '2021-08-12', '2021-08-14'),
       (2005, 'testgame5', TRUE, 'testuser2', '2021-08-12', '2021-08-14'),
       (2006, 'testgame6', TRUE, 'testuser3', '2021-08-12', '2021-08-14');

INSERT INTO user_status (game_id, username, user_status)
VALUES (2001, 'testuser1', 'Accepted'),
       (2001, 'testuser2', 'Accepted'),
       
       (2002, 'testuser2', 'Accepted'),
       (2002, 'testuser1', 'Accepted'),
       
       (2003, 'testuser1', 'Accepted'),
       (2003, 'testuser3', 'Accepted'),
       
       (2004, 'testuser1', 'Accepted'),
       (2004, 'testuser2', 'Accepted'),
       
       (2005, 'testuser2', 'Accepted'),
       (2005, 'testuser1', 'Accepted'),
       
       (2006, 'testuser3', 'Accepted'),
       (2006, 'testuser1', 'Pending');

INSERT INTO balances (balance_id, game_id, username, amount)
VALUES (3001, 2001, 'testuser1', 99655.51),
       (3002, 2001, 'testuser2', 97330.10),
       
       (3003, 2002, 'testuser2', 96497.70),
       (3004, 2002, 'testuser1', 100690.10),
       
       (3005, 2003, 'testuser1', 100041.60),
       (3006, 2003, 'testuser3', 100342.10),
       
       (3007, 2004, 'testuser1', 23603.65),
       (3008, 2004, 'testuser2', 5240.05),
       
       (3009, 2005, 'testuser2', 28772.60),
       (0010, 2005, 'testuser1', 26955.05),
       
       (3011, 2006, 'testuser3', 8884.60),
       (3012, 2006, 'testuser1', 100000.00);
       
INSERT INTO trades (trade_id, game_id, username, type_id, stock_ticker, stock_name, amount, purchase_date, price_per_share, shares)
VALUES (4001, 2001, 'testuser1', 1, 'AAPL', 'Apple Inc',                                73180.00, '2021-08-01', 146.36,  500),
       (4002, 2001, 'testuser1', 1, 'GOOG', 'Alphabet Inc - Class C',                   2709.69,  '2021-08-01', 2709.69, 1),
       (4003, 2001, 'testuser2', 1, 'GME',  'Gamestop Corporation - Class A',           81000.00, '2021-08-01', 162.00,  500),
       (4004, 2001, 'testuser1', 2, 'AAPL', 'Apple Inc',                                72905.00, '2021-08-02', 145.81,  500),
       (4005, 2001, 'testuser1', 2, 'GOOG', 'Alphabet Inc - Class C',                   2720.00,  '2021-08-02', 2720.00, 1),
       (4006, 2001, 'testuser2', 2, 'GME',  'Gamestop Corporation - Class A',           78370.00, '2021-08-02', 156.74,  500),
       
       (4007, 2002, 'testuser2', 1, 'GME',  'Gamestop Corporation - Class A',           39185.00, '2021-08-02', 156.74,  250),
       (4008, 2002, 'testuser2', 1, 'AMC',  'AMC Entertainment Holdings Inc - Class A', 37580.00, '2021-08-02', 37.58,   1000),
       (4009, 2002, 'testuser1', 1, 'AAPL', 'Apple Inc',                                72905.00, '2021-08-02', 145.81,  500),
       (4010, 2002, 'testuser2', 2, 'GME',  'Gamestop Corporation - Class A',           38182.50, '2021-08-03', 152.73,  250),
       (4011, 2002, 'testuser2', 2, 'AMC',  'AMC Entertainment Holdings Inc - Class A', 35160.00, '2021-08-03', 35.16,   1000),
       (4012, 2002, 'testuser1', 2, 'AAPL', 'Apple Inc',                                73635.00, '2021-08-03', 147.27,  500),
       
       (4013, 2003, 'testuser1', 1, 'AAPL', 'Apple Inc',                                73100.00, '2021-08-08', 146.20,  500),
       (4014, 2003, 'testuser1', 1, 'AMZN', 'Amazon.com Inc.',                          3343.61,  '2021-08-08', 3343.61, 1),
       (4015, 2003, 'testuser3', 1, 'TSLA', 'Tesla Inc',                                71017.00, '2021-08-08', 710.17,  100),
       (4016, 2003, 'testuser1', 2, 'AAPL', 'Apple Inc',                                73220.00, '2021-08-10', 146.44,  500),
       (4017, 2003, 'testuser1', 2, 'AMZN', 'Amazon.com Inc.',                          3345.01,  '2021-08-10', 3345.01, 1),
       (4018, 2003, 'testuser3', 2, 'TSLA', 'Tesla Inc',                                71399.00, '2021-08-10', 713.99,  100),
       
       (4019, 2004, 'testuser1', 1, 'AAPL', 'Apple',                                    73025.00, '2021-08-12', 146.05,  500),
       (4020, 2004, 'testuser1', 1, 'AMZN', 'Amazon.com Inc.',                          3331.45,  '2021-08-12', 3331.45, 1),
       (4021, 2004, 'testuser2', 1, 'AMC',  'AMC Entertainment Holdings Inc - Class A', 94740.00, '2021-08-12', 31.58,   3000),
       
       (4022, 2005, 'testuser2', 1, 'GME',  'Gamestop Corporation - Class A',           39607.50, '2021-08-12', 158.43,  250),
       (4023, 2005, 'testuser2', 1, 'AMC',  'AMC Entertainment Holdings Inc - Class A', 31580.00, '2021-08-12', 31.58,   1000),
       (4024, 2005, 'testuser1', 1, 'AAPL', 'Apple Inc',                                73025.00, '2021-08-12', 146.05,  500),
       
       (4025, 2006, 'testuser3', 1, 'TSLA', 'Tesla Inc',                                35635.50, '2021-08-12', 712.71,  50),
       (4026, 2006, 'testuser3', 1, 'F',    'Ford Motor Co.',                           55440.00, '2021-08-12', 13.86,   4000);
       
INSERT INTO stocks (stock_id, game_id, username, stock_ticker, stock_name, shares)
VALUES (5001, 2001, 'testuser1', 'AAPL', 'Apple Inc',                                0),
       (5002, 2001, 'testuser1', 'GOOG', 'Alphabet Inc - Class C',                   0),
       (5003, 2001, 'testuser2', 'GME',  'Gamestop Corporation - Class A',           0),
       
       (5004, 2002, 'testuser2', 'GME',  'Gamestop Corporation - Class A',           0),
       (5005, 2002, 'testuser2', 'AMC',  'AMC Entertainment Holdings Inc - Class A', 0),
       (5006, 2002, 'testuser1', 'AAPL', 'Apple Inc',                                0),
       
       (5007, 2003, 'testuser1', 'AAPL', 'Apple Inc',                                0),
       (5008, 2003, 'testuser1', 'AMZN', 'Amazon.com Inc.',                          0),
       (5009, 2003, 'testuser3', 'TSLA', 'Tesla Inc',                                0),
       
       (5010, 2004, 'testuser1', 'AAPL', 'Apple',                                    500),
       (5011, 2004, 'testuser1', 'AMZN', 'Amazon.com Inc.',                          1),
       (5012, 2004, 'testuser2', 'AMC',  'AMC Entertainment Holdings Inc - Class A', 3000),
       
       (5013, 2005, 'testuser2', 'GME',  'Gamestop Corporation - Class A',           250),
       (5014, 2005, 'testuser2', 'AMC',  'AMC Entertainment Holdings Inc - Class A', 1000),
       (5015, 2005, 'testuser1', 'AAPL', 'Apple Inc',                                500),
       
       (5016, 2006, 'testuser3', 'TSLA', 'Tesla Inc',                                50),
       (5017, 2006, 'testuser3', 'F',    'Ford Motor Co.',                           4000);