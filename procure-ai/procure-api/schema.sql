-- Procurement Application Database Schema

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS auction_bids CASCADE;
DROP TABLE IF EXISTS rfq_responses CASCADE;
DROP TABLE IF EXISTS rfx_line_items CASCADE;
DROP TABLE IF EXISTS rfx_events CASCADE;
DROP TABLE IF EXISTS vendors CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;

-- User roles enum
CREATE TYPE user_role AS ENUM ('CREATOR', 'APPROVER', 'VENDOR');

-- Event types enum
CREATE TYPE event_type AS ENUM ('RFQ', 'AUCTION');

-- Event status enum
CREATE TYPE event_status AS ENUM ('DRAFT', 'PUBLISHED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'AWARDED');

-- Bid status enum
CREATE TYPE bid_status AS ENUM ('SUBMITTED', 'WITHDRAWN', 'AWARDED', 'REJECTED');

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User roles table
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role user_role NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role)
);

-- Vendors table
CREATE TABLE vendors (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(100) NOT NULL,
    registration_number VARCHAR(50),
    tax_id VARCHAR(50),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(50),
    contact_person VARCHAR(100),
    is_approved BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- RFX Events table (for both RFQ and Auctions)
CREATE TABLE rfx_events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    event_type event_type NOT NULL,
    status event_status DEFAULT 'DRAFT',
    created_by BIGINT NOT NULL REFERENCES users(id),
    approved_by BIGINT REFERENCES users(id),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    currency VARCHAR(3) DEFAULT 'USD',
    terms_and_conditions TEXT,
    minimum_bid_increment DECIMAL(12,2) DEFAULT 0.01,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- RFX Line Items table
CREATE TABLE rfx_line_items (
    id BIGSERIAL PRIMARY KEY,
    rfx_event_id BIGINT NOT NULL REFERENCES rfx_events(id) ON DELETE CASCADE,
    item_number VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit VARCHAR(20),
    estimated_price DECIMAL(12,2),
    specifications TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- RFQ Responses table
CREATE TABLE rfq_responses (
    id BIGSERIAL PRIMARY KEY,
    rfx_event_id BIGINT NOT NULL REFERENCES rfx_events(id) ON DELETE CASCADE,
    vendor_id BIGINT NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    line_item_id BIGINT NOT NULL REFERENCES rfx_line_items(id) ON DELETE CASCADE,
    unit_price DECIMAL(12,2) NOT NULL,
    total_price DECIMAL(12,2) NOT NULL,
    delivery_time_days INTEGER,
    comments TEXT,
    status bid_status DEFAULT 'SUBMITTED',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(rfx_event_id, vendor_id, line_item_id)
);

-- Auction Bids table
CREATE TABLE auction_bids (
    id BIGSERIAL PRIMARY KEY,
    rfx_event_id BIGINT NOT NULL REFERENCES rfx_events(id) ON DELETE CASCADE,
    vendor_id BIGINT NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    line_item_id BIGINT NOT NULL REFERENCES rfx_line_items(id) ON DELETE CASCADE,
    bid_amount DECIMAL(12,2) NOT NULL,
    bid_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    comments TEXT
);

-- Indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_vendors_user_id ON vendors(user_id);
CREATE INDEX idx_rfx_events_created_by ON rfx_events(created_by);
CREATE INDEX idx_rfx_events_status ON rfx_events(status);
CREATE INDEX idx_rfx_events_type ON rfx_events(event_type);
CREATE INDEX idx_rfx_line_items_event_id ON rfx_line_items(rfx_event_id);
CREATE INDEX idx_rfq_responses_event_vendor ON rfq_responses(rfx_event_id, vendor_id);
CREATE INDEX idx_auction_bids_event_vendor ON auction_bids(rfx_event_id, vendor_id);
CREATE INDEX idx_auction_bids_time ON auction_bids(bid_time);

-- Sample Data

-- Insert sample users
INSERT INTO users (username, email, password, first_name, last_name, phone) VALUES
('admin', 'admin@procure.com', '$2a$10$8GfvAf1qIXqfVjKhkFJEhuXDgs1UGZ7Vwgbp7FTMkYqYDpJD5CDPW', 'Admin', 'User', '+1234567890'),
('approver1', 'approver1@procure.com', '$2a$10$8GfvAf1qIXqfVjKhkFJEhuXDgs1UGZ7Vwgbp7FTMkYqYDpJD5CDPW', 'John', 'Approver', '+1234567891'),
('vendor1', 'vendor1@supplier.com', '$2a$10$8GfvAf1qIXqfVjKhkFJEhuXDgs1UGZ7Vwgbp7FTMkYqYDpJD5CDPW', 'Alice', 'Johnson', '+1234567892'),
('vendor2', 'vendor2@supplier.com', '$2a$10$8GfvAf1qIXqfVjKhkFJEhuXDgs1UGZ7Vwgbp7FTMkYqYDpJD5CDPW', 'Bob', 'Smith', '+1234567893'),
('vendor3', 'vendor3@supplier.com', '$2a$10$8GfvAf1qIXqfVjKhkFJEhuXDgs1UGZ7Vwgbp7FTMkYqYDpJD5CDPW', 'Carol', 'Davis', '+1234567894');

-- Insert user roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'CREATOR'),
(2, 'APPROVER'),
(3, 'VENDOR'),
(4, 'VENDOR'),
(5, 'VENDOR');

-- Insert sample vendors
INSERT INTO vendors (user_id, company_name, registration_number, tax_id, address, city, state, postal_code, country, contact_person, is_approved) VALUES
(3, 'Alpha Tech Solutions', 'REG001', 'TAX001', '123 Tech Street', 'San Francisco', 'CA', '94105', 'USA', 'Alice Johnson', true),
(4, 'Beta Manufacturing', 'REG002', 'TAX002', '456 Industry Ave', 'Detroit', 'MI', '48201', 'USA', 'Bob Smith', true),
(5, 'Gamma Supplies Inc', 'REG003', 'TAX003', '789 Supply Road', 'Chicago', 'IL', '60601', 'USA', 'Carol Davis', true);

-- Insert sample RFQ event
INSERT INTO rfx_events (title, description, event_type, status, created_by, start_date, end_date, currency, terms_and_conditions) VALUES
('Office Equipment RFQ', 'Request for quotation for office equipment including computers, printers, and furniture', 'RFQ', 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 'USD', 'Payment terms: Net 30 days. Delivery required within 45 days of order.'),
('IT Services Auction', 'English auction for IT maintenance and support services', 'AUCTION', 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7 days', 'USD', 'Service period: 12 months. Payment terms: Monthly billing.');

-- Insert line items for RFQ
INSERT INTO rfx_line_items (rfx_event_id, item_number, description, quantity, unit, estimated_price, specifications) VALUES
(1, 'ITEM001', 'Desktop Computers', 50, 'units', 800.00, 'Intel i5, 8GB RAM, 256GB SSD, Windows 11'),
(1, 'ITEM002', 'Laser Printers', 10, 'units', 300.00, 'Black & White, Network enabled, 30 ppm'),
(1, 'ITEM003', 'Office Chairs', 50, 'units', 200.00, 'Ergonomic, adjustable height, 5-year warranty');

-- Insert line items for Auction
INSERT INTO rfx_line_items (rfx_event_id, item_number, description, quantity, unit, estimated_price, specifications) VALUES
(2, 'SRV001', 'IT Support Services', 1, 'annual contract', 50000.00, '24/7 support, on-site and remote, SLA 99.9%');

-- Insert sample RFQ responses
INSERT INTO rfq_responses (rfx_event_id, vendor_id, line_item_id, unit_price, total_price, delivery_time_days, comments) VALUES
(1, 1, 1, 750.00, 37500.00, 30, 'Bulk discount applied. Free delivery included.'),
(1, 2, 1, 780.00, 39000.00, 25, 'Premium warranty included.'),
(1, 1, 2, 280.00, 2800.00, 20, 'Energy efficient models.'),
(1, 2, 2, 290.00, 2900.00, 22, 'Extended warranty available.'),
(1, 3, 3, 180.00, 9000.00, 15, 'Ergonomic design, multiple color options.');

-- Insert sample auction bids
INSERT INTO auction_bids (rfx_event_id, vendor_id, line_item_id, bid_amount, bid_time, comments) VALUES
(2, 1, 4, 48000.00, CURRENT_TIMESTAMP - INTERVAL '2 hours', 'Competitive rate with 24/7 support'),
(2, 2, 4, 47000.00, CURRENT_TIMESTAMP - INTERVAL '1 hour', 'Best value proposition'),
(2, 3, 4, 46500.00, CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'Premium service at competitive price');

-- Update timestamps trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_vendors_updated_at BEFORE UPDATE ON vendors 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_rfx_events_updated_at BEFORE UPDATE ON rfx_events 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
