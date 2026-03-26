/**
 * Savage Network - Software Proprietário
 * Copyright (c) 2026 Savage Network. - zrfadev
 * Todos os direitos reservados.
 */
package com.savagemc.builds.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Configuração do plugin SavageMC Builds.
 */
public class PluginConfig {

    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration config;

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                plugin.saveResource("config.yml", false);
            } catch (Exception e) {
                plugin.getLogger().info("Criando config.yml padrao...");
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void save() {
        if (config != null && configFile != null) {
            try {
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Erro ao salvar config: " + e.getMessage());
            }
        }
    }

    public void reload() {
        load();
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "").replace("&", "\u00A7");
    }

    public String getMessage(String path, String def) {
        String s = config.getString("messages." + path, def);
        return s != null ? s.replace("&", "\u00A7") : def;
    }

    public String getPrefix() {
        return getMessage("prefix", "&8[&2&lSavageBuilds&8] &7");
    }

    public String getKickMessage() {
        return getMessage("kick-no-permission", "&2&lSAVAGE BUILDS\n\n&7Voce nao tem permissao para entrar neste servidor.\n&7Contate um administrador.\n&2savagemc.net");
    }

    public boolean isOpenToAll() {
        return config.getBoolean("open-to-all", false);
    }

    public boolean isBypassEnabled() {
        return config.getBoolean("bypass.enabled", true);
    }

    public String getBypassPermission() {
        return config.getString("bypass.permission", "savagebuilds.bypass");
    }

    public boolean isOpBypass() {
        return config.getBoolean("bypass.op-bypass", true);
    }

    public List<String> getAllowedWorlds() {
        List<String> list = config.getStringList("allowed-worlds");
        return list != null && !list.isEmpty() ? list : null;
    }

    public boolean isWorldAllowed(String worldName) {
        List<String> allowed = getAllowedWorlds();
        return allowed == null || allowed.contains(worldName);
    }

    public boolean isScoreboardEnabled() {
        return config.getBoolean("scoreboard.enabled", true);
    }

    public String getScoreboardTitle() {
        String s = config.getString("scoreboard.title", "&2&lSAVAGE &a&lBUILDS");
        String title = s != null ? s.replace("&", "\u00A7") : "\u00A72\u00A7lSAVAGE";
        return title.length() > 32 ? title.substring(0, 32) : title;
    }

    public String getScoreboardFooter() {
        String s = config.getString("scoreboard.footer", "&2savagemc.net");
        return s != null ? s.replace("&", "\u00A7") : "\u00A72savagemc.net";
    }

    public int getScoreboardUpdateInterval() {
        return config.getInt("scoreboard.update-interval", 10);
    }

    public boolean isDatabaseEnabled() {
        return config.getBoolean("database.enabled", false);
    }


    public String getDatabaseHost() {
        return config.getString("database.host", "localhost");
    }

    public int getDatabasePort() {
        return config.getInt("database.port", 3306);
    }

    public String getDatabaseName() {
        return config.getString("database.database", "SavageMC");
    }

    public String getDatabaseUsername() {
        return config.getString("database.username", "root");
    }

    public String getDatabasePassword() {
        return config.getString("database.password", "");
    }

    public boolean isDatabaseUseSSL() {
        return config.getBoolean("database.use-ssl", false);
    }

    public int getWandItemId() {
        return config.getInt("wand-item", 271);
    }

    public String getWandItemName() {
        String s = config.getString("wand-name", "&dMachado Magico");
        return s != null ? s.replace("&", "\u00A7") : "\u00A7dMachado Magico";
    }
}
