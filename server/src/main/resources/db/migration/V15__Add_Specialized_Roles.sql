INSERT INTO roles (role_code, role_name, description, is_system_role) VALUES
('HOSTEL_WARDEN', 'Hostel Warden', 'Manage hostel facilities and student residents', TRUE),
('SPORTS_COACH', 'Sports Coach', 'Manage sports activities and physical education', TRUE),
('MEDICAL_OFFICER', 'Medical Officer', 'Manage school health clinic and student health records', TRUE),
('COUNSELOR', 'School Counselor', 'Manage student guidance and mental health support', TRUE),
('SECURITY_HEAD', 'Security Head', 'Manage school campus security and surveillance', TRUE)
ON CONFLICT (role_code) DO UPDATE SET
    role_name = EXCLUDED.role_name,
    description = EXCLUDED.description,
    is_system_role = EXCLUDED.is_system_role;
