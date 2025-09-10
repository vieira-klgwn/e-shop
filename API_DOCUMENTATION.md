# Stock Management System - API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

All endpoints (except registration and login) require JWT token in Authorization header:
```
Authorization: Bearer <jwt-token>
```

### 1. Register User
**POST** `/auth/register`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "STORE_MANAGER",
  "gender": "MALE"
}
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "User registered successfully"
}
```

### 2. Login
**POST** `/auth/authenticate`

**Request Body:**
```json
{
  "email": "admin@acme.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Authentication successful"
}
```

### 3. Refresh Token
**POST** `/auth/refresh-token`

**Headers:**
```
Authorization: Bearer <refresh-token>
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 4. Logout
**POST** `/auth/logout`

**Headers:**
```
Authorization: Bearer <access-token>
```

**Response:**
```json
{
  "message": "Logout successful"
}
```

## Products

### 5. List Products
**GET** `/products?page=0&size=10`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "sku": "LAPTOP-001",
      "name": "Gaming Laptop",
      "description": "High-performance gaming laptop",
      "price": 120000,
      "category": "ELECTRONICS",
      "isActive": true,
      "barcode": null,
      "imageUrl": null,
      "createdAt": "2024-01-15T10:30:00.000Z",
      "updatedAt": "2024-01-15T10:30:00.000Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### 6. Get Product by ID
**GET** `/products/1`

**Response:**
```json
{
  "id": 1,
  "sku": "LAPTOP-001",
  "name": "Gaming Laptop",
  "description": "High-performance gaming laptop",
  "price": 120000,
  "category": "ELECTRONICS",
  "isActive": true,
  "attributes": {
    "color": "black",
    "warranty": "2 years"
  }
}
```

### 7. Create Product
**POST** `/products`

**Request Body:**
```json
{
  "sku": "PHONE-001",
  "name": "Smartphone",
  "description": "Latest smartphone with advanced features",
  "price": 85000,
  "category": "ELECTRONICS",
  "isActive": true,
  "barcode": "1234567890123",
  "attributes": {
    "color": "blue",
    "storage": "128GB"
  }
}
```

**Response:**
```json
{
  "id": 4,
  "sku": "PHONE-001",
  "name": "Smartphone",
  "description": "Latest smartphone with advanced features",
  "price": 85000,
  "category": "ELECTRONICS",
  "isActive": true,
  "barcode": "1234567890123",
  "createdAt": "2024-01-15T11:00:00.000Z"
}
```

### 8. Update Product
**PUT** `/products/4`

**Request Body:**
```json
{
  "name": "Advanced Smartphone",
  "price": 90000,
  "description": "Updated smartphone with latest features"
}
```

### 9. Delete Product
**DELETE** `/products/4`

**Response:**
```json
{
  "message": "Product deleted successfully"
}
```

## Inventory

### 10. List Inventory
**GET** `/inventory?page=0&size=10`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "locationType": "WAREHOUSE",
      "locationId": 1,
      "product": {
        "id": 1,
        "sku": "LAPTOP-001",
        "name": "Gaming Laptop"
      },
      "qtyOnHand": 50,
      "qtyReserved": 0,
      "qtyAvailable": 50,
      "reorderLevel": 10,
      "maxLevel": 100,
      "avgUnitCost": 115000.00,
      "isLowStock": false
    }
  ]
}
```

### 11. Get Inventory by ID
**GET** `/inventory/1`

### 12. Get Inventory by Product
**GET** `/inventory/by-product?productId=1`

### 13. Create/Update Inventory
**POST** `/inventory`

**Request Body:**
```json
{
  "locationType": "STORE",
  "locationId": 1,
  "productId": 2,
  "qtyOnHand": 100,
  "reorderLevel": 15,
  "maxLevel": 200,
  "avgUnitCost": 2000.00
}
```

### 14. Delete Inventory
**DELETE** `/inventory/1`

## Orders

### 15. List Orders
**GET** `/orders?page=0&size=10&status=DRAFT`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "number": "ORD-1642234567890",
      "level": "STORE_TO_WAREHOUSE",
      "channel": "WEB",
      "status": "DRAFT",
      "currency": "USD",
      "orderLines": [],
      "createdAt": "2024-01-15T10:00:00.000Z",
      "deliveryDate": null
    }
  ]
}
```

### 16. Get Order by ID
**GET** `/orders/1`

### 17. Create Order
**POST** `/orders`

**Request Body:**
```json
{
  "level": "STORE_TO_WAREHOUSE",
  "channel": "WEB",
  "storeId": 1,
  "warehouseId": 1,
  "currency": "USD",
  "deliveryDate": "2024-02-01T10:00:00.000Z",
  "deliveryAddress": "123 Main St, City",
  "notes": "Urgent order",
  "orderLines": [
    {
      "productId": 1,
      "qty": 5,
      "unitPrice": 115000,
      "notes": "Handle with care"
    },
    {
      "productId": 2,
      "qty": 10,
      "unitPrice": 2000
    }
  ]
}
```

