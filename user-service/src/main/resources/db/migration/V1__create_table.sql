CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name TEXT,
    last_name TEXT,
    username TEXT NOT NULL UNIQUE,
    bio TEXT,
    country VARCHAR(3),
    email TEXT NOT NULL UNIQUE,
    profile_picture TEXT
);