/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

-- URL Shortener 초기 테스트 데이터

-- 테스트 사용자
INSERT INTO USERS (EMAIL, NAME, CREATED_AT, UPDATED_AT) VALUES
('alice@example.com', 'Alice', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bob@example.com', 'Bob', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('charlie@example.com', 'Charlie', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트 링크
INSERT INTO LINKS (ORIGINAL_URL, SHORT_CODE, USER_ID, EXPIRES_AT, CREATED_AT, UPDATED_AT) VALUES
('https://www.google.com', 'google', 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('https://www.github.com', 'github', 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('https://stackoverflow.com/questions/12345', 'so12345', 2, DATEADD('DAY', 30, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('https://www.youtube.com/watch?v=dQw4w9WgXcQ', 'yt-rick', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('https://docs.spring.io/spring-boot/docs/current/reference/html/', 'spring-docs', 2, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트 클릭 기록
INSERT INTO LINK_CLICKS (LINK_ID, CLICKED_AT, IP_ADDRESS, USER_AGENT, REFERER) VALUES
(1, DATEADD('HOUR', -5, CURRENT_TIMESTAMP), '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://twitter.com'),
(1, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://facebook.com'),
(1, DATEADD('HOUR', -1, CURRENT_TIMESTAMP), '192.168.1.102', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0)', NULL),
(2, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://google.com'),
(2, DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), '192.168.1.104', 'Mozilla/5.0 (Linux; Android 11)', 'https://reddit.com'),
(3, DATEADD('HOUR', -4, CURRENT_TIMESTAMP), '192.168.1.105', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://stackoverflow.com'),
(4, DATEADD('HOUR', -6, CURRENT_TIMESTAMP), '192.168.1.106', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', NULL),
(4, DATEADD('HOUR', -5, CURRENT_TIMESTAMP), '192.168.1.107', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://twitter.com'),
(4, DATEADD('HOUR', -4, CURRENT_TIMESTAMP), '192.168.1.108', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0)', NULL),
(4, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), '192.168.1.109', 'Mozilla/5.0 (Linux; Android 11)', 'https://reddit.com');