**Response:**
```json
{
  "id": 2,
  "number": "ORD-1642234567891",
  "level": "STORE_TO_WAREHOUSE",
  "channel": "WEB",
  "status": "DRAFT",
  "currency": "USD",
  "storeId": 1,
  "warehouseId": 1,
  "orderLines": [
    {
      "id": 1,
      "productId": 1,
      "qty": 5,
      "unitPrice": 115000,
      "lineTotal": 575000
    }
  ],
  "createdAt": "2024-01-15T12:00:00.000Z"
}
```

### 18. Update Order
**PUT** `/orders/2`

**Request Body:**
```json
{
  "deliveryAddress": "456 Updated St, City",
  "notes": "Updated delivery instructions"
}
```

### 19. Submit Order
**PUT** `/orders/submit/2`

**Response:**
```json
{
  "message": "Order submitted successfully",
  "order": {
    "id": 2,
    "status": "SUBMITTED",
    "submittedAt": "2024-01-15T12:30:00.000Z"
  }
}
```

### 20. Approve Order
**PUT** `/orders/approve/2`

**Response:**
```json
{
  "message": "Order approved successfully",
  "order": {
    "id": 2,
    "status": "APPROVED",
    "approvedAt": "2024-01-15T13:00:00.000Z"
  },
  "invoice": {
    "id": 1,
    "number": "INV-1642234567892"
  }
}
```

### 21. Reject Order
**PUT** `/orders/reject/2`

**Request Body:**
```json
{
  "reason": "Insufficient inventory"
}
```

### 22. Fulfill Order
**PUT** `/orders/fulfill/2`

**Response:**
```json
{
  "message": "Order fulfilled successfully",
  "stockTransactions": [
    {
      "id": 1,
      "type": "OUT",
      "productId": 1,
      "qty": 5,
      "locationType": "WAREHOUSE"
    }
  ]
}
```

## Invoices

### 23. List Invoices
**GET** `/invoices?page=0&size=10&status=ISSUED`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "number": "INV-1642234567892",
      "orderId": 2,
      "status": "ISSUED",
      "totalAmount": 595000,
      "paidAmount": 0,
      "balanceAmount": 595000,
      "issuedAt": "2024-01-15T13:00:00.000Z",
      "dueDate": "2024-02-14"
    }
  ]
}
```

### 24. Get Invoice by ID
**GET** `/invoices/1`

### 25. Create Invoice
**POST** `/invoices`

**Request Body:**
```json
{
  "orderId": 2,
  "storeId": 1,
  "dueDate": "2024-02-15",
  "currency": "USD",
  "notes": "Payment due in 30 days"
}
```

### 26. Update Invoice
**PUT** `/invoices/1`

**Request Body:**
```json
{
  "dueDate": "2024-02-20",
  "notes": "Extended payment terms"
}
```

## Payments

### 27. List Payments
**GET** `/payments?page=0&size=10&invoiceId=1`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "invoiceId": 1,
      "method": "BANK_TRANSFER",
      "amount": 295000.00,
      "currency": "USD",
      "txnRef": "TXN-202401151400",
      "paymentStatus": "SUCCESS",
      "paidAt": "2024-01-15T14:00:00.000Z",
      "postedBy": "admin@acme.com"
    }
  ]
}
```

### 28. Process Payment
**POST** `/payments/process?invoiceId=1&amount=300000&method=BANK_TRANSFER&txnRef=TXN-202401151500`

**Response:**
```json
{
  "message": "Payment processed successfully",
  "payment": {
    "id": 2,
    "amount": 300000.00,
    "txnRef": "TXN-202401151500",
    "paymentStatus": "SUCCESS"
  },
  "invoice": {
    "id": 1,
    "balanceAmount": 0.00,
    "status": "PAID"
  }
}
```

## Transfers

