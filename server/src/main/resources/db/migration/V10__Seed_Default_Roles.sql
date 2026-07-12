INSERT INTO roles (role_code, role_name, description, is_system_role) VALUES
('SCHOOL_ADMIN', 'School Admin', 'Full access to school data and settings', TRUE),
('PRINCIPAL', 'Principal', 'Academic and administrative head of the school', TRUE),
('TEACHER', 'Teacher', 'Access to classroom management and student data', TRUE),
('STUDENT', 'Student', 'Access to learning materials and own records', TRUE),
('PARENT', 'Parent', 'Access to their children''s records', TRUE),
('ACCOUNTANT', 'Accountant', 'Access to financial records and fee management', TRUE),
('LIBRARIAN', 'Librarian', 'Manage library books and records', TRUE),
('TRANSPORT_MANAGER', 'Transport Manager', 'Manage vehicles and routes', TRUE),
('INVENTORY_MANAGER', 'Inventory Manager', 'Manage assets and stocks', TRUE),
('RECEPTIONIST', 'Receptionist', 'Front desk management', TRUE),
('CLERK', 'Clerk', 'General administrative tasks', TRUE),
('ADMISSION_OFFICER', 'Admission Officer', 'Manage student admissions and enquiries', TRUE),
('HR_MANAGER', 'HR Manager', 'Manage staff, payroll, and recruitment', TRUE),
('EXAM_CONTROLLER', 'Exam Controller', 'Manage examinations and results', TRUE),
('ACADEMIC_COORDINATOR', 'Academic Coordinator', 'Manage curriculum and timetable', TRUE),
('FINANCE_OFFICER', 'Finance Officer', 'Strategic financial planning and oversight', TRUE)
ON CONFLICT (role_code) DO UPDATE SET
    role_name = EXCLUDED.role_name,
    description = EXCLUDED.description,
    is_system_role = EXCLUDED.is_system_role;
