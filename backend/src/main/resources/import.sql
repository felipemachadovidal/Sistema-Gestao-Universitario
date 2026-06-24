DELETE FROM users;
INSERT INTO users (username, password_hash, role)
VALUES ('admin@unifor.br', '$2a$10$eYpnX2//rABRpEDfFXYlQeFM7E0VI8LRjnJlqyirqvugGB.b2kGNu', 'ADMIN');