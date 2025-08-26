-- Insert built-in admin user (password: admin123)
-- Note: In production, this should be a secure password
INSERT INTO users (id, email, password_hash, name, role, blocked, created_at, updated_at)
VALUES (
    '5fbde2a6-aa8f-3938-8459-2021095ea257',
    'admin@fooddelivery.com',
    '$2a$10$ZKAPwbAfDjaTfNBCLey0.unkP.xcayCAKxReE7.qsDZ85pAMaFydC',
    'System Administrator',
    'ADMIN',
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert sample coupon
INSERT INTO coupons (id, code, discount_percent, expires_at, active, created_at, updated_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440001',
    'WELCOME20',
    20,
    DATEADD('DAY', 30, CURRENT_TIMESTAMP),
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
