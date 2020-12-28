CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    email VARCHAR(320) UNIQUE NOT NULL,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    password VARCHAR(64) NOT NULL,
    encrypted_secret_key VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS cards (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES users ON DELETE CASCADE,
    tag VARCHAR(256) DEFAULT '' NOT NULL,
    cite VARCHAR(128) NOT NULL,
    cite_information VARCHAR(2048) DEFAULT '',
    body_html TEXT NOT NULL,
    body_draft TEXT NOT NULL
);


