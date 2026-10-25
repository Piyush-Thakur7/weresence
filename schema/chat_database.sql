-- SQLite Local Chat Database Schema with Media Support
-- Location: C:\Users\HP\.gemini\antigravity\scratch\stealth-chat\schema\chat_database.sql

CREATE TABLE IF NOT EXISTS conversations (
    thread_id TEXT PRIMARY KEY,
    recipient_phone TEXT NOT NULL UNIQUE,
    recipient_name TEXT,
    last_message TEXT,
    last_message_type TEXT DEFAULT 'TEXT', -- 'TEXT', 'PHOTO', 'VIDEO', 'AUDIO'
    last_message_timestamp INTEGER,
    unread_count INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS messages (
    message_id TEXT PRIMARY KEY,
    thread_id TEXT NOT NULL,
    sender_phone TEXT NOT NULL,
    recipient_phone TEXT NOT NULL,
    is_outgoing INTEGER NOT NULL, -- 1 = sent, 0 = received
    message_type TEXT NOT NULL DEFAULT 'TEXT', -- 'TEXT', 'PHOTO', 'VIDEO', 'AUDIO'
    body_plain TEXT, -- Text message content or media caption
    local_media_uri TEXT, -- Local path on phone storage
    remote_cloud_url TEXT, -- Cloud storage URL (S3 / GCS)
    thumbnail_blob BLOB, -- Micro-thumbnail binary
    file_size_bytes INTEGER DEFAULT 0,
    transport_medium TEXT NOT NULL, -- 'INTERNET' or 'STEALTH_DATA_SMS'
    delivery_status TEXT NOT NULL,  -- 'SENDING', 'SENT', 'DELIVERED', 'FAILED'
    timestamp INTEGER NOT NULL,
    FOREIGN KEY(thread_id) REFERENCES conversations(thread_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_messages_thread ON messages(thread_id);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp);
