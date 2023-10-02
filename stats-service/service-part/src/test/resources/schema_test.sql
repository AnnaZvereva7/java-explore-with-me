DROP TABLE IF EXISTS stat_data;

CREATE TABLE if NOT EXISTS stat_data (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app VARCHAR(20) NOT NULL,
    uri VARCHAR(50) NOT NULL,
    ip VARCHAR(39) NOT NULL,
    request_time TIMESTAMP NOT NULL
);