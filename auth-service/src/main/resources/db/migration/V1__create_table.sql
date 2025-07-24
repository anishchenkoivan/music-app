DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'role') THEN
            CREATE TYPE role AS ENUM ('USER', 'ADMIN');
        END IF;
    END
$$;

CREATE TABLE users (
    id UUID PRIMARY KEY,
    password TEXT NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role role NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);