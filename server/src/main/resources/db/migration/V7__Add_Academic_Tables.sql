CREATE TABLE IF NOT EXISTS academic_settings (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT UNIQUE NOT NULL REFERENCES schools(school_id) ON DELETE CASCADE,
    grading_scale VARCHAR(20) DEFAULT 'LETTER',
    pass_marks INTEGER DEFAULT 35,
    gpa_decimals INTEGER DEFAULT 2,
    is_weighted_gpa BOOLEAN DEFAULT FALSE,
    attendance_mode VARCHAR(20) DEFAULT 'DAILY',
    late_threshold_minutes INTEGER DEFAULT 15,
    min_promotion_percentage INTEGER DEFAULT 40,
    min_promotion_attendance INTEGER DEFAULT 75,
    require_no_dues BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS academic_sessions (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES schools(school_id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    start_date VARCHAR(20) NOT NULL,
    end_date VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'UPCOMING'
);

CREATE TABLE IF NOT EXISTS holidays (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES schools(school_id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    date VARCHAR(20) NOT NULL,
    type VARCHAR(20) DEFAULT 'HOLIDAY'
);
