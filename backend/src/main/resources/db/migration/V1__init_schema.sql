-- ============================================================
--  V1 - Initial schema
-- ============================================================

-- ------------------------------------------------------------
--  users
-- ------------------------------------------------------------
CREATE TABLE users (
    id          BIGSERIAL    PRIMARY KEY,
    pseudo      VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    avatar      VARCHAR(255),
    theme       VARCHAR(10)  NOT NULL DEFAULT 'LIGHT' CHECK (theme IN ('LIGHT', 'DARK')),
    coins       INT          NOT NULL DEFAULT 0,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
--  notification_preference  (1-1 with users)
-- ------------------------------------------------------------
CREATE TABLE notification_preference (
    id                   BIGSERIAL PRIMARY KEY,
    user_id              BIGINT    NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    notify_on_death      BOOLEAN   NOT NULL DEFAULT TRUE,
    notify_before_death  BOOLEAN   NOT NULL DEFAULT TRUE,
    daily_reminder       BOOLEAN   NOT NULL DEFAULT FALSE
);

-- ------------------------------------------------------------
--  aquarium  (1-1 with users)
-- ------------------------------------------------------------
CREATE TABLE aquarium (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    user_id    BIGINT    NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    is_public  BOOLEAN   NOT NULL DEFAULT FALSE,
    level      INT       NOT NULL DEFAULT 1,
    capacity   INT       NOT NULL DEFAULT 5
);

-- ------------------------------------------------------------
--  fish  (N-1 with aquarium)
-- ------------------------------------------------------------
CREATE TABLE fish (
    id            BIGSERIAL    PRIMARY KEY,
    aquarium_id   BIGINT       NOT NULL REFERENCES aquarium(id) ON DELETE CASCADE,
    name          VARCHAR(100) NOT NULL,
    price         INT          NOT NULL DEFAULT 10,
    species       VARCHAR(100),
    color         VARCHAR(50),
    size          INT          NOT NULL DEFAULT 1,
    age           INT          NOT NULL DEFAULT 0,
    life_points   INT          NOT NULL DEFAULT 100,
    last_fed_at   TIMESTAMP
);

-- ------------------------------------------------------------
--  friendship  (N-N self-ref on users)
-- ------------------------------------------------------------
CREATE TABLE friendship (
    id            BIGSERIAL  PRIMARY KEY,
    requester_id  BIGINT     NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    addressee_id  BIGINT     NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status        VARCHAR(10) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'BLOCKED')),
    since         TIMESTAMP  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_friendship UNIQUE (requester_id, addressee_id)
);

