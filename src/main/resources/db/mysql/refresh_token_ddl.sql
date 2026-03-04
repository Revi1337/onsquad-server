DROP TABLE IF EXISTS refresh_token;
CREATE TABLE IF NOT EXISTS refresh_token
(
    member_id   BIGINT PRIMARY KEY,
    token_value VARCHAR(512) NOT NULL,
    expired_at  BIGINT       NOT NULL
);
