DROP TABLE IF EXISTS verification_code;
CREATE TABLE IF NOT EXISTS verification_code
(
    email      VARCHAR(255) NOT NULL,
    code       VARCHAR(50)  NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    expired_at BIGINT       NOT NULL,

    PRIMARY KEY (email)
);
