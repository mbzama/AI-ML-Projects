#!/usr/bin/env python3
"""
Procurement Database Test Data Generator

This script generates test data for a procurement application database
based on user inputs for number of admins, vendors, auctions, and RFQs.
"""

import csv
import random
from datetime import datetime, timedelta
from faker import Faker
import bcrypt
import os

fake = Faker()

class ProcurementDataGenerator:
    def __init__(self):
        self.users = []
        self.user_roles = []
        self.vendors = []
        self.rfx_events = []
        self.rfx_line_items = []
        self.rfq_responses = []
        self.auction_bids = []
        
        # Sample data for realistic generation
        self.item_categories = [
            "Office Equipment", "IT Hardware", "Software Licenses", "Furniture",
            "Maintenance Services", "Consulting Services", "Raw Materials",
            "Manufacturing Equipment", "Security Services", "Cleaning Services"
        ]
        
        self.office_items = [
            ("Desktop Computers", "units", "Intel i5, 8GB RAM, 256GB SSD"),
            ("Laptops", "units", "Business grade, 16GB RAM, 512GB SSD"),
            ("Laser Printers", "units", "Network enabled, duplex printing"),
            ("Office Chairs", "units", "Ergonomic, adjustable height"),
            ("Standing Desks", "units", "Electric height adjustable"),
            ("Monitors", "units", "24-inch LED, Full HD"),
            ("Keyboards", "units", "Wireless, ergonomic design"),
            ("Mice", "units", "Optical, wireless"),
        ]
        
        self.services = [
            ("IT Support Services", "annual contract", "24/7 support, on-site and remote"),
            ("Security Services", "monthly contract", "Physical security, surveillance"),
            ("Cleaning Services", "monthly contract", "Office cleaning, sanitization"),
            ("Consulting Services", "project", "Business process optimization"),
            ("Software Development", "project", "Custom application development"),
            ("Network Maintenance", "annual contract", "Network infrastructure support"),
        ]
        
        self.company_names = [
            "Alpha Tech Solutions", "Beta Manufacturing", "Gamma Supplies Inc",
            "Delta Services Corp", "Epsilon Technologies", "Zeta Industries",
            "Eta Consulting Group", "Theta Enterprises", "Iota Systems",
            "Kappa Global Solutions", "Lambda Innovations", "Mu Technologies"
        ]

    def generate_password_hash(self, password="password"):
        """Generate bcrypt hash for password"""
        return bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

    def generate_users(self, num_admins, num_vendors):
        """Generate users data"""
        user_id = 1
        
        # Generate admin users
        for i in range(num_admins):
            user = {
                'id': user_id,
                'username': f'admin{i+1}' if i > 0 else 'admin',
                'email': f'admin{i+1}@procure.com' if i > 0 else 'admin@procure.com',
                'password': self.generate_password_hash(),
                'first_name': fake.first_name(),
                'last_name': fake.last_name(),
                'phone': fake.phone_number(),
                'is_active': True,
                'created_at': fake.date_time_between(start_date='-1y', end_date='now'),
                'updated_at': fake.date_time_between(start_date='-1y', end_date='now')
            }
            self.users.append(user)
            
            # Add CREATOR and APPROVER roles for admins
            self.user_roles.append({
                'id': len(self.user_roles) + 1,
                'user_id': user_id,
                'role': 'CREATOR',
                'created_at': user['created_at']
            })
            
            if i < num_admins // 2 + 1:  # Half of admins are also approvers
                self.user_roles.append({
                    'id': len(self.user_roles) + 1,
                    'user_id': user_id,
                    'role': 'APPROVER',
                    'created_at': user['created_at']
                })
            
            user_id += 1
        
        # Generate vendor users
        for i in range(num_vendors):
            user = {
                'id': user_id,
                'username': f'vendor{i+1}',
                'email': f'vendor{i+1}@supplier{i%5+1}.com',
                'password': self.generate_password_hash(),
                'first_name': fake.first_name(),
                'last_name': fake.last_name(),
                'phone': fake.phone_number(),
                'is_active': True,
                'created_at': fake.date_time_between(start_date='-1y', end_date='now'),
                'updated_at': fake.date_time_between(start_date='-1y', end_date='now')
            }
            self.users.append(user)
            
            # Add VENDOR role
            self.user_roles.append({
                'id': len(self.user_roles) + 1,
                'user_id': user_id,
                'role': 'VENDOR',
                'created_at': user['created_at']
            })
            
            user_id += 1

    def generate_vendors(self, num_vendors):
        """Generate vendors data"""
        vendor_users = [u for u in self.users if any(r['role'] == 'VENDOR' for r in self.user_roles if r['user_id'] == u['id'])]
        
        for i, user in enumerate(vendor_users[:num_vendors]):
            vendor = {
                'id': i + 1,
                'user_id': user['id'],
                'company_name': random.choice(self.company_names) + f" {i+1}" if i >= len(self.company_names) else self.company_names[i],
                'registration_number': f'REG{i+1:03d}',
                'tax_id': f'TAX{i+1:03d}',
                'address': fake.street_address(),
                'city': fake.city(),
                'state': fake.state_abbr(),
                'postal_code': fake.postcode(),
                'country': 'USA',
                'contact_person': f"{user['first_name']} {user['last_name']}",
                'is_approved': random.choice([True, True, True, False]),  # 75% approved
                'created_at': user['created_at'],
                'updated_at': user['updated_at']
            }
            self.vendors.append(vendor)

    def generate_rfx_events(self, num_rfqs, num_auctions):
        """Generate RFX events (RFQs and Auctions)"""
        admin_users = [u for u in self.users if any(r['role'] == 'CREATOR' for r in self.user_roles if r['user_id'] == u['id'])]
        approver_users = [u for u in self.users if any(r['role'] == 'APPROVER' for r in self.user_roles if r['user_id'] == u['id'])]
        
        event_id = 1
        
        # Generate RFQs
        for i in range(num_rfqs):
            start_date = fake.date_time_between(start_date='-3m', end_date='+1m')
            end_date = start_date + timedelta(days=random.randint(7, 60))
            
            event = {
                'id': event_id,
                'title': f"{random.choice(self.item_categories)} RFQ {i+1}",
                'description': f"Request for quotation for {random.choice(self.item_categories).lower()} including various items and specifications",
                'event_type': 'RFQ',
                'status': random.choice(['DRAFT', 'PUBLISHED', 'IN_PROGRESS', 'COMPLETED']),
                'created_by': random.choice(admin_users)['id'],
                'approved_by': random.choice(approver_users)['id'] if random.random() > 0.3 else None,
                'start_date': start_date,
                'end_date': end_date,
                'currency': 'USD',
                'terms_and_conditions': 'Payment terms: Net 30 days. Delivery required within 45 days of order.',
                'minimum_bid_increment': 0.01,
                'created_at': fake.date_time_between(start_date='-4m', end_date='-1m'),
                'updated_at': fake.date_time_between(start_date='-4m', end_date='now')
            }
            self.rfx_events.append(event)
            event_id += 1
        
        # Generate Auctions
        for i in range(num_auctions):
            start_date = fake.date_time_between(start_date='-2m', end_date='+1m')
            end_date = start_date + timedelta(days=random.randint(1, 14))
            
            event = {
                'id': event_id,
                'title': f"{random.choice(self.item_categories)} Auction {i+1}",
                'description': f"English auction for {random.choice(self.item_categories).lower()} with competitive bidding",
                'event_type': 'AUCTION',
                'status': random.choice(['DRAFT', 'PUBLISHED', 'IN_PROGRESS', 'COMPLETED']),
                'created_by': random.choice(admin_users)['id'],
                'approved_by': random.choice(approver_users)['id'] if random.random() > 0.3 else None,
                'start_date': start_date,
                'end_date': end_date,
                'currency': 'USD',
                'terms_and_conditions': 'Service period: 12 months. Payment terms: Monthly billing.',
                'minimum_bid_increment': random.choice([0.01, 1.00, 10.00, 100.00]),
                'created_at': fake.date_time_between(start_date='-3m', end_date='-1m'),
                'updated_at': fake.date_time_between(start_date='-3m', end_date='now')
            }
            self.rfx_events.append(event)
            event_id += 1

    def generate_line_items(self):
        """Generate line items for RFX events"""
        line_item_id = 1
        
        for event in self.rfx_events:
            num_items = random.randint(1, 5)
            
            if event['event_type'] == 'RFQ':
                items_pool = self.office_items
            else:  # AUCTION
                items_pool = self.services
            
            selected_items = random.sample(items_pool, min(num_items, len(items_pool)))
            
            for j, (description, unit, specifications) in enumerate(selected_items):
                line_item = {
                    'id': line_item_id,
                    'rfx_event_id': event['id'],
                    'item_number': f'ITEM{line_item_id:03d}',
                    'description': description,
                    'quantity': random.randint(1, 100) if unit != 'project' else 1,
                    'unit': unit,
                    'estimated_price': round(random.uniform(100, 5000), 2),
                    'specifications': specifications,
                    'created_at': event['created_at']
                }
                self.rfx_line_items.append(line_item)
                line_item_id += 1

    def generate_rfq_responses(self):
        """Generate RFQ responses"""
        rfq_events = [e for e in self.rfx_events if e['event_type'] == 'RFQ']
        approved_vendors = [v for v in self.vendors if v['is_approved']]
        
        response_id = 1
        
        for event in rfq_events:
            event_line_items = [li for li in self.rfx_line_items if li['rfx_event_id'] == event['id']]
            participating_vendors = random.sample(approved_vendors, min(random.randint(2, 6), len(approved_vendors)))
            
            for vendor in participating_vendors:
                for line_item in event_line_items:
                    if random.random() > 0.2:  # 80% chance vendor responds to each item
                        unit_price = round(line_item['estimated_price'] * random.uniform(0.8, 1.2), 2)
                        total_price = round(unit_price * line_item['quantity'], 2)
                        
                        response = {
                            'id': response_id,
                            'rfx_event_id': event['id'],
                            'vendor_id': vendor['id'],
                            'line_item_id': line_item['id'],
                            'unit_price': unit_price,
                            'total_price': total_price,
                            'delivery_time_days': random.randint(7, 60),
                            'comments': fake.sentence(),
                            'status': random.choice(['SUBMITTED', 'SUBMITTED', 'SUBMITTED', 'AWARDED', 'REJECTED']),
                            'submitted_at': fake.date_time_between(start_date=event['start_date'], end_date=event['end_date'])
                        }
                        self.rfq_responses.append(response)
                        response_id += 1

    def generate_auction_bids(self):
        """Generate auction bids"""
        auction_events = [e for e in self.rfx_events if e['event_type'] == 'AUCTION']
        approved_vendors = [v for v in self.vendors if v['is_approved']]
        
        bid_id = 1
        
        for event in auction_events:
            event_line_items = [li for li in self.rfx_line_items if li['rfx_event_id'] == event['id']]
            participating_vendors = random.sample(approved_vendors, min(random.randint(3, 8), len(approved_vendors)))
            
            for line_item in event_line_items:
                # Generate multiple bids per item with decreasing prices (reverse auction style)
                current_price = line_item['estimated_price']
                bid_times = []
                
                # Generate random bid times within the event period
                for _ in range(random.randint(5, 20)):
                    bid_times.append(fake.date_time_between(start_date=event['start_date'], end_date=event['end_date']))
                bid_times.sort()
                
                for i, bid_time in enumerate(bid_times):
                    vendor = random.choice(participating_vendors)
                    # Each subsequent bid should be lower (with some randomness)
                    current_price *= random.uniform(0.95, 0.99)
                    
                    bid = {
                        'id': bid_id,
                        'rfx_event_id': event['id'],
                        'vendor_id': vendor['id'],
                        'line_item_id': line_item['id'],
                        'bid_amount': round(current_price, 2),
                        'bid_time': bid_time,
                        'is_active': True,
                        'comments': fake.sentence() if random.random() > 0.7 else None
                    }
                    self.auction_bids.append(bid)
                    bid_id += 1

    def save_to_csv(self, output_dir='csv_output'):
        """Save all data to CSV files"""
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)
        
        # Users CSV
        with open(f'{output_dir}/users.csv', 'w', newline='', encoding='utf-8') as file:
            if self.users:
                writer = csv.DictWriter(file, fieldnames=self.users[0].keys())
                writer.writeheader()
                writer.writerows(self.users)
        
        # User Roles CSV
        with open(f'{output_dir}/user_roles.csv', 'w', newline='', encoding='utf-8') as file:
            if self.user_roles:
                writer = csv.DictWriter(file, fieldnames=self.user_roles[0].keys())
                writer.writeheader()
                writer.writerows(self.user_roles)
        
        # Vendors CSV
        with open(f'{output_dir}/vendors.csv', 'w', newline='', encoding='utf-8') as file:
            if self.vendors:
                writer = csv.DictWriter(file, fieldnames=self.vendors[0].keys())
                writer.writeheader()
                writer.writerows(self.vendors)
        
        # RFX Events CSV
        with open(f'{output_dir}/rfx_events.csv', 'w', newline='', encoding='utf-8') as file:
            if self.rfx_events:
                writer = csv.DictWriter(file, fieldnames=self.rfx_events[0].keys())
                writer.writeheader()
                writer.writerows(self.rfx_events)
        
        # RFX Line Items CSV
        with open(f'{output_dir}/rfx_line_items.csv', 'w', newline='', encoding='utf-8') as file:
            if self.rfx_line_items:
                writer = csv.DictWriter(file, fieldnames=self.rfx_line_items[0].keys())
                writer.writeheader()
                writer.writerows(self.rfx_line_items)
        
        # RFQ Responses CSV
        with open(f'{output_dir}/rfq_responses.csv', 'w', newline='', encoding='utf-8') as file:
            if self.rfq_responses:
                writer = csv.DictWriter(file, fieldnames=self.rfq_responses[0].keys())
                writer.writeheader()
                writer.writerows(self.rfq_responses)
        
        # Auction Bids CSV
        with open(f'{output_dir}/auction_bids.csv', 'w', newline='', encoding='utf-8') as file:
            if self.auction_bids:
                writer = csv.DictWriter(file, fieldnames=self.auction_bids[0].keys())
                writer.writeheader()
                writer.writerows(self.auction_bids)

    def generate_all_data(self, num_admins, num_vendors, num_auctions, num_rfqs):
        """Generate all test data"""
        print("Generating test data...")
        print(f"Admins: {num_admins}, Vendors: {num_vendors}, Auctions: {num_auctions}, RFQs: {num_rfqs}")
        
        self.generate_users(num_admins, num_vendors)
        print(f"✓ Generated {len(self.users)} users")
        
        self.generate_vendors(num_vendors)
        print(f"✓ Generated {len(self.vendors)} vendors")
        
        self.generate_rfx_events(num_rfqs, num_auctions)
        print(f"✓ Generated {len(self.rfx_events)} RFX events")
        
        self.generate_line_items()
        print(f"✓ Generated {len(self.rfx_line_items)} line items")
        
        self.generate_rfq_responses()
        print(f"✓ Generated {len(self.rfq_responses)} RFQ responses")
        
        self.generate_auction_bids()
        print(f"✓ Generated {len(self.auction_bids)} auction bids")
        
        print("\nData generation completed!")
        return {
            'users': len(self.users),
            'user_roles': len(self.user_roles),
            'vendors': len(self.vendors),
            'rfx_events': len(self.rfx_events),
            'rfx_line_items': len(self.rfx_line_items),
            'rfq_responses': len(self.rfq_responses),
            'auction_bids': len(self.auction_bids)
        }


