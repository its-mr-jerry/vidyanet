-- Assign the newly added permissions (TIMETABLE, EXAMINATIONS, HOSTEL, SPORTS) to the SCHOOL_ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
CROSS JOIN permissions p
WHERE r.role_code = 'SCHOOL_ADMIN'
AND p.module_name IN ('TIMETABLE', 'EXAMINATIONS', 'HOSTEL', 'SPORTS')
ON CONFLICT DO NOTHING;