### 29. List Transfers
**GET** `/transfers?page=0&size=10&status=PENDING`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "fromLevel": "WAREHOUSE",
      "fromLocationId": 1,
      "toLevel": "STORE",
      "toLocationId": 1,
      "productId": 1,
      "qty": 10,
      "status": "PENDING",
      "notes": "Regular stock transfer"
    }
  ]
}
```

### 30. Create Transfer
**POST** `/transfers`

**Request Body:**
```json
{
  "fromLevel": "WAREHOUSE",
  "fromLocationId": 1,
  "toLevel": "STORE",
  "toLocationId": 1,
  "productId": 1,
  "qty": 10,
  "notes": "Monthly inventory transfer"
}
```

### 31. Complete Transfer
**PUT** `/transfers/complete/1`

**Response:**
```json
{
  "message": "Transfer completed successfully",
  "stockTransactions": [
    {
      "fromLocation": "WAREHOUSE-1",
      "toLocation": "STORE-1",
      "productId": 1,
      "qty": 10
    }
  ]
}
```

## Adjustments

### 32. List Adjustments
**GET** `/adjustments?page=0&size=10`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "level": "WAREHOUSE",
      "locationId": 1,
      "productId": 1,
      "qtyDelta": -2,
      "reason": "DAMAGE",
      "note": "Damaged during handling",
      "oldQty": 50,
      "newQty": 48,
      "processedAt": null
    }
  ]
}
```

### 33. Create Adjustment
**POST** `/adjustments`

**Request Body:**
```json
{
  "level": "WAREHOUSE",
  "locationId": 1,
  "productId": 1,
  "qtyDelta": -5,
  "reason": "EXPIRED",
  "note": "Products expired, removing from inventory"
}
```

### 34. Process Adjustment
**PUT** `/adjustments/process/1`

**Response:**
```json
{
  "message": "Adjustment processed successfully",
  "adjustment": {
    "id": 1,
    "processedAt": "2024-01-15T15:00:00.000Z",
    "newQty": 45
  },
  "stockTransaction": {
    "id": 3,
    "type": "ADJUSTMENT",
    "qty": -5
  }
}
```

## Notifications

### 35. List Notifications
**GET** `/notifications?page=0&size=10&type=LOW_STOCK`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "type": "LOW_STOCK",
      "title": "Low Stock Alert",
      "channel": "EMAIL",
      "subject": "Product running low",
      "message": "Product LAPTOP-001 is running low in WAREHOUSE-1",
      "status": "SENT",
      "sentAt": "2024-01-15T16:00:00.000Z",
      "recipientAddress": "manager@acme.com"
    }
  ]
}
```

## Reports

### 36. Sales Report
**GET** `/reports/sales?from=2024-01-01&to=2024-01-31&productId=1`

**Response:**
```json
{
  "period": {
    "from": "2024-01-01",
    "to": "2024-01-31"
  },
  "totalSales": 1150000,
  "totalOrders": 2,
  "products": [
    {
      "productId": 1,
      "productName": "Gaming Laptop",
      "quantity": 10,
      "revenue": 1150000
    }
  ]
}
```

### 37. Inventory Report
**GET** `/reports/inventory?lowStock=true`

**Response:**
```json
{
  "totalProducts": 3,
  "lowStockItems": 1,
  "items": [
    {
      "productId": 1,
      "productName": "Gaming Laptop",
      "locationType": "WAREHOUSE",
      "locationId": 1,
      "currentStock": 8,
      "reorderLevel": 10,
      "status": "LOW_STOCK"
    }
  ]
}
```

### 38. Financial Report
**GET** `/reports/financials?from=2024-01-01&to=2024-01-31`

**Response:**
```json
{
  "period": {
    "from": "2024-01-01",
    "to": "2024-01-31"
  },
  "totalRevenue": 1150000,
  "totalPaid": 595000,
  "totalOutstanding": 555000,
  "invoices": {
    "issued": 2,
    "paid": 1,
    "partial": 0,
    "overdue": 1
  }
}
```

## Error Responses

### Validation Error (400)
```json
{
  "timestamp": "2024-01-15T16:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email is required"
    },
    {
      "field": "price",
      "message": "Price must be positive"
    }
  ]
}
```

### Unauthorized (401)
```json
{
  "timestamp": "2024-01-15T16:30:00.000Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is expired or invalid"
}
```

### Forbidden (403)
```json
{
  "timestamp": "2024-01-15T16:30:00.000Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied. Insufficient permissions."
}
```

### Not Found (404)
```json
{
  "timestamp": "2024-01-15T16:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product with ID 999 not found"
}
```

## Testing the API

### Using curl

1. **Register a user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com", 
    "password": "password123",
    "role": "STORE_MANAGER"
  }'
```

2. **Login:**
```bash
curl -X POST http://localhost:8080/api/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@acme.com",
    "password": "password123"
  }'
```

3. **Get products (with JWT token):**
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

4. **Create a product:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "TEST-001",
    "name": "Test Product",
    "price": 5000,
    "category": "ELECTRONICS"
  }'
```

### Using Postman

1. Import the above examples as a Postman collection
2. Set up environment variables for base URL and JWT token
3. Use the authentication endpoints to get tokens
4. Test all CRUD operations for each resource

### API Documentation via Swagger

The API also includes Swagger documentation accessible at:
```
http://localhost:8080/swagger-ui/index.html
```

This provides an interactive interface to test all endpoints directly from the browser.