def main():
    """Main function to run the data generator"""
    print("=== Procurement Database Test Data Generator ===\n")
    
    try:
        # Get user input
        num_admins = int(input("Enter number of admin users (default: 3): ") or "3")
        num_vendors = int(input("Enter number of vendors (default: 10): ") or "10")
        num_rfqs = int(input("Enter number of RFQs (default: 5): ") or "5")
        num_auctions = int(input("Enter number of auctions (default: 3): ") or "3")
        
        # Validate inputs
        if any(val < 0 for val in [num_admins, num_vendors, num_rfqs, num_auctions]):
            raise ValueError("All values must be non-negative")
            
        if num_admins < 1:
            print("Warning: At least 1 admin is required. Setting to 1.")
            num_admins = 1
            
    except ValueError as e:
        print(f"Invalid input: {e}")
        print("Using default values...")
        num_admins, num_vendors, num_rfqs, num_auctions = 3, 10, 5, 3
    
    # Generate data
    generator = ProcurementDataGenerator()
    stats = generator.generate_all_data(num_admins, num_vendors, num_auctions, num_rfqs)
    
    # Save to CSV
    print("\nSaving data to CSV files...")
    generator.save_to_csv()
    print("✓ CSV files saved to 'csv_output' directory")
    
    # Print summary
    print("\n=== Generation Summary ===")
    for table, count in stats.items():
        print(f"{table}: {count} records")
    
    print("\n=== CSV Files Created ===")
    csv_files = [
        "users.csv", "user_roles.csv", "vendors.csv", 
        "rfx_events.csv", "rfx_line_items.csv", 
        "rfq_responses.csv", "auction_bids.csv"
    ]
    for file in csv_files:
        print(f"✓ csv_output/{file}")
    
    print("\nData generation completed successfully!")
    print("You can now import these CSV files into your PostgreSQL database.")


if __name__ == "__main__":
    main()
