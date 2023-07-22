DROP TABLE IF EXISTS stats CASCADE;

CREATE TABLE IF NOT EXISTS stats (
  stat_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  app VARCHAR(100) NOT NULL,
  uri VARCHAR(100) NOT NULL,
  ip VARCHAR(50) NOT NULL,
  timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_stats PRIMARY KEY (stat_id)
);