-- ------------------------------------------------------------
--  trade  (N-1 with users)
-- ------------------------------------------------------------
CREATE TABLE trade (
    id           BIGSERIAL    PRIMARY KEY,
    price        INT          NOT NULL DEFAULT 0,
    initiator_id BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id  BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status       VARCHAR(10)  NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
--  trade_fish  (join table Trade <-> Fish)
-- ------------------------------------------------------------
CREATE TABLE trade_fish (
    trade_id BIGINT NOT NULL REFERENCES trade(id) ON DELETE CASCADE,
    fish_id  BIGINT NOT NULL REFERENCES fish(id)  ON DELETE CASCADE,
    PRIMARY KEY (trade_id, fish_id)
);

-- ------------------------------------------------------------
--  daily_challenge  (N-1 with users)
-- ------------------------------------------------------------
CREATE TABLE daily_challenge (
    id        BIGSERIAL    PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    reward    INT          NOT NULL DEFAULT 0,
    description TEXT       NOT NULL
);

-- ------------------------------------------------------------
--  daily_challenge_user  (join table DailyChallenge <-> User)
-- ------------------------------------------------------------
CREATE TABLE daily_challenge_user (
    daily_challenge_id BIGINT NOT NULL REFERENCES daily_challenge(id) ON DELETE CASCADE,
    user_id            BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    completed          BOOLEAN NOT NULL DEFAULT FALSE,
    date               DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (daily_challenge_id, user_id)
);

-- ------------------------------------------------------------
--  food  (ShopItem subtype — @MappedSuperclass)
-- ------------------------------------------------------------
CREATE TABLE food (
    id               BIGSERIAL    PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    price            INT          NOT NULL DEFAULT 0,
    nutrition_value  INT          NOT NULL DEFAULT 10
);

-- ------------------------------------------------------------
--  aquarium_upgrade  (ShopItem subtype — @MappedSuperclass)
-- ------------------------------------------------------------
CREATE TABLE aquarium_upgrade (
    id               BIGSERIAL    PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    price            INT          NOT NULL DEFAULT 0,
    capacity_bonus   INT          NOT NULL DEFAULT 0,
    level_bonus      INT          NOT NULL DEFAULT 0
);


-- ============================================================
--  V2 - Sample data
-- ============================================================

INSERT INTO users (pseudo, email, password, theme, coins, role) VALUES
    ('alice', 'alice@example.com', '$2a$10$7QJ8Z1e2Kp3mN5oP6qR8SuXvYwZaAbBcCdDeEfFgGhHiIjJkKlLm', 'LIGHT', 200, 'USER'),
    ('bob',   'bob@example.com',   '$2a$10$7QJ8Z1e2Kp3mN5oP6qR8SuXvYwZaAbBcCdDeEfFgGhHiIjJkKlLm', 'DARK',  50, 'USER'),
    ('admin', 'admin@example.com', '$2a$10$7QJ8Z1e2Kp3mN5oP6qR8SuXvYwZaAbBcCdDeEfFgGhHiIjJkKlLm', 'LIGHT', 500, 'ADMIN');

INSERT INTO notification_preference (user_id, notify_on_death, notify_before_death, daily_reminder) VALUES
    (1, TRUE,  TRUE,  TRUE),
    (2, TRUE,  FALSE, FALSE),
    (3, FALSE, FALSE, FALSE);

INSERT INTO aquarium (user_id, is_public, level, capacity, name) VALUES
    (1, TRUE,  2, 10, 'Alice''s Aquarium'),
    (2, FALSE, 1,  5, 'Bob''s Aquarium'),
    (3, TRUE,  3, 15, 'Admin''s Aquarium');

INSERT INTO fish (aquarium_id, name, species, color, size, age, life_points, last_fed_at) VALUES
    (1, 'Nemo',   'Clownfish', 'Orange', 2, 1, 100, NOW()),
    (1, 'Dory',   'Tang',      'Blue',   3, 2,  80, NOW() - INTERVAL '1 hour'),
    (2, 'Goldie', 'Goldfish',  'Gold',   1, 0, 100, NOW()),
    (3, 'Sharky', 'Shark',     'Grey',   5, 3,  60, NOW() - INTERVAL '2 hours');

INSERT INTO friendship (requester_id, addressee_id, status, since) VALUES
    (1, 2, 'ACCEPTED', NOW()),
    (1, 3, 'PENDING',  NOW());

INSERT INTO trade (initiator_id, receiver_id, status, price) VALUES
    (1, 2, 'PENDING',  100),
    (2, 1, 'ACCEPTED',  50);

INSERT INTO trade_fish (trade_id, fish_id) VALUES
    (1, 2);

INSERT INTO daily_challenge (id, name, reward, description) VALUES
    (1, 'Feed all fish',           20, 'Feed all the fish in your aquarium'),
    (2, 'Visit a friend aquarium', 10, 'Visit a friend''s aquarium'),
    (3, 'Feed all fish',           20, 'Feed all the fish in your aquarium'),
    (4, 'Level up your aquarium',  30, 'Upgrade your aquarium to the next level');

INSERT INTO daily_challenge_user (daily_challenge_id, user_id, completed, date) VALUES
    (1, 1, TRUE,  CURRENT_DATE),
    (2, 1, FALSE, CURRENT_DATE),
    (3, 2, TRUE,  CURRENT_DATE),
    (4, 2, FALSE, CURRENT_DATE);

INSERT INTO food (name, price, nutrition_value) VALUES
    ('Basic Pellets',  10, 10),
    ('Premium Flakes', 25, 25),
    ('Live Brine',     50, 50);

INSERT INTO aquarium_upgrade (name, price, capacity_bonus, level_bonus) VALUES
    ('Small Extension', 100, 5, 0),
    ('Level Up Kit',    200, 0, 1),
    ('Deluxe Bundle',   400, 5, 1);