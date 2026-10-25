-- SQLite Local Chat Database Schema with Phone-Centric Identity & Media Support
-- WeResence Master Schema

-- 1. LOCAL USER PROFILE (Phone Number is the Primary Identity)
CREATE TABLE IF NOT EXISTS user_profile (
    phone_number TEXT PRIMARY KEY,           -- e.g. "+919876543210" (Unique Identity Anchor)
    resence_id TEXT UNIQUE NOT NULL,         -- e.g. "@piyush" (Public Unique Tag)
    display_name TEXT NOT NULL,              -- e.g. "Piyush Singh"
    bio TEXT DEFAULT 'Available on WeResence',-- User Bio / Status
    avatar_uri TEXT,                         -- Local or cloud avatar image path
    linked_google_email TEXT,                -- Optional linked Google Account for fast login/backup
    created_at INTEGER NOT NULL
);

-- 2. CONTACTS BOOK (Phone Number Matching)
CREATE TABLE IF NOT EXISTS contacts (
    phone_number TEXT PRIMARY KEY,           -- Contact's phone number
    contact_name TEXT NOT NULL,              -- Name as saved in user's phonebook
    resence_id TEXT,                         -- Their @handle on WeResence (if registered)
    bio TEXT,                                -- Their bio
    avatar_url TEXT,                         -- Their profile picture
    is_on_weresence INTEGER DEFAULT 0,       -- 1 = WeResence user, 0 = Offline SMS only
    last_sync_timestamp INTEGER
);

-- 3. CONVERSATIONS
CREATE TABLE IF NOT EXISTS conversations (
    thread_id TEXT PRIMARY KEY,
    recipient_phone TEXT NOT NULL UNIQUE,    -- Chat mapped strictly to recipient's phone number
    recipient_name TEXT,
    last_message TEXT,
    last_message_type TEXT DEFAULT 'TEXT',   -- 'TEXT', 'PHOTO', 'VIDEO', 'AUDIO'
    last_message_timestamp INTEGER,
    unread_count INTEGER DEFAULT 0,
    is_pinned INTEGER DEFAULT 0,
    is_vault_hidden INTEGER DEFAULT 0        -- 1 = Hidden behind Master Vault PIN
);

-- 4. MESSAGES
CREATE TABLE IF NOT EXISTS messages (
    message_id TEXT PRIMARY KEY,
    thread_id TEXT NOT NULL,
    sender_phone TEXT NOT NULL,
    recipient_phone TEXT NOT NULL,
    is_outgoing INTEGER NOT NULL,            -- 1 = sent, 0 = received
    message_type TEXT NOT NULL DEFAULT 'TEXT',-- 'TEXT', 'PHOTO', 'VIDEO', 'AUDIO'
    body_plain TEXT,                         -- Text message content or media caption
    local_media_uri TEXT,                    -- Local sandboxed media path
    remote_cloud_url TEXT,                   -- Cloud storage URL
    thumbnail_blob BLOB,                     -- Micro-thumbnail binary
    file_size_bytes INTEGER DEFAULT 0,
    transport_medium TEXT NOT NULL,          -- 'INTERNET' or 'OFFLINE_SMS'
    delivery_status TEXT NOT NULL,           -- 'SENDING', 'SENT', 'DELIVERED', 'FAILED'
    timestamp INTEGER NOT NULL,
    FOREIGN KEY(thread_id) REFERENCES conversations(thread_id) ON DELETE CASCADE
);

-- 5. APP SETTINGS & SECURITY
CREATE TABLE IF NOT EXISTS user_settings (
    setting_key TEXT PRIMARY KEY,
    setting_value TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_messages_thread ON messages(thread_id);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp);
CREATE INDEX IF NOT EXISTS idx_contacts_phone ON contacts(phone_number);
