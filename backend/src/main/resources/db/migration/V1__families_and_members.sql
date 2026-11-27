CREATE TABLE families (
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE members (
    id            UUID PRIMARY KEY,
    family_id     UUID         NOT NULL REFERENCES families (id) ON DELETE CASCADE,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('PARENT', 'CHILD')),
    color         VARCHAR(7)   NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_members_email ON members (lower(email));
CREATE INDEX idx_members_family ON members (family_id);
