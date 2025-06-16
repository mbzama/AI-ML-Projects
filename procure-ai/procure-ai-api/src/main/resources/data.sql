-- Sample Data for Procurement Application

-- Insert sample users (password is 'password' encrypted with BCrypt)
INSERT INTO users (username, email, password, first_name, last_name, phone, is_active, created_at, updated_at) VALUES
('admin', 'admin@procure.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', '+1234567890', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('approver1', 'approver1@procure.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'John', 'Approver', '+1234567891', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('vendor1', 'vendor1@supplier.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Alice', 'Johnson', '+1234567892', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('vendor2', 'vendor2@supplier.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Bob', 'Smith', '+1234567893', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('vendor3', 'vendor3@supplier.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Carol', 'Davis', '+1234567894', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert user roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'CREATOR'),
(2, 'APPROVER'),
(3, 'VENDOR'),
(4, 'VENDOR'),
(5, 'VENDOR');

-- Insert sample vendors
INSERT INTO vendors (user_id, company_name, registration_number, tax_id, address, city, state, postal_code, country, contact_person, is_approved, created_at, updated_at) VALUES
(3, 'Alpha Tech Solutions', 'REG001', 'TAX001', '123 Tech Street', 'San Francisco', 'CA', '94105', 'USA', 'Alice Johnson', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Beta Manufacturing', 'REG002', 'TAX002', '456 Industry Ave', 'Detroit', 'MI', '48201', 'USA', 'Bob Smith', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Gamma Supplies Inc', 'REG003', 'TAX003', '789 Supply Road', 'Chicago', 'IL', '60601', 'USA', 'Carol Davis', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
