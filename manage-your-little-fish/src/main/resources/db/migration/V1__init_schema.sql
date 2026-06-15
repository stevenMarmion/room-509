-- ============================================================
--  V1 - Initial schema
-- ============================================================

CREATE TYPE role_enum        AS ENUM ('USER', 'ADMIN');
CREATE TYPE theme_enum       AS ENUM ('LIGHT', 'DARK');
CREATE TYPE friend_status    AS ENUM ('PENDING', 'ACCEPTED', 'BLOCKED');
CREATE TYPE trade_status     AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED');

-- ------------------------------------------------------------
--  users
-- ------------------------------------------------------------
CREATE TABLE users (
    id          BIGSERIAL    PRIMARY KEY,
    pseudo      VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    avatar      VARCHAR(255),
    theme       theme_enum   NOT NULL DEFAULT 'LIGHT',
    coins       INT          NOT NULL DEFAULT 0,
    role        role_enum    NOT NULL DEFAULT 'USER',
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
    aquarium_id   BIGINT       REFERENCES aquarium(id) ON DELETE SET NULL,
    name          VARCHAR(100) NOT NULL,
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
    status        friend_status NOT NULL DEFAULT 'PENDING',
    since         TIMESTAMP  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_friendship UNIQUE (requester_id, addressee_id)
);

-- ------------------------------------------------------------
--  trade  (N-1 with users)
-- ------------------------------------------------------------
CREATE TABLE trade (
    id           BIGSERIAL    PRIMARY KEY,
    initiator_id BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status       trade_status NOT NULL DEFAULT 'PENDING',
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
    user_id   BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name      VARCHAR(100) NOT NULL,
    reward    INT          NOT NULL DEFAULT 0,
    date      DATE         NOT NULL DEFAULT CURRENT_DATE,
    completed BOOLEAN      NOT NULL DEFAULT FALSE
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
