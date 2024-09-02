CREATE TABLE track_data (
    id UUID PRIMARY KEY,
    title TEXT,
    artist UUID[],
    creation_time TIMESTAMPTZ,
    cover TEXT
);

CREATE TABLE track_views (
    id UUID PRIMARY KEY,
    title TEXT NOT NULL,
    cover TEXT,
    track_data_id UUID REFERENCES track_data(id)
);

CREATE TABLE albums (
    id UUID PRIMARY KEY,
    title TEXT NOT NULL,
    cover TEXT
);

CREATE TABLE artists (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    profile_picture TEXT,
    gallery TEXT[],
    user_id UUID
);

