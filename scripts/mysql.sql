CREATE
DATABASE IF NOT EXISTS test;
USE
test;

CREATE TABLE IF NOT EXISTS Sessions
(
    SessionId
    CHAR
(
    36
) PRIMARY KEY,
    Email VARCHAR
(
    255
) NOT NULL,
    Status VARCHAR
(
    50
) NOT NULL
    );

