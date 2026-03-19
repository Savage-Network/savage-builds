-- SpartaMC Builds - Tabela MySQL
-- (c) 2026 Sparta Network. - zrfadev
-- Execute este script no seu banco MySQL antes de ativar o plugin.

CREATE DATABASE IF NOT EXISTS spartamc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE spartamc;

CREATE TABLE IF NOT EXISTS builds (
    uuid VARCHAR(64) PRIMARY KEY,
    username VARCHAR(16) NOT NULL DEFAULT '',
    tag VARCHAR(20) DEFAULT NULL,
    cargo VARCHAR(20) DEFAULT NULL,
    playtime BIGINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
