Enterprise-grade User Management System built with Spring Boot, Spring Security, JWT, MySQL, Email OTP Verification, Forgot Password, Role-Based Access Control, Swagger Documentation, and Admin Dashboard APIs.

Features

Authentication & Authorization
✅ User Registration
✅ Email OTP Verification
✅ OTP Resend Functionality
✅ Secure Login using Email or Mobile Number
✅ JWT Token Authentication
✅ Role-Based Access Control (RBAC)

Password Management
✅ Change Password
✅ Forgot Password OTP
✅ Resend Forgot Password OTP
✅ Reset Password
✅ BCrypt Password Encryption
✅ Password Reuse Prevention

User Module
✅ View Profile
✅ Update Profile
✅ Change Password

Admin Module
✅ View All Users
✅ Search Users
✅ Pagination Support
✅ Delete User
✅ Activate User
✅ Deactivate User
✅ Users Statistics

Security Features
✅ Spring Security
✅ JWT Authentication
✅ BCrypt Password Hashing
✅ Method-Level Security
✅ URL-Level Security
✅ Input Validation
✅ Global Exception Handling
✅ Scheduled Cleanup of Unverified Accounts


API Documentation
✅ Swagger UI


Tech Stack
✅Java
✅Spring Boot
✅Spring Security
✅Spring Data JPA
✅Hibernate
✅MySQL
✅Swagger / OpenAPI
✅Maven


Authentication APIs
- POST /api/v1/auth/register/send-otp: Send OTP for registration verification
- POST /api/v1/auth/register/verify-otp: Verify email OTP
- POST /api/v1/auth/register: Complete user registration
- POST /api/v1/auth/login: User authentication and JWT token generation
- POST /api/v1/auth/forgot-password/send-otp: Send OTP for password reset
- POST /api/v1/auth/forgot-password/resend-otp: Resend OTP for password reset
- POST /api/v1/auth/forgot-password/reset: Reset password using OTP

User APIs
- GET /api/v1/user/profile: Fetch the logged-in user’s profile details (requires authentication)
- PUT /api/v1/user/profile: Update the logged-in user’s profile (requires authentication)
- POST /api/v1/user/change-password: Change password for the logged-in user (requires authentication)


Admin APIs
- GET /api/v1/admin/paginated-users: Get paginated user list (Admin role)
- DELETE /api/v1/admin/delete-user/{userId}: Delete user by ID (Admin role)
- GET /api/v1/admin/users/statistics: Get user stats (total, active, inactive)
- POST /api/v1/admin/{userId}/toggle-user-status: Activate/Deactivate user
- GET /api/v1/admin/users/search: Search users by username or email
- GET /api/v1/admin/users/active: Get active users with pagination
- GET /api/v1/admin/users/inactive: Get inactive users with pagination

Scheduler
- Automated cleanup of unverified accounts (removes users who didn't verify their email in 24 hours)

