DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'special_playlist_type') THEN
            CREATE TYPE special_playlist_type AS ENUM ('HISTORY', 'FAVORITE');
        END IF;
    END
$$;

CREATE TABLE track_data (
    id UUID PRIMARY KEY,
    title TEXT,
    likes_count BIGINT NOT NULL CHECK (likes_count >= 0),
    plays_count BIGINT NOT NULL CHECK (plays_count >= 0),
    duration INT NOT NULL CHECK (duration > 0),
    is_valid BOOLEAN DEFAULT FALSE
);

CREATE TABLE artists (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL CHECK (name <> ''),
    user_id UUID DEFAULT NULL
);

CREATE TABLE albums (
    id UUID PRIMARY KEY,
    title TEXT NOT NULL CHECK (title <> ''),
    artist_id UUID REFERENCES artists(id),
    duration INT NOT NULL CHECK (duration > 0),
    length INT NOT NULL CHECK (length > 0),
    release_date DATE
);

CREATE TABLE track_views (
    id UUID PRIMARY KEY,
    title TEXT NOT NULL CHECK (title <> ''),
    track_data_id UUID REFERENCES track_data(id) ON DELETE CASCADE,
    album_id UUID REFERENCES albums(id),
    album_index INT
);

CREATE TABLE track_artists (
    track_id UUID REFERENCES track_views(id) ON DELETE CASCADE,
    artist_id UUID REFERENCES artists(id)
);

CREATE TABLE playlists (
    id UUID PRIMARY KEY,
    user_id UUID,
    title TEXT,
    length INT,
    duration  INT,
    is_public BOOLEAN DEFAULT TRUE,
    is_special BOOLEAN DEFAULT FALSE,
    special_type special_playlist_type DEFAULT NULL
);

CREATE TABLE playlist_tracks (
    playlist_id UUID REFERENCES playlists(id) ON DELETE CASCADE,
    track_id UUID REFERENCES track_views(id) ON DELETE CASCADE,
    playlist_index INT
)
