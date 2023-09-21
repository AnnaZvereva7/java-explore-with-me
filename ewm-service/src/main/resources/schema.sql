CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(50) NOT NULL,
email VARCHAR(100) NOT NULL UNIQUE,
CONSTRAINT check_email CHECK (email ~'^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
);

CREATE TABLE IF NOT EXISTS categories (
id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(25) NOT NULL UNIQUE
);

--CREATE TABLE IF NOT EXISTS events (
--id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
--category INT REFERENCES categories (id) ON DELETE RESTRICT NOT NULL,
--title VARCHAR(100) NOT NULL,
--annotation VARCHAR(100) NOT NULL,
--description VARCHAR(256) NOT NULL,
--event_date TIMESTAMP NOT NULL,
--initiator BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
--paid BOOLEAN NOT NULL,
--participant_limit INT,
--
--created_on TIMESTAMP NOT NULL,
--published_on TIMESTAMP,
--request_moderation BOOLEAN NOT NULL,
--state VARCHAR(20) NOT NULL,
--)
