# API Testing Guide with Bearer Token

This guide provides comprehensive testing examples for all API endpoints using Bearer token authentication.

## Authentication

### 1. Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "userType": "USER"
  }'
```

### 2. Login to get Bearer token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "jwtToken": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "testuser"
}
```

**Save the JWT token for subsequent requests!**

## Customer API Testing

### 1. Create Customer
```bash
curl -X POST http://localhost:8080/api/customer \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customername": "John Doe",
    "emailid": "john@example.com",
    "mobilenumber": 9876543210,
    "companyname": "ABC Corp",
    "address": "123 Main St, City",
    "refferedby": "Website",
    "userno": 1
  }'
```

### 2. Get All Customers
```bash
curl -X GET http://localhost:8080/api/customer \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Get Customer by ID
```bash
curl -X GET http://localhost:8080/api/customer/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Update Customer
```bash
curl -X PUT http://localhost:8080/api/customer/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customername": "John Doe Updated",
    "emailid": "john.updated@example.com",
    "mobilenumber": 9876543211,
    "companyname": "ABC Corp Updated",
    "address": "456 Updated St, City",
    "refferedby": "Referral",
    "userno": 1
  }'
```

### 5. Delete Customer
```bash
curl -X DELETE http://localhost:8080/api/customer/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Item API Testing

### 1. Create Item
```bash
curl -X POST http://localhost:8080/api/items \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idname": "ITEM001",
    "itemname": "Software License",
    "licensetype": "Annual",
    "price": 1000.00,
    "createdby": "admin",
    "isactive": true
  }'
```

### 2. Get All Items
```bash
curl -X GET http://localhost:8080/api/items \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Get Item by ID
```bash
curl -X GET http://localhost:8080/api/items/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Update Item
```bash
curl -X PUT http://localhost:8080/api/items/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idname": "ITEM001_UPDATED",
    "itemname": "Software License Updated",
    "licensetype": "Monthly",
    "price": 1200.00,
    "updatedby": "admin",
    "isactive": true
  }'
```

### 5. Delete Item
```bash
curl -X DELETE http://localhost:8080/api/items/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Quotation API Testing

### 1. Create Quotation
```bash
curl -X POST http://localhost:8080/api/quat \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "quatno": "QUO-2025-001",
    "quatDate": "2025-01-15T10:00:00",
    "validity": 30,
    "customer": {"id": 1},
    "user": {"id": 1}
  }'
```

### 2. Get All Quotations
```bash
curl -X GET http://localhost:8080/api/quat \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Get Quotation by ID
```bash
curl -X GET http://localhost:8080/api/quat/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Get Quotations by Customer ID
```bash
curl -X GET http://localhost:8080/api/quat/customer/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Update Quotation
```bash
curl -X PUT http://localhost:8080/api/quat/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "quatno": "QUO-2025-001-UPDATED",
    "quatDate": "2025-01-16T10:00:00",
    "validity": 45,
    "customer": {"id": 1},
    "user": {"id": 1}
  }'
```

### 6. Generate Quotation PDF
```bash
curl -X GET http://localhost:8080/api/quat/1/quotation \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output quotation_1.pdf
```

## Quotation Items API Testing

### 1. Create Quotation Item
```bash
curl -X POST http://localhost:8080/api/qitem \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 5,
    "unitPrice": 1000.00,
    "licenseType": "Annual",
    "quotation": {"id": 1},
    "item": {"id": 1}
  }'
```

### 2. Get All Quotation Items
```bash
curl -X GET http://localhost:8080/api/qitem \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Get Quotation Items by Quotation ID
```bash
curl -X GET http://localhost:8080/api/qitem/quotation/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Update Quotation Item
```bash
curl -X PUT http://localhost:8080/api/qitem/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 10,
    "unitPrice": 950.00,
    "licenseType": "Annual",
    "quotation": {"id": 1},
    "item": {"id": 1}
  }'
```

## Invoice API Testing

### 1. Create Invoice
```bash
curl -X POST http://localhost:8080/api/invoices \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "invoiceno": "INV-2025-001",
    "invoiceDate": "2025-01-15T10:00:00",
    "dueDate": "2025-02-15T10:00:00",
    "validity": 30,
    "status": "DRAFT",
    "paymentStatus": "UNPAID",
    "notes": "Payment terms: Net 30",
    "terms": "Standard terms and conditions",
    "customer": {"id": 1},
    "user": {"id": 1}
  }'
