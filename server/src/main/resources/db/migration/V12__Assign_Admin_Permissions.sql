INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
CROSS JOIN permissions p
WHERE r.role_code = 'SCHOOL_ADMIN'
ON CONFLICT DO NOTHING;
