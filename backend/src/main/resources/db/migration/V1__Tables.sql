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
    tag VARCHAR(384) DEFAULT '' NOT NULL, -- limited length is actually 256, but it has to be longer to account for Base64 and AES
    cite VARCHAR(216) NOT NULL, -- limited length: 128
    cite_information VARCHAR(2776) DEFAULT '', -- limited length: 2048
    body_html TEXT NOT NULL,
    body_draft TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS arguments (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES users ON DELETE CASCADE,
    name VARCHAR(216) NOT NULL -- limited length: 128
);

CREATE TABLE IF NOT EXISTS argument_cards (
    argument_id UUID NOT NULL REFERENCES arguments ON DELETE CASCADE,
    card_id UUID NOT NULL REFERENCES users ON DELETE CASCADE,
    index_in_argument SMALLINT DEFAULT 0 NOT NULL -- 0-based signed short (limit of 32767)
);
