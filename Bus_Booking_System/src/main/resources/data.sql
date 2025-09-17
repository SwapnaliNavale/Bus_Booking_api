-- Insert Normal User
INSERT INTO users ( created_on, updated_on, age, dob, email, first_name, is_active, last_name, mobile_no, password, role)
VALUES 
( '2025-09-17 11:09:39.837094', '2025-09-17 11:09:39.837094', 25, '1999-05-05', 'user@gmail.com', 'Swapnali', b'1', 'Navale', '9876543211', 
'$2a$10$boKUXHJbdqNyNwTWJEHGmuGO5V/m2N4bjhLuEI7AIaKvGz13YFER.', 'ROLE_USER');

-- Insert Admin User
INSERT INTO users ( created_on, updated_on, age, dob, email, first_name, is_active, last_name, mobile_no, password, role)
VALUES 
( '2025-09-17 11:40:45.761123', '2025-09-17 11:40:45.761123', 25, '1999-05-05', 'admin1@gmail.com', 'Swapnali', b'1', 'Navale', '9876543212', 
'$2a$10$uvcd.UqEAc.oyl4b7Vrb4.ZpSy8kohEC3ibTvjCvHlA.usJTmMRvO', 'ROLE_ADMIN');
