ALTER TABLE roles ADD COLUMN school_id BIGINT REFERENCES schools(school_id);

-- Remove the global unique constraint on role_code
ALTER TABLE roles DROP CONSTRAINT roles_role_code_key;

-- Add a unique constraint per school (allowing multiple NULL school_id for system roles if needed,
-- but usually system roles are unique by code anyway)
-- In Postgres, (code, NULL) is unique but multiple (code, NULL) are allowed by default UNIQUE.
-- We want system roles (school_id NULL) to be unique by code,
-- and school roles to be unique by (code, school_id).

CREATE UNIQUE INDEX idx_roles_code_school_id ON roles (role_code, school_id) WHERE school_id IS NOT NULL;
CREATE UNIQUE INDEX idx_roles_code_system ON roles (role_code) WHERE school_id IS NULL;
