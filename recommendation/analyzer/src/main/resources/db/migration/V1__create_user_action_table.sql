CREATE TABLE user_actions (
      id SERIAL PRIMARY KEY,
      user_id INT NOT NULL,
      event_id INT NOT NULL,
      action_type VARCHAR(50) NOT NULL CHECK (action_type IN ('VIEW', 'REGISTER', 'LIKE')),
      timestamp TIMESTAMP NOT NULL
);