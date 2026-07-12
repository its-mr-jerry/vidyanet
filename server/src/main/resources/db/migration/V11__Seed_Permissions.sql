INSERT INTO permissions (module_name, action, description)
SELECT m.name, a.action, 'Can ' || a.action || ' ' || m.name
FROM (
    VALUES
    ('DASHBOARD'), ('ADMISSIONS'), ('STUDENTS'), ('PARENTS'), ('STAFF'),
    ('ACADEMICS'), ('ATTENDANCE'), ('FINANCE'), ('LIBRARY'), ('TRANSPORT'),
    ('INVENTORY'), ('COMMUNICATION'), ('REPORTS'), ('SETTINGS')
) AS m(name)
CROSS JOIN (
    VALUES
    ('VIEW'), ('CREATE'), ('EDIT'), ('DELETE'), ('EXPORT')
) AS a(action)
ON CONFLICT (module_name, action) DO NOTHING;
