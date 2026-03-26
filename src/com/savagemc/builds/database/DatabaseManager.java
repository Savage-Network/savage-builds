/**
 * Savage Network - Software Proprietário
 * Copyright (c) 2026 Savage Network. - zrfadev
 * Todos os direitos reservados.
 */
package com.savagemc.builds.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gerencia conexão e operações MySQL para a tabela builds.
 * (c) 2026 Savage Network. - zrfadev
 */
public class DatabaseManager {

    private static final String TABLE = "builds";

    private final JavaPlugin plugin;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean useSSL;

    private Connection connection;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DatabaseManager(JavaPlugin plugin, String host, int port, String database,
                          String username, String password, boolean useSSL) {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.useSSL = useSSL;
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=" + useSSL
                    + "&autoReconnect=true"
                    + "&characterEncoding=UTF-8"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=America/Sao_Paulo";
            connection = DriverManager.getConnection(url, username, password);
            createTable();
            plugin.getLogger().info("[savagebuilds] Conectado ao MySQL.");
            return true;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("[savagebuilds] MySQL Connector nao encontrado. Adicione mysql-connector-j ao classpath.");
            return false;
        } catch (SQLException e) {
            plugin.getLogger().severe("[savagebuilds] Erro ao conectar MySQL: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro ao fechar conexao MySQL: " + e.getMessage());
        }
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                "uuid VARCHAR(64) PRIMARY KEY," +
                "username VARCHAR(16) NOT NULL DEFAULT ''," +
                "tag VARCHAR(20) DEFAULT NULL," +
                "cargo VARCHAR(20) DEFAULT NULL," +
                "playtime BIGINT DEFAULT 0," +
                "blocks_placed BIGINT DEFAULT 0" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(sql);
            try {
                st.executeUpdate("ALTER TABLE " + TABLE + " ADD COLUMN blocks_placed BIGINT DEFAULT 0");
            } catch (SQLException e) {
                if (e.getMessage() == null || !e.getMessage().toLowerCase().contains("duplicate")) {
                    plugin.getLogger().warning("Migration blocks_placed: " + e.getMessage());
                }
            }
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public void runAsync(Runnable task) {
        executor.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                plugin.getLogger().warning("Erro em tarefa async DB: " + e.getMessage());
            }
        });
    }

    public String getTag(UUID uuid) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT tag FROM " + TABLE + " WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("tag");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro getTag: " + e.getMessage());
        }
        return null;
    }

    public String getCargo(UUID uuid) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT cargo FROM " + TABLE + " WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("cargo");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro getCargo: " + e.getMessage());
        }
        return null;
    }

    public String getPendingCargo(String playerName) {
        String key = "pending:" + playerName.toLowerCase();
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT cargo FROM " + TABLE + " WHERE uuid = ?")) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("cargo");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro getPendingCargo: " + e.getMessage());
        }
        return null;
    }

    public long getPlaytime(UUID uuid) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT playtime FROM " + TABLE + " WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("playtime");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro getPlaytime: " + e.getMessage());
        }
        return 0L;
    }

    public void setTag(UUID uuid, String username, String tag) {
        runAsync(() -> {
            try {
                String sql = "INSERT INTO " + TABLE + " (uuid, username, tag, cargo, playtime) VALUES (?,?,?,NULL,0) " +
                        "ON DUPLICATE KEY UPDATE tag = VALUES(tag), username = VALUES(username)";
                try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, username != null ? username : "");
                    ps.setString(3, tag != null && !tag.isEmpty() ? tag : null);
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Erro setTag: " + e.getMessage());
            }
        });
    }

    public void setCargo(UUID uuid, String username, String cargo) {
        runAsync(() -> {
            try {
                String sql = "INSERT INTO " + TABLE + " (uuid, username, tag, cargo, playtime) VALUES (?,?,NULL,?,0) " +
                        "ON DUPLICATE KEY UPDATE cargo = VALUES(cargo), username = VALUES(username)";
                try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, username != null ? username : "");
                    ps.setString(3, cargo != null && !cargo.isEmpty() ? cargo : null);
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Erro setCargo: " + e.getMessage());
            }
        });
    }

    public void setPendingCargo(String playerName, String cargo) {
        runAsync(() -> {
            try {
                String key = "pending:" + playerName.toLowerCase();
                String sql = "INSERT INTO " + TABLE + " (uuid, username, tag, cargo, playtime) VALUES (?,?,NULL,?,0) " +
                        "ON DUPLICATE KEY UPDATE cargo = VALUES(cargo), username = VALUES(username)";
                try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
                    ps.setString(1, key);
                    ps.setString(2, playerName);
                    ps.setString(3, cargo);
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Erro setPendingCargo: " + e.getMessage());
            }
        });
    }

    public void removePendingCargo(String playerName) {
        runAsync(() -> {
            try (PreparedStatement ps = getConnection().prepareStatement(
                    "DELETE FROM " + TABLE + " WHERE uuid = ?")) {
                ps.setString(1, "pending:" + playerName.toLowerCase());
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("Erro removePendingCargo: " + e.getMessage());
            }
        });
    }

    public void setPlaytime(UUID uuid, String username, long seconds) {
        runAsync(() -> {
            try {
                String sql = "INSERT INTO " + TABLE + " (uuid, username, tag, cargo, playtime) VALUES (?,?,NULL,NULL,?) " +
                        "ON DUPLICATE KEY UPDATE playtime = VALUES(playtime), username = VALUES(username)";
                try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, username != null ? username : "");
                    ps.setLong(3, seconds);
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Erro setPlaytime: " + e.getMessage());
            }
        });
    }

    public void migratePendingToPlayer(String playerName, UUID uuid) {
        runAsync(() -> {
            try {
                String pendingKey = "pending:" + playerName.toLowerCase();
                String cargo;
                try (PreparedStatement ps = getConnection().prepareStatement(
                        "SELECT cargo FROM " + TABLE + " WHERE uuid = ?")) {
                    ps.setString(1, pendingKey);
                    ResultSet rs = ps.executeQuery();
                    cargo = rs.next() ? rs.getString("cargo") : null;
                }
                if (cargo != null) {
                    String sql = "INSERT INTO " + TABLE + " (uuid, username, tag, cargo, playtime) VALUES (?,?,NULL,?,0) " +
                            "ON DUPLICATE KEY UPDATE cargo = VALUES(cargo), username = VALUES(username)";
                    try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
                        ps.setString(1, uuid.toString());
                        ps.setString(2, playerName);
                        ps.setString(3, cargo);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = getConnection().prepareStatement(
                            "DELETE FROM " + TABLE + " WHERE uuid = ?")) {
                        ps.setString(1, pendingKey);
                        ps.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Erro migratePending: " + e.getMessage());
            }
        });
    }

    public java.util.Map<UUID, String> loadAllTags() {
        java.util.Map<UUID, String> map = new java.util.HashMap<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT uuid, tag FROM " + TABLE + " WHERE tag IS NOT NULL AND tag != '' AND uuid NOT LIKE 'pending:%'")) {
            while (rs.next()) {
                try {
                    map.put(UUID.fromString(rs.getString("uuid")), rs.getString("tag"));
                } catch (IllegalArgumentException ignored) {}
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro loadAllTags: " + e.getMessage());
        }
        return map;
    }

    public java.util.Map<UUID, String> loadAllCargos() {
        java.util.Map<UUID, String> map = new java.util.HashMap<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT uuid, cargo FROM " + TABLE + " WHERE cargo IS NOT NULL AND cargo != '' AND uuid NOT LIKE 'pending:%'")) {
            while (rs.next()) {
                try {
                    String cargo = rs.getString("cargo");
                    if (cargo != null && !cargo.isEmpty()) {
                        map.put(UUID.fromString(rs.getString("uuid")), cargo.toLowerCase());
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro loadAllCargos: " + e.getMessage());
        }
        return map;
    }

    public java.util.Map<String, String> loadPendingCargos() {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT uuid, cargo FROM " + TABLE + " WHERE uuid LIKE 'pending:%'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                String cargo = rs.getString("cargo");
                if (uuid != null && uuid.startsWith("pending:") && cargo != null) {
                    map.put(uuid.substring(8).toLowerCase(), cargo.toLowerCase());
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro loadPendingCargos: " + e.getMessage());
        }
        return map;
    }

    public java.util.Map<UUID, Long> loadAllPlaytime() {
        java.util.Map<UUID, Long> map = new java.util.HashMap<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT uuid, playtime FROM " + TABLE + " WHERE uuid NOT LIKE 'pending:%' AND playtime > 0")) {
            while (rs.next()) {
                try {
                    long pt = rs.getLong("playtime");
                    if (pt > 0) {
                        map.put(UUID.fromString(rs.getString("uuid")), pt);
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro loadAllPlaytime: " + e.getMessage());
        }
        return map;
    }

    /**
     * Retorna o top de jogadores por tempo de jogo (uuid, username, tag, playtime em segundos).
     * Ordenado por playtime DESC. Limite padrao 10.
     */
    public List<TopPlaytimeEntry> getTopPlaytime(int limit) {
        List<TopPlaytimeEntry> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT uuid, username, tag, playtime FROM " + TABLE +
                " WHERE uuid NOT LIKE 'pending:%' AND playtime > 0 ORDER BY playtime DESC LIMIT ?")) {
            ps.setInt(1, Math.max(1, Math.min(limit, 100)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        String uuidStr = rs.getString("uuid");
                        String username = rs.getString("username");
                        String tagStr = rs.getString("tag");
                        long playtime = rs.getLong("playtime");
                        if (username == null) username = "?";
                        list.add(new TopPlaytimeEntry(UUID.fromString(uuidStr), username, tagStr, playtime));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro getTopPlaytime: " + e.getMessage());
        }
        return list;
    }

    public static class TopPlaytimeEntry {
        public final UUID uuid;
        public final String username;
        public final String tagName;
        public final long playtimeSeconds;

        public TopPlaytimeEntry(UUID uuid, String username, String tagName, long playtimeSeconds) {
            this.uuid = uuid;
            this.username = username != null ? username : "?";
            this.tagName = tagName;
            this.playtimeSeconds = playtimeSeconds;
        }
    }

    public long getBlocksPlaced(UUID uuid) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT blocks_placed FROM " + TABLE + " WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong("blocks_placed");
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro getBlocksPlaced: " + e.getMessage());
        }
        return 0L;
    }

    public void setBlocksPlaced(UUID uuid, String username, long blocks) {
        runAsync(() -> {
            try {
                String sql = "INSERT INTO " + TABLE + " (uuid, username, tag, cargo, playtime, blocks_placed) VALUES (?,?,NULL,NULL,0,?) " +
                        "ON DUPLICATE KEY UPDATE blocks_placed = VALUES(blocks_placed), username = VALUES(username)";
                try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, username != null ? username : "");
                    ps.setLong(3, blocks);
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("Erro setBlocksPlaced: " + e.getMessage());
            }
        });
    }

    public java.util.Map<UUID, Long> loadAllBlocksPlaced() {
        java.util.Map<UUID, Long> map = new java.util.HashMap<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT uuid, blocks_placed FROM " + TABLE + " WHERE uuid NOT LIKE 'pending:%' AND blocks_placed > 0")) {
            while (rs.next()) {
                try {
                    long bp = rs.getLong("blocks_placed");
                    if (bp > 0) {
                        map.put(UUID.fromString(rs.getString("uuid")), bp);
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro loadAllBlocksPlaced: " + e.getMessage());
        }
        return map;
    }

    public List<TopBlocksEntry> getTopBlocks(int limit) {
        List<TopBlocksEntry> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT uuid, username, tag, blocks_placed FROM " + TABLE +
                " WHERE uuid NOT LIKE 'pending:%' AND blocks_placed > 0 ORDER BY blocks_placed DESC LIMIT ?")) {
            ps.setInt(1, Math.max(1, Math.min(limit, 100)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        String uuidStr = rs.getString("uuid");
                        String username = rs.getString("username");
                        String tagStr = rs.getString("tag");
                        long blocks = rs.getLong("blocks_placed");
                        if (username == null) username = "?";
                        list.add(new TopBlocksEntry(UUID.fromString(uuidStr), username, tagStr, blocks));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro getTopBlocks: " + e.getMessage());
        }
        return list;
    }

    public static class TopBlocksEntry {
        public final UUID uuid;
        public final String username;
        public final String tagName;
        public final long blocksPlaced;

        public TopBlocksEntry(UUID uuid, String username, String tagName, long blocksPlaced) {
            this.uuid = uuid;
            this.username = username != null ? username : "?";
            this.tagName = tagName;
            this.blocksPlaced = blocksPlaced;
        }
    }
}
