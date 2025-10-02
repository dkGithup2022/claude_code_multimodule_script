-- Test users for FK constraints
INSERT INTO USERS (USER_ID, EMAIL, NAME, CREATED_AT, UPDATED_AT)
VALUES
    (1, 'fk-user1@example.com', 'FK User One', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    (2, 'fk-user2@example.com', 'FK User Two', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
