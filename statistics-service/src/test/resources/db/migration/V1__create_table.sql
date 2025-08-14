CREATE TABLE history (
    user_id UUID,
    track_id UUID,
    timestamp DateTime64(3, 'UTC')
)
    ENGINE = MergeTree()
ORDER BY (timestamp);
