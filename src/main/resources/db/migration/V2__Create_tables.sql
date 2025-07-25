-- Tables creation script
-- This will be applied to both dev and prod schemas via Hibernate

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Messages table
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    sender VARCHAR(255) NOT NULL,
    content TEXT,
    room_id VARCHAR(255) NOT NULL,
    message_type VARCHAR(255) NOT NULL DEFAULT 'message',
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_messages_room_id ON messages(room_id);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Comments
COMMENT ON TABLE users IS 'User accounts for the chat application';
COMMENT ON TABLE messages IS 'Chat messages stored by room';
COMMENT ON COLUMN messages.message_type IS 'Type of message: message, file, system';
COMMENT ON COLUMN messages.room_id IS 'Room identifier for chat organization';