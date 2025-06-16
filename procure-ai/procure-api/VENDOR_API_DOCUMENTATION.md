# Vendor API Endpoints

This document describes the vendor management endpoints in the ProcureAI API.

## Base URL
```
http://localhost:8080/api/admin
```

## Authentication
All endpoints require Bearer token authentication with `CREATOR` role.

## Endpoints

### 1. Get All Vendors
**GET** `/vendors`

Retrieve a list of all registered vendors with optional filtering and search capabilities.

#### Query Parameters
- `approved` (optional, boolean): Filter by approval status
  - `true` - Get only approved vendors
  - `false` - Get only pending/unapproved vendors
  - omit - Get all vendors
- `search` (optional, string): Search vendors by company name (case-insensitive)

#### Examples
```bash
# Get all vendors
GET /admin/vendors

# Get only approved vendors
GET /admin/vendors?approved=true

# Search vendors by company name
GET /admin/vendors?search=tech

# Get approved vendors with "alpha" in company name
GET /admin/vendors?approved=true&search=alpha
```

#### Response
```json
{
  "success": true,
  "message": "Vendors retrieved successfully",
  "data": [
    {
      "id": 1,
      "companyName": "Alpha Tech Solutions",
      "registrationNumber": "REG001",
      "taxId": "TAX001",
      "address": "123 Tech Street",
      "city": "San Francisco",
      "state": "CA",
      "postalCode": "94105",
      "country": "USA",
      "contactPerson": "Alice Johnson",
      "isApproved": true,
      "createdAt": "2025-06-16T21:28:59.000Z",
      "updatedAt": "2025-06-16T21:28:59.000Z",
      "userId": 3,
      "username": "vendor1",
      "email": "vendor1@supplier.com",
      "firstName": "Alice",
      "lastName": "Johnson",
      "phone": "+1234567892"
    }
  ]
}
```

### 2. Get Vendor by ID
**GET** `/vendors/{vendorId}`

Retrieve a specific vendor by their ID.

#### Path Parameters
- `vendorId` (required, number): The ID of the vendor to retrieve

#### Example
```bash
GET /admin/vendors/1
```

#### Response
```json
{
  "success": true,
  "message": "Vendor retrieved successfully",
  "data": {
    "id": 1,
    "companyName": "Alpha Tech Solutions",
    "registrationNumber": "REG001",
    // ... other vendor fields
  }
}
```

### 3. Get Vendor Statistics
**GET** `/vendors/stats`

Get summary statistics about vendors in the system.

#### Example
```bash
GET /admin/vendors/stats
```

#### Response
```json
{
  "success": true,
  "message": "Vendor statistics retrieved successfully",
  "data": {
    "totalVendors": 3,
    "approvedVendors": 3,
    "pendingVendors": 0,
    "approvalRate": 100.0
  }
}
```

### 4. Approve Vendor
**POST** `/vendors/{vendorId}/approve`

Approve a vendor registration.

#### Path Parameters
- `vendorId` (required, number): The ID of the vendor to approve

#### Example
```bash
POST /admin/vendors/1/approve
```

#### Response
```json
{
  "success": true,
  "message": "Vendor approved successfully",
  "data": {
    "id": 1,
    "companyName": "Alpha Tech Solutions",
    "isApproved": true,
    // ... other vendor fields
  }
}
```

### 5. Reject Vendor
**POST** `/vendors/{vendorId}/reject`

Reject a vendor registration.

#### Path Parameters
- `vendorId` (required, number): The ID of the vendor to reject

#### Example
```bash
POST /admin/vendors/1/reject
```

#### Response
```json
{
  "success": true,
  "message": "Vendor rejected successfully",
  "data": {
    "id": 1,
    "companyName": "Alpha Tech Solutions",
    "isApproved": false,
    // ... other vendor fields
  }
}
```

### 6. Create Vendor
**POST** `/vendors`

Create a new vendor registration.

#### Request Body
```json
{
  "companyName": "New Tech Corp",
  "registrationNumber": "REG123456",
  "taxId": "TAX789012",
  "address": "123 Tech Street",
  "city": "Tech City",
  "state": "TS",
  "postalCode": "12345",
  "country": "Techland",
  "contactPerson": "John Tech",
  "isApproved": false
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Vendor created successfully",
  "data": {
    "id": 4,
    "companyName": "New Tech Corp",
    "registrationNumber": "REG123456",
    "taxId": "TAX789012",
    "address": "123 Tech Street",
    "city": "Tech City",
    "state": "TS",
    "postalCode": "12345",
    "country": "Techland",
    "contactPerson": "John Tech",
    "isApproved": false,
    "createdAt": "2025-06-16T10:30:00",
    "updatedAt": "2025-06-16T10:30:00"
  }
}
```

#### Error Responses
- **409 Conflict**: Vendor with same registration number or tax ID already exists
- **400 Bad Request**: Invalid vendor data

