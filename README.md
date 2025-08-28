# GOMA Gorilla Management - Backend API

## System Architecture Overview

### What is a Tenant?

In the GOMA system, a **Tenant** represents a **separate company or business entity** that uses the platform. Think of it as a completely isolated business environment within the same software system.

**Examples of Tenants:**
- **Company A**: "Coca-Cola Rwanda" 
- **Company B**: "Pepsi Distribution Ltd"
- **Company C**: "Local Beverage Company"

Each tenant has:
- Their own users, products, warehouses, distributors
- Completely separate data (no cross-contamination)
- Independent pricing, orders, and inventory
- Custom settings and configurations

**Why Multitenancy?** One GOMA platform can serve multiple beverage companies simultaneously, each with complete data isolation.

### System Flow

The GOMA system orchestrates a **Factory → Distributor → Retail supply chain** where a Sales Manager sets product prices at both factory (L1) and distributor (L2) levels, Distributors place orders to the factory warehouse which get approved by Accountants, Warehouse Managers dispatch approved orders from L1 to L2 distributor stores, Store Managers receive inventory and sell to retailers/customers, all transactions generate invoices that Accountants process with payments (bank/mobile money), daily operations are closed and approved by Accountants for accountability, while the system maintains complete audit trails and generates reports for stock movement, sales KPIs, and financial reconciliation across the entire supply chain within each tenant's isolated environment.

### What is OrderLine?

**OrderLine** represents **individual product items within an order**. It's the detailed breakdown of what's being ordered.

**Example:**
```
Order #ORD-001 (from Distributor to Factory):
├── OrderLine 1: Coca-Cola 500ml × 100 bottles @ 1,250 RWF each = 125,000 RWF
├── OrderLine 2: Fanta Orange 500ml × 50 bottles @ 1,100 RWF each = 55,000 RWF
└── OrderLine 3: Sprite 500ml × 75 bottles @ 1,200 RWF each = 90,000 RWF
```

**Key Features:**
- **Product**: What item is being ordered
- **Quantity**: How many units
- **Unit Price**: Price per item
- **Discounts/Tax**: Applied to this line
- **Line Total**: Calculated automatically
- **Fulfillment Tracking**: How many have been shipped/delivered

### What is BaseEntity?

**BaseEntity** is the **foundation class** that all other entities inherit from. It provides common functionality that every database record needs.

**What BaseEntity Provides:**
- **`id`**: Unique identifier for every record
- **`createdAt`**: When the record was created
- **`updatedAt`**: When it was last modified  
- **`deletedAt`**: For soft deletes (mark as deleted without removing)
- **`version`**: For optimistic locking (prevents concurrent modification conflicts)

**Why Use BaseEntity?**
- **DRY Principle**: Don't repeat these fields in every entity
- **Consistent Auditing**: Every record tracks creation/modification
- **Soft Deletes**: Preserve data for audit trails
- **Concurrency Control**: Prevent data corruption from simultaneous updates

**Example:**
```java
// Instead of writing this in every entity:
public class Product {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // ... product fields
}

// You extend BaseEntity:
public class Product extends BaseEntity {
    // ... only product-specific fields
}
```

This architecture ensures data consistency, auditability, and maintainability across your entire GOMA platform.
