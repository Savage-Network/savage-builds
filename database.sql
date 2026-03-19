-- SpartaMC Builds - Tabela MySQL
-- (c) 2026 Sparta Network. - zrfadev
-- Execute este script no seu banco MySQL antes de ativar o plugin.


CREATE TABLE IF NOT EXISTS builds (
    uuid VARCHAR(64) PRIMARY KEY,
    username VARCHAR(16) NOT NULL DEFAULT '',
    tag VARCHAR(20) DEFAULT NULL,
    cargo VARCHAR(20) DEFAULT NULL,
    playtime BIGINT DEFAULT 0
)