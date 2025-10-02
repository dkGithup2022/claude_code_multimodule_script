-- 테스트 데이터

-- Users
INSERT INTO users (email, name, created_at, updated_at)
VALUES
    ('user1@example.com', 'User One', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('user2@example.com', 'User Two', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('user3@example.com', 'User Three', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

-- Coupons
INSERT INTO coupons (name, discount_amount, total_quantity, issued_count, user_id, start_date, end_date, created_at, updated_at)
VALUES
    ('Welcome Coupon', 1000, 100, 0, 1, '2024-01-01 00:00:00.000000', '2024-12-31 23:59:59.999999', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('Summer Sale', 5000, 50, 0, 1, '2024-06-01 00:00:00.000000', '2024-08-31 23:59:59.999999', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('VIP Discount', 10000, 10, 0, 2, '2024-01-01 00:00:00.000000', '2025-12-31 23:59:59.999999', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
