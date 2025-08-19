-- Migration to add media_url and media_type columns to messages table

ALTER TABLE messages
ADD COLUMN media_url VARCHAR(255) DEFAULT NULL,
ADD COLUMN media_type VARCHAR(50) DEFAULT NULL;
