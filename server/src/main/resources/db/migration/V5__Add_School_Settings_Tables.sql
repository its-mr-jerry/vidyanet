CREATE TABLE IF NOT EXISTS school_settings (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT UNIQUE NOT NULL REFERENCES schools(school_id) ON DELETE CASCADE,
    registration_number VARCHAR(50),
    motto VARCHAR(255),
    establishment_date VARCHAR(20),
    affiliation_board VARCHAR(100),
    primary_brand_color VARCHAR(10) DEFAULT '#4F46E5',
    is_maintenance_mode BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS school_working_hours (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES schools(school_id) ON DELETE CASCADE,
    day_of_week VARCHAR(15) NOT NULL,
    opening_time VARCHAR(10),
    closing_time VARCHAR(10),
    is_closed BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS school_branches (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES schools(school_id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    contact_person VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(150),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);
