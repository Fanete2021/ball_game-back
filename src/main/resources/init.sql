CREATE DATABASE game;

CREATE TABLE IF NOT EXISTS sessions (
    sessionId TEXT PRIMARY KEY,
    nickname TEXT NOT NULL,
    startedAt BIGINT NOT NULL,
    finishedAt BIGINT,
    score INTEGER DEFAULT 0,
    clientVersion TEXT
);

CREATE TABLE IF NOT EXISTS scores (
    id SERIAL PRIMARY KEY,
    nickname TEXT NOT NULL,
    score INTEGER NOT NULL,
    "when" BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sessions_sessionId ON sessions(sessionId);
CREATE INDEX IF NOT EXISTS idx_scores_score ON scores(score DESC);
CREATE INDEX IF NOT EXISTS idx_scores_nickname ON scores(nickname);