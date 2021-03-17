CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    email VARCHAR(320) UNIQUE NOT NULL,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    password VARCHAR(64) NOT NULL,
    encrypted_secret_key VARCHAR(128) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS cards (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES users ON DELETE CASCADE,
    tag VARCHAR(384) DEFAULT '' NOT NULL, -- limited length is actually 256, but it has to be longer to account for Base64 and AES
    cite VARCHAR(216) NOT NULL, -- limited length: 128
    cite_information VARCHAR(2776) DEFAULT '', -- limited length: 2048
    body_html TEXT NOT NULL,
    body_draft TEXT NOT NULL DEFAULT '',
    body_text TEXT NOT NULL DEFAULT '', -- just the raw text of the card -- no styling
    time_created_at BIGINT NOT NULL,
    last_modified BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS arguments (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES users ON DELETE CASCADE,
    name VARCHAR(216) NOT NULL -- limited length: 128
);

CREATE TABLE IF NOT EXISTS argument_cards (
    argument_id UUID NOT NULL REFERENCES arguments ON DELETE CASCADE,
    card_id UUID NOT NULL REFERENCES cards ON DELETE CASCADE,
    index_in_argument SMALLINT DEFAULT 0 NOT NULL -- 0-based signed short (limit of 32767)
);

-- table for groups of debaters with shared evidence
-- users can only be in one team
CREATE TABLE IF NOT EXISTS teams (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(216) NOT NULL, -- limited length: 128
    secret_key_hash VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS team_members (
    team_id UUID NOT NULL REFERENCES teams ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users ON DELETE CASCADE, -- todo: UNIQUE?
    team_secret_key VARCHAR(128) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'MEMBER' -- 'MEMBER' or 'OWNER'
);

-- table for keeping track of action history (create card, modify card, etc)
CREATE TABLE IF NOT EXISTS actions (
    id UUID NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    subject_id UUID NOT NULL REFERENCES users ON DELETE CASCADE, -- user who took an action
    action_type VARCHAR(32) NOT NULL, -- type of action (e.g. "CREATE_CARD")
    time BIGINT NOT NULL,
    user_id UUID REFERENCES users ON DELETE CASCADE, -- id of user if another user is involved. cascading so that users who have deleted their accounts aren't shown (would be kinda invasive)
    card_id UUID REFERENCES cards ON DELETE NO ACTION, -- if this action involves a card, the id of the card (NO ACTION is because deleting a card is an event)
    argument_id UUID REFERENCES arguments ON DELETE NO ACTION -- if an argument is involved, the id of the argument
);
