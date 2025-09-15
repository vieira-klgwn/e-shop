### Multi-Tenancy Architecture and Fixes

This document explains how tenant isolation works end-to-end after recent fixes.

#### Overview
- Per-request tenant scoping via `TenantContext` (`ThreadLocal<Long>`).
- Hibernate dynamic filter `tenantFilter` enforces `tenant_id = :tenantId` on multi-tenant entities.
- SUPER_ADMIN users operate with `tenantId=0` (global/no filter applied).

#### Request Lifecycle
1) Servlet Filters
   - `TenantFilter` (ordered before JWT filter):
     - Skips only explicit public endpoints.
     - If there is an authenticated principal, sets `TenantContext` using the user's tenant; SUPER_ADMIN → `0`.
   - `JwtAuthenticationFilter`:
     - Authenticates Bearer tokens.
     - Extracts `tenantId` claim and sets `TenantContext`.
     - SUPER_ADMIN defaults to `0` when tenant claim is absent.
     - Clears `TenantContext` in `finally`.

2) Repository Access
   - `TenantFilterAspect` runs `@Before` repository methods.
   - Enables Hibernate filter with `tenantId` unless `tenantId` is `null` or `0`.

#### JWT Claims
- `JwtService.generateToken(UserDetails)` sets:
  - `authorities`: list of authorities
  - `tenantId`: user's tenant id; if role is SUPER_ADMIN → `0`

#### Security
- Public endpoints: `/api/auth/login`, `/api/auth/refresh-token`, `/api/auth/forgot-password`, `/api/auth/request-password-reset`, `/api/auth/reset-password`, `/api/tenants/admin`, Swagger and error endpoints.
- `/api/auth/register`: `@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','MANAGING_DIRECTOR')")`.

#### Entities
- Use `@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")` on tenant-scoped entities such as `User`, `Product`, etc.

#### Notes
- If you need SUPER_ADMIN to see specific tenants only, remove the `tenantId=0` shortcut and pass explicit tenantId claims.
- Always ensure `TenantContext` is cleared after the request.

#### Test Flow
1) SUPER_ADMIN login → receives token with `tenantId=0`.
2) Create tenant via `/api/tenants/admin`.
3) Tenant admin login → token carries `tenantId=<tenant id>`.
4) Register sub-users via `/api/auth/register`.
5) Query tenant resources (e.g., `/api/products`) → results scoped to tenant.


