CREATE TABLE event_similarities (
    id SERIAL PRIMARY KEY,
    event_a INT NOT NULL,
    event_b INT NOT NULL,
    score REAL NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
