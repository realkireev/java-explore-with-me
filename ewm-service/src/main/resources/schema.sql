CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(254) NOT NULL UNIQUE,
    name VARCHAR(250) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_name ON users(name);

CREATE TABLE IF NOT EXISTS category (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
CREATE INDEX IF NOT EXISTS idx_category_name ON category USING hash (name);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    category_id BIGINT NOT NULL,
    event_date TIMESTAMP NOT NULL,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_on TIMESTAMP,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER NOT NULL DEFAULT 0,
    request_moderation BOOLEAN NOT NULL DEFAULT TRUE,
    initiator_id BIGINT NOT NULL,
    state VARCHAR(20) NOT NULL,
    review_comment VARCHAR(200),

    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT fk_initiator FOREIGN KEY (initiator_id) REFERENCES users (id)
);
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_events_title ON events(title);
CREATE INDEX IF NOT EXISTS idx_events_annotation ON events(annotation);
CREATE INDEX IF NOT EXISTS idx_events_description_gin ON events USING GIN(description gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_events_event_date ON events(event_date);
CREATE INDEX IF NOT EXISTS idx_events_initiator_id ON events(initiator_id);
CREATE INDEX IF NOT EXISTS idx_events_state ON events USING hash (state);

CREATE TABLE IF NOT EXISTS request (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
	requester_id BIGINT REFERENCES users (id),
	event_id BIGINT REFERENCES events (id),
	status VARCHAR(20) NOT NULL,
	created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (requester_id, event_id)
);
CREATE INDEX IF NOT EXISTS idx_request_status ON request USING hash (status);

CREATE TABLE IF NOT EXISTS compilation (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	title VARCHAR(50) NOT NULL,
	pinned BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_event (
	compilation_id BIGINT NOT NULL,
	event_id BIGINT NOT NULL,
	PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS events_statistics (
    state VARCHAR(20) PRIMARY KEY,
    count BIGINT NOT NULL
)
