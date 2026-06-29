-- ============================================================
--  V2 - Seed data (test / demo)
--
--  Passwords (BCrypt, cost 10) :
--    admin    → "admin123"
--    alice    → "password123"
--    bob      → "password123"
--    charlie  → "password123"
--    diana    → "password123"
--    eve      → "password123"
-- ============================================================

-- ============================================================
--  USERS
-- ============================================================
INSERT INTO users (id, pseudo, email, password, avatar, theme, coins, role, created_at) VALUES
    (1, 'admin',   'admin@littlefish.dev',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', '', 'LIGHT', 1000, 'ADMIN',  NOW() - INTERVAL '30 days'),
    (2, 'alice',   'alice@littlefish.dev',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', '', 'LIGHT',  350, 'USER',   NOW() - INTERVAL '25 days'),
    (3, 'bob',     'bob@littlefish.dev',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', '', 'DARK',    80, 'USER',   NOW() - INTERVAL '20 days'),
    (4, 'charlie', 'charlie@littlefish.dev', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', '', 'LIGHT',  500, 'USER',   NOW() - INTERVAL '15 days'),
    (5, 'diana',   'diana@littlefish.dev',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', '', 'DARK',   200, 'USER',   NOW() - INTERVAL '10 days'),
    (6, 'eve',     'eve@littlefish.dev',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', '', 'LIGHT',   10, 'USER',   NOW() - INTERVAL '2 days');

-- ============================================================
--  NOTIFICATION PREFERENCES
-- ============================================================
INSERT INTO notification_preference (id, user_id, notify_on_death, notify_before_death, daily_reminder) VALUES
    (1, 1, FALSE, FALSE, FALSE),
    (2, 2, TRUE,  TRUE,  TRUE),
    (3, 3, TRUE,  FALSE, FALSE),
    (4, 4, TRUE,  TRUE,  FALSE),
    (5, 5, FALSE, TRUE,  TRUE),
    (6, 6, TRUE,  TRUE,  TRUE);

-- ============================================================
--  AQUARIUMS
-- ============================================================
INSERT INTO aquarium (id, name, user_id, is_public, level, capacity) VALUES
    (1, 'Admin Tank',        1, TRUE,  3, 20),
    (2, 'Alice''s Reef',     2, TRUE,  2, 10),
    (3, 'Bob''s Bowl',       3, FALSE, 1,  5),
    (4, 'Charlie''s Ocean',  4, TRUE,  2, 10),
    (5, 'Diana''s Lagoon',   5, FALSE, 1,  5),
    (6, 'Eve''s First Tank', 6, FALSE, 1,  5);

-- ============================================================
--  FOOD (catalogue boutique)
-- ============================================================
INSERT INTO food (id, name, price, nutrition_value) VALUES
    (1, 'Basic Pellets',     10,  15),
    (2, 'Premium Flakes',    25,  30),
    (3, 'Live Brine Shrimp', 50,  55),
    (4, 'Frozen Bloodworms', 40,  45),
    (5, 'Algae Wafers',      15,  20);

-- ============================================================
--  AQUARIUM UPGRADES (catalogue boutique)
-- ============================================================
INSERT INTO aquarium_upgrade (id, name, price, capacity_bonus, level_bonus) VALUES
    (1, 'Small Extension',   100, 5, 0),
    (2, 'Level Up Kit',      200, 0, 1),
    (3, 'Deluxe Bundle',     400, 5, 1),
    (4, 'Coral Decor Pack',   80, 2, 0),
    (5, 'Filtration System', 150, 0, 1);

-- ============================================================
--  FISH
--  Tous les poissons ont un aquarium_id valide (NOT NULL).
--  Les anciens "templates boutique" sont remplacés par des
--  poissons réels dans les aquariums des utilisateurs.
-- ============================================================
INSERT INTO fish (id, aquarium_id, name, species, color, size, age, life_points, price, last_fed_at) VALUES
    -- Aquarium admin (id=1)
    (1,  1, 'Zeus',     'Clownfish',   'Orange',         2, 5,  95,  50, NOW() - INTERVAL '10 minutes'),
    (2,  1, 'Athena',   'Blue Tang',   'Blue',           3, 3,  88,  80, NOW() - INTERVAL '30 minutes'),
    (3,  1, 'Apollo',   'Goldfish',    'Gold',           1, 7, 100,  20, NOW()),
    (4,  1, 'Ares',     'Betta',       'Red',            2, 2,  72,  60, NOW() - INTERVAL '1 hour'),
    (5,  1, 'Hermes',   'Koi',         'Orange & Black', 4, 1, 100, 120, NOW() - INTERVAL '15 minutes'),

    -- Aquarium alice (id=2)
    (6,  2, 'Bubbles',  'Goldfish',    'Gold',           1, 1, 100,  20, NOW()),
    (7,  2, 'Splash',   'Zebra Danio', 'Black & White',  1, 2,  85,  15, NOW() - INTERVAL '20 minutes'),
    (8,  2, 'Coral',    'Clownfish',   'Orange & White', 2, 4,  60,  50, NOW() - INTERVAL '2 hours'),

    -- Aquarium bob (id=3)
    (9,  3, 'Finley',   'Koi',         'Orange & Black', 4, 0, 100, 120, NOW()),
    (10, 3, 'Speedy',   'Blue Tang',   'Blue & Yellow',  3, 1,  45,  80, NOW() - INTERVAL '3 hours'),

    -- Aquarium charlie (id=4)
    (11, 4, 'Poseidon', 'Hammerhead',  'Grey',           5, 6,  90, 200, NOW() - INTERVAL '5 minutes'),
    (12, 4, 'Triton',   'Clownfish',   'Orange',         2, 3,  78,  50, NOW() - INTERVAL '45 minutes'),
    (13, 4, 'Nereid',   'Betta',       'Blue & Red',     2, 1, 100,  60, NOW()),

    -- Aquarium diana (id=5)
    (14, 5, 'Luna',     'Goldfish',    'White',          1, 0, 100,  20, NOW()),
    (15, 5, 'Nova',     'Betta',       'Purple',         2, 0, 100,  60, NOW() - INTERVAL '5 minutes'),

    -- Aquarium eve (id=6)
    (16, 6, 'Rookie',   'Zebra Danio', 'Black & White',  1, 0,   5,  15, NOW() - INTERVAL '12 hours');

-- ============================================================
--  FRIENDSHIPS
-- ============================================================
INSERT INTO friendship (id, requester_id, addressee_id, status, since) VALUES
    (1, 2, 3, 'ACCEPTED', NOW() - INTERVAL '20 days'),
    (2, 3, 2, 'ACCEPTED', NOW() - INTERVAL '20 days'),
    (3, 2, 4, 'ACCEPTED', NOW() - INTERVAL '10 days'),
    (4, 4, 2, 'ACCEPTED', NOW() - INTERVAL '10 days'),
    (5, 4, 5, 'PENDING',  NOW() - INTERVAL '1 day'),
    (6, 3, 6, 'BLOCKED',  NOW() - INTERVAL '5 days');

-- ============================================================
--  DAILY CHALLENGES (catalogue)
-- ============================================================
INSERT INTO daily_challenge (id, name, reward, description) VALUES
    (1, 'Morning Feed',     20, 'Feed all the fish in your aquarium'),
    (2, 'Visit a Friend',   15, 'Visit a friend''s public aquarium'),
    (3, 'Coin Collector',   25, 'Click on your fish 10 times to collect coins'),
    (4, 'Aquarium Upgrade', 30, 'Purchase an upgrade for your aquarium'),
    (5, 'Fish Whisperer',   10, 'Name a fish and check on it'),
    (6, 'Social Butterfly', 20, 'Accept a friend request'),
    (7, 'Trader Joe',       35, 'Complete a fish trade with a friend'),
    (8, 'Full Health',      15, 'Keep all your fish above 80 life points');

-- ============================================================
--  DAILY CHALLENGE USERS
-- ============================================================
INSERT INTO daily_challenge_user (daily_challenge_id, user_id, completed, date) VALUES
    (1, 2, TRUE,  CURRENT_DATE),
    (2, 2, FALSE, CURRENT_DATE),
    (3, 2, FALSE, CURRENT_DATE),
    (1, 3, TRUE,  CURRENT_DATE),
    (4, 3, TRUE,  CURRENT_DATE),
    (5, 3, TRUE,  CURRENT_DATE),
    (2, 4, FALSE, CURRENT_DATE),
    (6, 4, TRUE,  CURRENT_DATE),
    (7, 4, FALSE, CURRENT_DATE),
    (1, 5, FALSE, CURRENT_DATE),
    (3, 5, FALSE, CURRENT_DATE),
    (8, 6, FALSE, CURRENT_DATE);

-- ============================================================
--  TRADES
-- ============================================================
INSERT INTO trade (id, price, initiator_id, receiver_id, status, created_at) VALUES
    (1, 80, 2, 3, 'PENDING',  NOW() - INTERVAL '2 hours'),
    (2, 60, 4, 2, 'ACCEPTED', NOW() - INTERVAL '5 days'),
    (3, 70, 3, 4, 'REJECTED', NOW() - INTERVAL '3 days');

INSERT INTO trade_fish (trade_id, fish_id) VALUES
    (1, 8),   -- trade 1 : Coral (alice → bob)
    (2, 13),  -- trade 2 : Nereid (charlie → alice)
    (3, 10);  -- trade 3 : Speedy (bob → charlie)

-- ============================================================
--  RESYNCHRONISATION DES SÉQUENCES
-- ============================================================
SELECT setval('users_id_seq',                    (SELECT MAX(id) FROM users));
SELECT setval('notification_preference_id_seq',  (SELECT MAX(id) FROM notification_preference));
SELECT setval('aquarium_id_seq',                 (SELECT MAX(id) FROM aquarium));
SELECT setval('fish_id_seq',                     (SELECT MAX(id) FROM fish));
SELECT setval('food_id_seq',                     (SELECT MAX(id) FROM food));
SELECT setval('aquarium_upgrade_id_seq',         (SELECT MAX(id) FROM aquarium_upgrade));
SELECT setval('friendship_id_seq',               (SELECT MAX(id) FROM friendship));
SELECT setval('trade_id_seq',                    (SELECT MAX(id) FROM trade));
SELECT setval('daily_challenge_id_seq',          (SELECT MAX(id) FROM daily_challenge));