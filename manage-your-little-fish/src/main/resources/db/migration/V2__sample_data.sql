-- ============================================================
--  V2 - Sample data
-- ============================================================

-- Users (passwords are bcrypt hashes of "password123")
INSERT INTO users (pseudo, email, password, theme, coins, role) VALUES
    ('alice',   'alice@example.com',   '$2a$10$7QJ8Z1e2Kp3mN5oP6qR8SuXvYwZaAbBcCdDeEfFgGhHiIjJkKlLm', 'LIGHT', 100, 'USER'),
    ('bob',     'bob@example.com',     '$2a$10$7QJ8Z1e2Kp3mN5oP6qR8SuXvYwZaAbBcCdDeEfFgGhHiIjJkKlLm', 'DARK',   50, 'USER'),
    ('admin',   'admin@example.com',   '$2a$10$7QJ8Z1e2Kp3mN5oP6qR8SuXvYwZaAbBcCdDeEfFgGhHiIjJkKlLm', 'LIGHT',  500, 'ADMIN');

-- Notification preferences
INSERT INTO notification_preference (user_id, notify_on_death, notify_before_death, daily_reminder) VALUES
    (1, TRUE,  TRUE,  TRUE),
    (2, TRUE,  FALSE, FALSE),
    (3, FALSE, FALSE, FALSE);

-- Aquariums
INSERT INTO aquarium (user_id, is_public, level, capacity) VALUES
    (1, TRUE,  2, 10),
    (2, FALSE, 1,  5),
    (3, TRUE,  3, 15);

-- Fish
INSERT INTO fish (aquarium_id, name, species, color, size, age, life_points, last_fed_at) VALUES
    (1, 'Nemo',    'Clownfish',  'Orange',  2, 1, 100, NOW()),
    (1, 'Dory',    'Tang',       'Blue',    3, 2,  80, NOW() - INTERVAL '1 hour'),
    (2, 'Goldie',  'Goldfish',   'Gold',    1, 0, 100, NOW()),
    (3, 'Sharky',  'Shark',      'Grey',    5, 3,  60, NOW() - INTERVAL '2 hours');

-- Friendships
INSERT INTO friendship (requester_id, addressee_id, status, since) VALUES
    (1, 2, 'ACCEPTED', NOW()),
    (1, 3, 'PENDING',  NOW());

-- Trades
INSERT INTO trade (initiator_id, status) VALUES
    (1, 'PENDING');

INSERT INTO trade_fish (trade_id, fish_id) VALUES
    (1, 2);

-- Daily challenges
INSERT INTO daily_challenge (user_id, name, reward, date, completed) VALUES
    (1, 'Feed all fish',      20, CURRENT_DATE, FALSE),
    (1, 'Visit a friend aquarium', 10, CURRENT_DATE, FALSE),
    (2, 'Feed all fish',      20, CURRENT_DATE, TRUE);

-- Shop items — Food
INSERT INTO food (name, price, nutrition_value) VALUES
    ('Basic Pellets',   10, 10),
    ('Premium Flakes',  25, 25),
    ('Live Brine',      50, 50);

-- Shop items — Aquarium upgrades
INSERT INTO aquarium_upgrade (name, price, capacity_bonus, level_bonus) VALUES
    ('Small Extension',  100, 5, 0),
    ('Level Up Kit',     200, 0, 1),
    ('Deluxe Bundle',    400, 5, 1);