```

### 2. Create Invoice from Quotation
```bash
curl -X POST http://localhost:8080/api/invoices/from-quotation/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Get All Invoices
```bash
curl -X GET http://localhost:8080/api/invoices \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Get Invoice by ID
```bash
curl -X GET http://localhost:8080/api/invoices/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Get Invoices by Customer ID
```bash
curl -X GET http://localhost:8080/api/invoices/customer/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 6. Get Overdue Invoices
```bash
curl -X GET http://localhost:8080/api/invoices/overdue \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 7. Update Invoice
```bash
curl -X PUT http://localhost:8080/api/invoices/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "invoiceno": "INV-2025-001-UPDATED",
    "invoiceDate": "2025-01-16T10:00:00",
    "dueDate": "2025-02-16T10:00:00",
    "validity": 45,
    "status": "SENT",
    "paymentStatus": "UNPAID",
    "notes": "Updated payment terms",
    "terms": "Updated terms and conditions",
    "customer": {"id": 1},
    "user": {"id": 1}
  }'
```

### 8. Update Invoice Status
```bash
curl -X PATCH "http://localhost:8080/api/invoices/1/status?status=SENT" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 9. Update Payment Status
```bash
curl -X PATCH "http://localhost:8080/api/invoices/1/payment-status?paymentStatus=PAID" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 10. Generate Invoice PDF
```bash
curl -X GET http://localhost:8080/api/invoices/1/pdf \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output invoice_1.pdf
```

### 11. Delete Invoice
```bash
curl -X DELETE http://localhost:8080/api/invoices/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## User Management API Testing

### 1. Get All Users (Admin only)
```bash
curl -X GET http://localhost:8080/api/logins \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Get User by ID
```bash
curl -X GET http://localhost:8080/api/logins/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Create User
```bash
curl -X POST http://localhost:8080/api/logins \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "firstName": "New",
    "lastName": "User",
    "userType": "USER",
    "isActive": true
  }'
```

### 4. Update User
```bash
curl -X PUT http://localhost:8080/api/logins/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "email": "updated@example.com",
    "firstName": "Updated",
    "lastName": "User",
    "userType": "USER",
    "isActive": true
  }'
```

## Error Handling Examples

### 1. Unauthorized Access (No Token)
```bash
curl -X GET http://localhost:8080/api/customer
```
**Expected Response:** 401 Unauthorized

### 2. Invalid Token
```bash
curl -X GET http://localhost:8080/api/customer \
  -H "Authorization: Bearer invalid_token"
```
**Expected Response:** 403 Forbidden

### 3. Resource Not Found
```bash
curl -X GET http://localhost:8080/api/customer/999 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```
**Expected Response:** 404 Not Found

### 4. Validation Error
```bash
curl -X POST http://localhost:8080/api/customer \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customername": "",
    "emailid": "invalid-email"
  }'
```
**Expected Response:** 400 Bad Request with validation errors

## Testing with Postman

### 1. Set up Environment Variables
- Create a new environment in Postman
- Add variable: `baseUrl` = `http://localhost:8080/api`
- Add variable: `token` = `YOUR_JWT_TOKEN`

### 2. Set Authorization Header
- In each request, go to Authorization tab
- Select "Bearer Token"
- Enter `{{token}}` in the Token field

### 3. Sample Collection Structure
```
Invoice Management API
├── Auth
│   ├── Register
│   └── Login
├── Customers
│   ├── Create Customer
│   ├── Get All Customers
│   ├── Get Customer by ID
│   ├── Update Customer
│   └── Delete Customer
├── Items
│   ├── Create Item
│   ├── Get All Items
│   ├── Get Item by ID
│   ├── Update Item
│   └── Delete Item
├── Quotations
│   ├── Create Quotation
│   ├── Get All Quotations
│   ├── Get Quotation by ID
│   ├── Update Quotation
│   ├── Generate Quotation PDF
│   └── Delete Quotation
├── Quotation Items
│   ├── Create Quotation Item
│   ├── Get All Quotation Items
│   ├── Get Items by Quotation
│   ├── Update Quotation Item
│   └── Delete Quotation Item
└── Invoices
    ├── Create Invoice
    ├── Create Invoice from Quotation
    ├── Get All Invoices
    ├── Get Invoice by ID
    ├── Get Invoices by Customer
    ├── Get Overdue Invoices
    ├── Update Invoice
    ├── Update Invoice Status
    ├── Update Payment Status
    ├── Generate Invoice PDF
    └── Delete Invoice
```

## Response Status Codes

- **200 OK**: Successful GET, PUT, PATCH requests
- **201 Created**: Successful POST requests
- **400 Bad Request**: Validation errors, malformed requests
- **401 Unauthorized**: Missing or invalid authentication
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate resource (e.g., duplicate invoice number)
- **500 Internal Server Error**: Server-side errors

## Notes

1. Replace `YOUR_JWT_TOKEN` with the actual token received from login
2. Ensure the MySQL database is running and accessible
3. The application runs on port 8080 by default
4. All timestamps should be in ISO 8601 format
5. PDF generation requires proper data setup (customer, items, etc.)
6. Some endpoints may require ADMIN role for access