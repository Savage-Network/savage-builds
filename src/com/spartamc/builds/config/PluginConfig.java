/**
 * Sparta Network - Software Proprietário
 * Copyright (c) 2026 Sparta Network. - zrfadev
 * Todos os direitos reservados.
 */
package com.spartamc.builds.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Configuração do plugin SpartaMC Builds.
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
        return getMessage("prefix", "&7[&6SpartaBuilds&7] ");
    }

    public String getKickMessage() {
        return getMessage("kick-no-permission", "&c&lSpartaMC Builds\n\n&7Voce nao tem permissao para entrar neste servidor.\n&7Contate um administrador.");
    }

    public boolean isBypassEnabled() {
        return config.getBoolean("bypass.enabled", true);
    }

    public String getBypassPermission() {
        return config.getString("bypass.permission", "spartabuilds.bypass");
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
        String s = config.getString("scoreboard.title", "&6&l✦ &e&lSPARTA &6&lBUILDS &6&l✦");
        return s != null ? s.replace("&", "\u00A7") : "\u00A76\u00A7l✦ \u00A7e\u00A7lSPARTA \u00A76\u00A7lBUILDS \u00A76\u00A7l✦";
    }

    public String getScoreboardFooter() {
        String s = config.getString("scoreboard.footer", "&6spartamc.com.br");
        return s != null ? s.replace("&", "\u00A7") : "\u00A76spartamc.com.br";
    }

    public int getScoreboardUpdateInterval() {
        return config.getInt("scoreboard.update-interval", 40);
    }
}