### 7. Update Vendor
**PUT** `/vendors/{vendorId}`

Update an existing vendor's information.

#### Path Parameters
- `vendorId` (required, number): The ID of the vendor to update

#### Request Body
```json
{
  "companyName": "Updated Tech Corp",
  "contactPerson": "Jane Tech",
  "isApproved": true
}
```

#### Response
```json
{
  "success": true,
  "message": "Vendor updated successfully",
  "data": {
    "id": 4,
    "companyName": "Updated Tech Corp",
    "registrationNumber": "REG123456",
    "taxId": "TAX789012",
    "address": "123 Tech Street",
    "city": "Tech City",
    "state": "TS",
    "postalCode": "12345",
    "country": "Techland",
    "contactPerson": "Jane Tech",
    "isApproved": true,
    "createdAt": "2025-06-16T10:30:00",
    "updatedAt": "2025-06-16T11:45:00"
  }
}
```

#### Error Responses
- **404 Not Found**: Vendor not found
- **409 Conflict**: Duplicate registration number or tax ID
- **400 Bad Request**: Invalid vendor data

### 8. Delete Vendor
**DELETE** `/vendors/{vendorId}`

Delete a vendor from the system.

#### Path Parameters
- `vendorId` (required, number): The ID of the vendor to delete

#### Response
```json
{
  "success": true,
  "message": "Vendor deleted successfully",
  "data": null
}
```

#### Error Responses
- **404 Not Found**: Vendor not found
- **409 Conflict**: Cannot delete vendor with active bids/responses

## Public Endpoints

### Get Approved Vendors (Public)
**GET** `/public/vendors`

Retrieve approved vendors without authentication (for public viewing).

#### Query Parameters
- `search` (optional, string): Search by company name

#### Example
```bash
GET /api/public/vendors?search=tech
```

#### Response
```json
{
  "success": true,
  "message": "Approved vendors retrieved successfully",
  "data": [
    {
      "id": 1,
      "companyName": "Alpha Tech Solutions",
      "address": "123 Business St",
      "city": "Business City",
      "state": "BC",
      "country": "Businessland",
      "contactPerson": "John Alpha",
      "isApproved": true
      // Note: Sensitive fields like taxId are included for admin endpoints only
    }
  ]
}
```

### Get Approved Vendor Count (Public)
**GET** `/public/vendors/count`

Get the total number of approved vendors.

#### Response
```json
{
  "success": true,
  "message": "Approved vendor count retrieved successfully",
  "data": 3
}
```

## Error Responses

All endpoints may return the following error responses:

### 401 Unauthorized
```json
{
  "success": false,
  "message": "Unauthorized - Admin role required",
  "data": null
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "Forbidden - Insufficient permissions",
  "data": null
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Vendor not found with ID: {vendorId}",
  "data": null
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "Failed to retrieve vendors: {error details}",
  "data": null
}
```

## Authentication Example

```bash
# First, login to get the JWT token
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password"}'

# Use the token in subsequent requests
curl -X GET http://localhost:8080/api/admin/vendors \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Frontend Integration

The frontend API client in `/src/lib/api.ts` should call these endpoints:

```typescript
// Complete vendor API client methods
class ApiClient {
  // Get all vendors (admin)
  async getVendors(): Promise<Vendor[]> {
    const response = await this.client.get('/admin/vendors');
    return response.data.data;
  }

  // Get vendor by ID (admin)
  async getVendor(id: number): Promise<Vendor> {
    const response = await this.client.get(`/admin/vendors/${id}`);
    return response.data.data;
  }

  // Create vendor (admin)
  async createVendor(vendor: CreateVendorRequest): Promise<Vendor> {
    const response = await this.client.post('/admin/vendors', vendor);
    return response.data.data;
  }

  // Update vendor (admin)
  async updateVendor(id: number, vendor: UpdateVendorRequest): Promise<Vendor> {
    const response = await this.client.put(`/admin/vendors/${id}`, vendor);
    return response.data.data;
  }

  // Delete vendor (admin)
  async deleteVendor(id: number): Promise<void> {
    await this.client.delete(`/admin/vendors/${id}`);
  }

  // Get approved vendors (public)
  async getApprovedVendors(search?: string): Promise<Vendor[]> {
    const params = search ? { search } : {};
    const response = await this.client.get('/public/vendors', { params });
    return response.data.data;
  }

  // Get vendor statistics (admin)
  async getVendorStats(): Promise<VendorStats> {
    const response = await this.client.get('/admin/vendors/stats');
    return response.data.data;
  }

  // Approve vendor (admin)
  async approveVendor(id: number): Promise<Vendor> {
    const response = await this.client.post(`/admin/vendors/${id}/approve`);
    return response.data.data;
  }

  // Reject vendor (admin)
  async rejectVendor(id: number): Promise<Vendor> {
    const response = await this.client.post(`/admin/vendors/${id}/reject`);
    return response.data.data;
  }
}
```
