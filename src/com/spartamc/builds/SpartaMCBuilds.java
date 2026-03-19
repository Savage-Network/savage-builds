/**
 * Sparta Network - Software Proprietário
 * Copyright (c) 2026 Sparta Network.
 * Todos os direitos reservados.
 *
 * Este software e seu código-fonte são propriedade intelectual
 * exclusiva da Sparta Network. O uso deste software é restrito
 * aos sistemas, serviços e infraestrutura autorizados pela
 * organização.
 *
 * Nenhuma parte deste software pode ser copiada, modificada,
 * distribuída, publicada, sublicenciada, vendida ou utilizada
 * para qualquer finalidade sem autorização prévia e expressa
 * da Sparta Network.
 *
 * Este código pode conter sistemas internos, mecanismos de
 * segurança e tecnologias proprietárias desenvolvidas para a
 * infraestrutura da plataforma.
 *
 * O acesso a este código é restrito a desenvolvedores e
 * colaboradores autorizados.
 *
 * Qualquer uso não autorizado poderá resultar em medidas
 * legais conforme a legislação aplicável.
 *
 * (c) 2026 Sparta Network. - zrfadev
 */
package com.spartamc.builds;

import com.spartamc.builds.config.PluginConfig;
import com.spartamc.builds.database.DatabaseManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.mrmicky.fastboard.FastBoard;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SpartaMCBuilds extends WorldEditPlugin implements Listener {

    public enum Tag {
        JOIA    ("JOIA",    "\u00A7b\u00A7l", "\u00A7b", "spartabuilds.tag.joia"),
        MASTER  ("PP",      "\u00A7d\u00A7o", "\u00A7d", "spartabuilds.tag.master"),
        ADMIN   ("ADMIN",   "\u00A74\u00A7l", "\u00A74", "spartabuilds.tag.admin"),
        STAFF   ("STAFF",   "\u00A79\u00A7l", "\u00A79", "spartabuilds.tag.staff"),
        BUILDER ("BUILDER", "\u00A72\u00A7l", "\u00A72", "spartabuilds.tag.builder");

        public final String displayName;
        public final String style;
        public final String color;
        public final String permission;

        Tag(String displayName, String style, String color, String permission) {
            this.displayName = displayName;
            this.style = style;
            this.color = color;
            this.permission = permission;
        }

        public String formatted() {
            return style + displayName + "\u00A7r";
        }

        public static Tag fromName(String name) {
            for (Tag t : values()) {
                if (t.name().equalsIgnoreCase(name) || t.displayName.equalsIgnoreCase(name)) {
                    return t;
                }
            }
            return null;
        }
    }

    private static final String PREFIX_DEFAULT = "\u00A77";

    private static final List<String> VALID_CARGOS = Arrays.asList("admin", "staff", "builder");

    private static final String[] CUSTOM_COMMANDS = {
        "bs", "gradient", "checkerboard", "stripes", "outline",
        "randomfill", "colorswap", "terraform", "heightmap",
        "measure", "palette", "circle", "sphere", "cylinder", "pyramid", "ellipsoid",
        "walls", "floor", "replace",
        "sb", "tag", "setcargo", "cargos", "sbreload"
    };

    private static final List<String> BS_SCRIPTS = Arrays.asList(
        "gradient", "checkerboard", "stripes", "outline",
        "randomfill", "colorswap", "terraform", "heightmap",
        "measure", "palette", "circle",
        "sphere", "cylinder", "pyramid", "ellipsoid",
        "walls", "floor", "replace"
    );

    private static final List<String> BS_UTILS = Arrays.asList(
        "clear", "undo", "redo", "save", "load", "list"
    );

    private static final List<String> BS_ALL_SUBS;

    static {
        List<String> all = new ArrayList<>();
        all.addAll(BS_SCRIPTS);
        all.addAll(BS_UTILS);
        BS_ALL_SUBS = Collections.unmodifiableList(all);
    }

    private static final Map<String, String> PERM_MAP = new LinkedHashMap<>();

    static {
        PERM_MAP.put("gradient", "spartabuilds.gradient");
        PERM_MAP.put("checkerboard", "spartabuilds.checkerboard");
        PERM_MAP.put("stripes", "spartabuilds.stripes");
        PERM_MAP.put("outline", "spartabuilds.outline");
        PERM_MAP.put("colorswap", "spartabuilds.colorswap");
        PERM_MAP.put("measure", "spartabuilds.measure");
        PERM_MAP.put("palette", "spartabuilds.palette");
        PERM_MAP.put("clear", "spartabuilds.clear");
        PERM_MAP.put("undo", "spartabuilds.undo");
        PERM_MAP.put("redo", "spartabuilds.redo");
        PERM_MAP.put("randomfill", "spartabuilds.randomfill");
        PERM_MAP.put("terraform", "spartabuilds.terraform");
        PERM_MAP.put("heightmap", "spartabuilds.heightmap");
        PERM_MAP.put("circle", "spartabuilds.circle");
        PERM_MAP.put("sphere", "spartabuilds.staff");
        PERM_MAP.put("cylinder", "spartabuilds.staff");
        PERM_MAP.put("walls", "spartabuilds.build");
        PERM_MAP.put("floor", "spartabuilds.build");
        PERM_MAP.put("replace", "spartabuilds.build");
        PERM_MAP.put("pyramid", "spartabuilds.staff");
        PERM_MAP.put("ellipsoid", "spartabuilds.staff");
        PERM_MAP.put("save", "spartabuilds.schematic.save");
        PERM_MAP.put("load", "spartabuilds.schematic.load");
        PERM_MAP.put("list", "spartabuilds.schematic.list");
    }

    private static final List<String> BLOCKS_BASIC = Arrays.asList(
        "1", "2", "3", "4", "5", "17", "18", "24", "35", "43",
        "44", "45", "48", "79", "80", "82", "98", "112", "121",
        "155", "159", "160", "168", "169", "170", "171", "172", "174"
    );

    private static final List<String> BLOCKS_WOOL = Arrays.asList(
        "35:0", "35:1", "35:2", "35:3", "35:4", "35:5", "35:6", "35:7",
        "35:8", "35:9", "35:10", "35:11", "35:12", "35:13", "35:14", "35:15"
    );

    private static final List<String> BLOCKS_CLAY = Arrays.asList(
        "159:0", "159:1", "159:2", "159:3", "159:4", "159:5", "159:6", "159:7",
        "159:8", "159:9", "159:10", "159:11", "159:12", "159:13", "159:14", "159:15"
    );

    private static final List<String> BLOCKS_GLASS = Arrays.asList(
        "95:0", "95:1", "95:2", "95:3", "95:4", "95:5", "95:6", "95:7",
        "95:8", "95:9", "95:10", "95:11", "95:12", "95:13", "95:14", "95:15"
    );

    private static final List<String> ALL_BLOCKS;

    static {
        List<String> all = new ArrayList<>();
        all.addAll(BLOCKS_BASIC);
        all.addAll(BLOCKS_WOOL);
        all.addAll(BLOCKS_CLAY);
        all.addAll(BLOCKS_GLASS);
        ALL_BLOCKS = Collections.unmodifiableList(all);
    }

    private static final List<String> COLORS = Arrays.asList(
        "0", "1", "2", "3", "4", "5", "6", "7",
        "8", "9", "10", "11", "12", "13", "14", "15"
    );

    private static final List<String> GRADIENT_DIRS = Arrays.asList("v", "h", "d");
    private static final List<String> AXES = Arrays.asList("x", "y", "z");
    private static final List<String> HEIGHTMAP_MODES = Arrays.asList("raise", "lower", "smooth");
    private static final List<String> PALETTE_TYPES = Arrays.asList("wool", "clay", "glass");
    private static final List<String> BOOL_OPTIONS = Arrays.asList("true", "false");
    private static final List<String> SMALL_NUMBERS = Arrays.asList("1", "2", "3", "4", "5", "6", "8", "10");
    private static final List<String> RADIUS_NUMBERS = Arrays.asList("5", "10", "15", "20", "30", "50");
    private static final List<String> WEIGHT_NUMBERS = Arrays.asList("10", "20", "25", "30", "40", "50", "60", "70", "80", "90");
    private static final List<String> INTENSITY_NUMBERS = Arrays.asList("3", "5", "8", "10", "15", "20");
    private static final List<String> UNDO_COUNTS = Arrays.asList("1", "2", "3", "5", "10");

    private final Map<UUID, Tag> playerTags = new HashMap<>();
    private final Map<UUID, String> playerCargos = new HashMap<>();
    private final Map<String, String> cargosPendingByName = new HashMap<>();
    private final Map<UUID, PermissionAttachment> cargoAttachments = new HashMap<>();
    private final Map<UUID, Long> sessionStartTime = new HashMap<>();
    private final Map<UUID, Long> totalPlayTime = new HashMap<>();

    private File tagsFile;
    private File cargosFile;
    private File playtimeFile;
    private Scoreboard scoreboard;
    private final Map<UUID, FastBoard> fastBoards = new HashMap<>();
    private PluginConfig pluginConfig;
    private DatabaseManager database;
    private int scoreboardTaskId = -1;

    @Override
    public void onEnable() {
        getLogger().info("===================================");
        getLogger().info("Desenvolvido por: zrfadev");
        getLogger().info("Exclusivamente para (c) SpartaMC");
        getLogger().info("https://spartamc.com.br");
        getLogger().info("Copyright (c) 2026 Sparta Network.");
        getLogger().info("O uso deste plugin é restrito aos sistemas, serviços e infraestrutura autorizados pela organização.");
        getLogger().info("===================================");
        super.onEnable();

        getDataFolder().mkdirs();
        pluginConfig = new PluginConfig(this);
        pluginConfig.load();

        if (pluginConfig.isDatabaseEnabled()) {
            database = new DatabaseManager(this,
                    pluginConfig.getDatabaseHost(),
                    pluginConfig.getDatabasePort(),
                    pluginConfig.getDatabaseName(),
                    pluginConfig.getDatabaseUsername(),
                    pluginConfig.getDatabasePassword(),
                    pluginConfig.isDatabaseUseSSL());
            if (!database.connect()) {
                getLogger().warning("MySQL falhou. Usando arquivos YAML.");
                database = null;
            }
        }
        loadTags();
        loadCargos();
        loadPlaytime();

        for (String cmd : CUSTOM_COMMANDS) {
            if (getCommand(cmd) != null) {
                getCommand(cmd).setExecutor(this);
                getCommand(cmd).setTabCompleter(this);
            }
        }

        getServer().getPluginManager().registerEvents(this, this);

        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        setupTeams();

        for (Player p : Bukkit.getOnlinePlayers()) {
            applyCargo(p);
            applyPrefix(p);
            if (pluginConfig.isScoreboardEnabled()) applyScoreboard(p);
            sessionStartTime.put(p.getUniqueId(), System.currentTimeMillis());
        }

        if (pluginConfig.isScoreboardEnabled()) {
            int interval = Math.max(10, pluginConfig.getScoreboardUpdateInterval());
            scoreboardTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.isOnline() && pluginConfig.isScoreboardEnabled()) {
                            updateScoreboard(p);
                        }
                    }
                }
            }, 40L, interval);
        }

        getLogger().info("SpartaMC Builds carregado!");
        getLogger().info("Cargos: spartabuilds.admin | spartabuilds.staff | spartabuilds.build");
        getLogger().info("Tags disponiveis: " + Tag.values().length);
    }

    @Override
    public void onDisable() {
        if (scoreboardTaskId != -1) {
            Bukkit.getScheduler().cancelTask(scoreboardTaskId);
        }
        for (FastBoard board : fastBoards.values()) {
            if (board != null && !board.isDeleted()) board.delete();
        }
        fastBoards.clear();
        if (database == null) {
            saveTags();
            saveCargos();
            savePlaytime();
        }
        if (database != null) database.disconnect();
        super.onDisable();
    }

    private void loadTags() {
        if (database != null) {
            Map<UUID, String> data = database.loadAllTags();
            for (Map.Entry<UUID, String> e : data.entrySet()) {
                String tagName = e.getValue();
                if ("LIRU".equalsIgnoreCase(tagName)) tagName = "MASTER";
                if ("FP".equalsIgnoreCase(tagName)) tagName = "JOIA";
                Tag tag = Tag.fromName(tagName);
                if (tag != null) playerTags.put(e.getKey(), tag);
            }
            return;
        }
        tagsFile = new File(getDataFolder(), "tags.yml");
        if (!tagsFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(tagsFile);
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String tagName = cfg.getString(key);
                if ("LIRU".equalsIgnoreCase(tagName)) tagName = "MASTER";
                if ("FP".equalsIgnoreCase(tagName)) tagName = "JOIA";
                Tag tag = Tag.fromName(tagName);
                if (tag != null) playerTags.put(uuid, tag);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private void saveTags() {
        if (database != null) return;
        if (tagsFile == null) return;
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Tag> entry : playerTags.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue().name());
        }
        try { cfg.save(tagsFile); }
        catch (IOException e) { getLogger().warning("Erro ao salvar tags: " + e.getMessage()); }
    }

    private void loadCargos() {
        if (database != null) {
            playerCargos.putAll(database.loadAllCargos());
            cargosPendingByName.putAll(database.loadPendingCargos());
            return;
        }
        cargosFile = new File(getDataFolder(), "cargos.yml");
        if (!cargosFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cargosFile);
        if (cfg.contains("pending") && cfg.getConfigurationSection("pending") != null) {
            for (String name : cfg.getConfigurationSection("pending").getKeys(false)) {
                String cargo = cfg.getString("pending." + name);
                if (cargo != null && VALID_CARGOS.contains(cargo.toLowerCase())) {
                    cargosPendingByName.put(name.toLowerCase(), cargo.toLowerCase());
                }
            }
        }
        for (String key : cfg.getKeys(false)) {
            if (key.equals("pending")) continue;
            try {
                UUID uuid = UUID.fromString(key);
                String cargo = cfg.getString(key);
                if (cargo != null && VALID_CARGOS.contains(cargo.toLowerCase())) {
                    playerCargos.put(uuid, cargo.toLowerCase());
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private UUID findUUIDByName(String name) {
        for (UUID uuid : playerCargos.keySet()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            if (op.getName() != null && op.getName().equalsIgnoreCase(name)) return uuid;
        }
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        return (op.getUniqueId() != null && !op.getUniqueId().toString().equals("00000000-0000-0000-0000-000000000000")) ? op.getUniqueId() : null;
    }

    private void loadPlaytime() {
        if (database != null) {
            totalPlayTime.putAll(database.loadAllPlaytime());
            return;
        }
        playtimeFile = new File(getDataFolder(), "playtime.yml");
        if (!playtimeFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(playtimeFile);
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                long seconds = cfg.getLong(key, 0L);
                if (seconds > 0) totalPlayTime.put(uuid, seconds);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private void savePlaytime() {
        if (database != null) return;
        if (playtimeFile == null) return;
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : totalPlayTime.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try { cfg.save(playtimeFile); }
        catch (IOException e) { getLogger().warning("Erro ao salvar playtime: " + e.getMessage()); }
    }

    private void saveCargos() {
        if (database != null) return;
        if (cargosFile == null) return;
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : playerCargos.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : cargosPendingByName.entrySet()) {
            cfg.set("pending." + entry.getKey(), entry.getValue());
        }
        try { cfg.save(cargosFile); }
        catch (IOException e) { getLogger().warning("Erro ao salvar cargos: " + e.getMessage()); }
    }

    private Tag getPlayerTag(Player player) {
        return playerTags.get(player.getUniqueId());
    }

    private void applyCargo(Player player) {
        PermissionAttachment old = cargoAttachments.remove(player.getUniqueId());
        if (old != null) player.removeAttachment(old);

        String cargo = playerCargos.get(player.getUniqueId());
        if (cargo == null) return;

        PermissionAttachment attach = player.addAttachment(this);
        switch (cargo) {
            case "admin":   attach.setPermission("spartabuilds.admin", true); break;
            case "staff":   attach.setPermission("spartabuilds.staff", true); break;
            case "builder": attach.setPermission("spartabuilds.build", true); break;
        }
        cargoAttachments.put(player.getUniqueId(), attach);
    }

    private void removeCargo(Player player) {
        PermissionAttachment old = cargoAttachments.remove(player.getUniqueId());
        if (old != null) player.removeAttachment(old);
        playerCargos.remove(player.getUniqueId());
        if (database != null) database.setCargo(player.getUniqueId(), player.getName(), null);
    }

    private void setupTeams() {
        createTeam("sb_admin",   "\u00A74\u00A7lADMIN \u00A7r\u00A74", "\u00A7r");
        createTeam("sb_staff",   "\u00A79\u00A7lSTAFF \u00A7r\u00A79", "\u00A7r");
        createTeam("sb_builder", "\u00A72\u00A7lBUILDER \u00A7r\u00A72", "\u00A7r");
        createTeam("sb_master",  "\u00A7d\u00A7o", "\u00A7r");
        createTeam("sb_joia",    "\u00A7b\u00A7lJoia \u00A7r\u00A7b", "\u00A7r");
        createTeam("sb_default", "\u00A77", "\u00A7r");
    }

    private void createTeam(String name, String prefix, String suffix) {
        Team team = scoreboard.getTeam(name);
        if (team == null) team = scoreboard.registerNewTeam(name);
        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }

    private String getPlayerRole(Player player) {
        if (player.hasPermission("spartabuilds.admin")) return "admin";
        if (player.hasPermission("spartabuilds.staff")) return "staff";
        if (player.hasPermission("spartabuilds.build")) return "builder";
        return "default";
    }

    private String getTeamName(Player player) {
        Tag tag = getPlayerTag(player);
        if (tag == null) return "sb_default";
        switch (tag) {
            case ADMIN:   return "sb_admin";
            case STAFF:   return "sb_staff";
            case BUILDER: return "sb_builder";
            case MASTER:  return "sb_master";
            case JOIA:    return "sb_joia";
            default:      return "sb_default";
        }
    }

    private String buildFullDisplay(Player player) {
        Tag tag = getPlayerTag(player);
        if (tag != null) {
            if (tag == Tag.MASTER) return tag.style + player.getName() + "\u00A7r";
            return tag.style + tag.displayName + " " + tag.color + player.getName() + "\u00A7r";
        }
        return PREFIX_DEFAULT + player.getName() + "\u00A7r";
    }

    private void applyPrefix(Player player) {
        if (pluginConfig.isScoreboardEnabled()) {
            applyScoreboard(player);
        } else {
            FastBoard fb = fastBoards.remove(player.getUniqueId());
            if (fb != null && !fb.isDeleted()) fb.delete();
            if (player.getScoreboard() != scoreboard) {
                player.setScoreboard(scoreboard);
            }
            for (Team t : scoreboard.getTeams()) {
                if (t.getName().startsWith("sb_")) t.removeEntry(player.getName());
            }
            Team team = scoreboard.getTeam(getTeamName(player));
            if (team != null) team.addEntry(player.getName());
        }

        String display = buildFullDisplay(player);
        player.setDisplayName(display);
        player.setPlayerListName(display);
    }

    private void setupTeamOnBoard(Scoreboard board, Player player) {
        for (Team t : board.getTeams()) {
            if (t.getName().startsWith("sb_")) t.removeEntry(player.getName());
        }
        String teamName = getTeamName(player);
        Team team = board.getTeam(teamName);
        if (team == null) {
            team = board.registerNewTeam(teamName);
            switch (teamName) {
                case "sb_admin":   team.setPrefix("\u00A74\u00A7lADMIN \u00A7r\u00A74"); break;
                case "sb_staff":   team.setPrefix("\u00A79\u00A7lSTAFF \u00A7r\u00A79"); break;
                case "sb_builder": team.setPrefix("\u00A72\u00A7lBUILDER \u00A7r\u00A72"); break;
                case "sb_master":  team.setPrefix("\u00A7d\u00A7o"); break;
                case "sb_joia":    team.setPrefix("\u00A7b\u00A7lJoia \u00A7r\u00A7b"); break;
                default:           team.setPrefix("\u00A77"); break;
            }
            team.setSuffix("\u00A7r");
        }
        team.addEntry(player.getName());
    }

    private void applyScoreboard(Player player) {
        FastBoard old = fastBoards.remove(player.getUniqueId());
        if (old != null && !old.isDeleted()) old.delete();

        player.setScoreboard(scoreboard);
        setupTeamOnBoard(scoreboard, player);

        FastBoard board = new FastBoard(player);
        board.updateTitle(pluginConfig.getScoreboardTitle());
        fastBoards.put(player.getUniqueId(), board);
        updateScoreboard(player);
    }

    private void updateScoreboard(Player player) {
        FastBoard board = fastBoards.get(player.getUniqueId());
        if (board == null || board.isDeleted()) {
            applyScoreboard(player);
            return;
        }

        Tag tag = getPlayerTag(player);
        String tagStr = tag != null ? tag.displayName : "MEMBRO";
        String world = player.getWorld().getName();
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        int online = Bukkit.getOnlinePlayers().size();

        long sessionSec = 0;
        Long start = sessionStartTime.get(player.getUniqueId());
        if (start != null) {
            sessionSec = (System.currentTimeMillis() - start) / 1000;
        }
        long totalSec = totalPlayTime.getOrDefault(player.getUniqueId(), 0L) + sessionSec;

        String lb = "\u00A77";
        String tagColored = tag != null ? (tag.style + tagStr + "\u00A7r") : (lb + tagStr);
        String mundoVal = "\u00A7b" + truncate(world, 14);
        String coordsVal = "\u00A7b" + x + ", " + y + ", " + z;
        String sessaoVal = "\u00A7a" + formatTime(sessionSec);
        String totalVal = "\u00A7a" + formatTime(totalSec);
        String onlineVal = "\u00A7e" + online;

        board.updateLines(
            "\u00A78 ",
            lb + "Tag: " + tagColored,
            "\u00A78  ",
            lb + "Mundo: " + mundoVal,
            lb + "Coords: " + coordsVal,
            "\u00A78   ",
            lb + "Sessao: " + sessaoVal,
            lb + "Total: " + totalVal,
            lb + "Online: " + onlineVal,
            "\u00A78    ",
            pluginConfig.getScoreboardFooter()
        );
    }

    private String formatTime(long seconds) {
        if (seconds < 60) return seconds + "s";
        long min = seconds / 60;
        long sec = seconds % 60;
        if (min < 60) return min + "m" + (sec > 0 ? " " + sec + "s" : "");
        long h = min / 60;
        min = min % 60;
        if (h < 24) return h + "h" + (min > 0 ? " " + min + "m" : "");
        long d = h / 24;
        h = h % 24;
        return d + "d" + (h > 0 ? " " + h + "h" : "") + (min > 0 ? " " + min + "m" : "");
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) : s;
    }

    private String getSelectionInfo(Player player) {
        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                String pending = cargosPendingByName.remove(player.getName().toLowerCase());
                if (pending != null) {
                    playerCargos.put(player.getUniqueId(), pending);
                    if (database != null) database.migratePendingToPlayer(player.getName(), player.getUniqueId());
                    else saveCargos();
                }
                applyCargo(player);
                applyPrefix(player);
                if (pluginConfig.isScoreboardEnabled()) applyScoreboard(player);
                sessionStartTime.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }, 5L);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (pluginConfig.isScoreboardEnabled()) {
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    applyScoreboard(event.getPlayer());
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        FastBoard fb = fastBoards.remove(uuid);
        if (fb != null && !fb.isDeleted()) fb.delete();
        Long start = sessionStartTime.remove(uuid);
        if (start != null) {
            long sessionSec = (System.currentTimeMillis() - start) / 1000;
            long total = totalPlayTime.getOrDefault(uuid, 0L) + sessionSec;
            totalPlayTime.put(uuid, total);
            if (database != null) database.setPlaytime(uuid, player.getName(), total);
            else savePlaytime();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;

        Player player = event.getPlayer();

        if (pluginConfig.isBypassEnabled()) {
            if (pluginConfig.isOpBypass() && player.isOp()) return;
            if (player.hasPermission(pluginConfig.getBypassPermission())) return;
        }
        if (player.hasPermission("spartabuilds.admin")) return;
        if (player.hasPermission("spartabuilds.staff")) return;
        if (player.hasPermission("spartabuilds.build")) return;
        if (playerCargos.containsKey(player.getUniqueId())) return;
        if (cargosPendingByName.containsKey(player.getName().toLowerCase())) return;

        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, pluginConfig.getKickMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Tag tag = getPlayerTag(player);

        if (tag != null) {
            if (tag == Tag.MASTER) {
                event.setFormat(tag.style + player.getName() + "\u00A7r\u00A77: \u00A7f%2$s");
            } else {
                event.setFormat(tag.style + tag.displayName + " " + tag.color + player.getName() + "\u00A7r\u00A77: \u00A7f%2$s");
            }
        } else {
            event.setFormat(PREFIX_DEFAULT + player.getName() + "\u00A7r\u00A77: \u00A7f%2$s");
        }
    }

    private boolean handleTag(Player player, String[] args) {
        if (args.length == 0) {
            sendTagList(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("membro")) {
            playerTags.remove(player.getUniqueId());
            if (database != null) database.setTag(player.getUniqueId(), player.getName(), null);
            else saveTags();
            applyPrefix(player);
            player.sendMessage(pluginConfig.getMessage("tag-membro", "\u00A7aTag alterada para: \u00A77MEMBRO"));
            return true;
        }

        Tag tag = Tag.fromName(sub);
        if (tag == null) {
            sendTagList(player);
            return true;
        }

        if (!player.hasPermission(tag.permission) && !player.hasPermission("spartabuilds.admin")) {
            player.sendMessage(pluginConfig.getMessage("no-permission-tag", "\u00A7cVoce nao tem permissao para usar esta tag."));
            return true;
        }

        playerTags.put(player.getUniqueId(), tag);
        if (database != null) database.setTag(player.getUniqueId(), player.getName(), tag.name());
        else saveTags();
        applyPrefix(player);
        player.sendMessage(pluginConfig.getMessage("tag-changed", "\u00A7aTag alterada para: %tag%").replace("%tag%", tag.formatted()));
        return true;
    }

    private boolean handleTagSet(CommandSender sender, String[] args) {
        if (!sender.hasPermission("spartabuilds.admin")) {
            sender.sendMessage(pluginConfig.getMessage("no-permission", "\u00A7cVoce nao tem permissao para usar este comando."));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("\u00A7cUso: /tag set <nick> <tag|membro>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(pluginConfig.getMessage("player-not-online", "\u00A7cJogador nao esta online.").replace("%player%", args[0]));
            return true;
        }

        String tagArg = args[1].toLowerCase();
        if (tagArg.equals("membro")) {
            playerTags.remove(target.getUniqueId());
            if (database != null) database.setTag(target.getUniqueId(), target.getName(), null);
            else saveTags();
            applyPrefix(target);
            sender.sendMessage("\u00A7aTag de \u00A7e" + target.getName() + " \u00A7aremovida (MEMBRO).");
            target.sendMessage(pluginConfig.getMessage("tag-membro", "\u00A7aTag alterada para: \u00A77MEMBRO"));
            return true;
        }

        Tag tag = Tag.fromName(tagArg);
        if (tag == null) {
            sender.sendMessage("\u00A7cTag invalida. Use: membro, builder, staff, admin, joia, pp");
            return true;
        }

        playerTags.put(target.getUniqueId(), tag);
        if (database != null) database.setTag(target.getUniqueId(), target.getName(), tag.name());
        else saveTags();
        applyPrefix(target);
        sender.sendMessage("\u00A7aTag de \u00A7e" + target.getName() + " \u00A7adefinida para: " + tag.formatted());
        target.sendMessage(pluginConfig.getMessage("tag-changed", "\u00A7aTag alterada para: %tag%").replace("%tag%", tag.formatted()));
        return true;
    }

    private boolean handleTagLista(CommandSender sender) {
        if (!sender.hasPermission("spartabuilds.staff")) {
            sender.sendMessage(pluginConfig.getMessage("no-permission", "\u00A7cVoce nao tem permissao para usar este comando."));
            return true;
        }

        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        sender.sendMessage("\u00A76\u00A7l\u2726 \u00A7e\u00A7lJogadores com Tag \u00A76\u00A7l\u2726");
        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        int count = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            Tag tag = getPlayerTag(p);
            if (tag != null) {
                sender.sendMessage("\u00A78  \u00A77" + tag.formatted() + " \u00A78\u00BB \u00A7f" + p.getName());
                count++;
            }
        }
        if (count == 0) sender.sendMessage("\u00A78  \u00A77Nenhum jogador com tag online.");
        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        return true;
    }

    private void sendTagList(Player player) {
        Tag current = getPlayerTag(player);

        StringBuilder json = new StringBuilder();
        json.append("[");
        json.append("{\"text\":\"Suas Tags: \",\"color\":\"gold\",\"bold\":true}");

        boolean membroActive = (current == null);
        if (membroActive) {
            json.append(",{\"text\":\"MEMBRO\",\"color\":\"gray\",\"bold\":true,");
            json.append("\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"\\u00A7aTag ativa!\"}}");
        } else {
            json.append(",{\"text\":\"MEMBRO\",\"color\":\"gray\",\"bold\":false,");
            json.append("\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tag membro\"},");
            json.append("\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"\\u00A7eClique para selecionar\"}}");
        }

        Tag[] tags = Tag.values();
        for (int i = tags.length - 1; i >= 0; i--) {
            Tag tag = tags[i];
            boolean has = player.hasPermission(tag.permission) || player.hasPermission("spartabuilds.admin");
            if (!has) continue;

            json.append(",{\"text\":\" | \",\"color\":\"dark_gray\",\"bold\":false}");

            boolean active = (current == tag);

            if (active) {
                json.append(",{\"text\":\"").append(tag.displayName).append("\",");
                json.append(tagJsonStyle(tag));
                json.append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"\\u00A7aTag ativa!\"}}");
            } else {
                json.append(",{\"text\":\"").append(tag.displayName).append("\",");
                json.append(tagJsonStyle(tag));
                json.append(",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tag ").append(tag.name().toLowerCase()).append("\"},");
                json.append("\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"\\u00A7eClique para selecionar\"}}");
            }
        }

        json.append("]");

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "tellraw " + player.getName() + " " + json.toString());
    }

    private String tagJsonStyle(Tag tag) {
        switch (tag) {
            case JOIA:    return "\"color\":\"aqua\",\"bold\":true";
            case MASTER:  return "\"color\":\"light_purple\",\"italic\":true,\"bold\":false";
            case ADMIN:   return "\"color\":\"dark_red\",\"bold\":true";
            case STAFF:   return "\"color\":\"blue\",\"bold\":true";
            case BUILDER: return "\"color\":\"dark_green\",\"bold\":true";
            default:      return "\"color\":\"gray\",\"bold\":false";
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("spartabuilds.admin")) {
            sender.sendMessage(pluginConfig.getMessage("no-permission", "\u00A7cVoce nao tem permissao para usar este comando."));
            return true;
        }
        pluginConfig.reload();
        if (scoreboardTaskId != -1) {
            Bukkit.getScheduler().cancelTask(scoreboardTaskId);
            scoreboardTaskId = -1;
        }
        if (pluginConfig.isScoreboardEnabled()) {
            int interval = Math.max(10, pluginConfig.getScoreboardUpdateInterval());
            scoreboardTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.isOnline() && pluginConfig.isScoreboardEnabled()) updateScoreboard(p);
                    }
                }
            }, interval, interval);
            for (Player p : Bukkit.getOnlinePlayers()) {
                applyPrefix(p);
                if (pluginConfig.isScoreboardEnabled()) applyScoreboard(p);
            }
        }
        sender.sendMessage(pluginConfig.getMessage("reload-success", "\u00A7aConfiguracao recarregada com sucesso!"));
        return true;
    }

    private boolean handleSetCargo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("\u00A7cUso: /setcargo <nick> <admin|staff|builder|none>");
            return true;
        }

        boolean isAdmin = sender.hasPermission("spartabuilds.admin");
        boolean isStaff = sender.hasPermission("spartabuilds.staff");

        if (!isAdmin && !isStaff) {
            sender.sendMessage(pluginConfig.getMessage("no-permission", "\u00A7cVoce nao tem permissao para definir cargos."));
            return true;
        }

        String targetName = args[0];
        String cargo = args[1].toLowerCase();
        Player target = Bukkit.getPlayer(targetName);

        if (cargo.equals("none") || cargo.equals("remover") || cargo.equals("nenhum")) {
            if (target != null) {
                removeCargo(target);
                applyPrefix(target);
                sender.sendMessage(pluginConfig.getMessage("cargo-removed", "\u00A7aCargo removido.").replace("%player%", target.getName()));
                target.sendMessage(pluginConfig.getMessage("cargo-removed-target", "\u00A7eSeu cargo foi removido.").replace("%sender%", sender.getName()));
            } else {
                UUID found = findUUIDByName(targetName);
                boolean removed = false;
                if (found != null) {
                    playerCargos.remove(found);
                    if (database != null) database.setCargo(found, targetName, null);
                    removed = true;
                }
                if (cargosPendingByName.remove(targetName.toLowerCase()) != null) {
                    if (database != null) database.removePendingCargo(targetName);
                    removed = true;
                }
                if (removed) {
                    if (database == null) saveCargos();
                    sender.sendMessage(pluginConfig.getMessage("cargo-removed", "\u00A7aCargo removido.").replace("%player%", targetName));
                } else {
                    sender.sendMessage(pluginConfig.getMessage("player-not-online", "\u00A7cJogador nao encontrado.").replace("%player%", targetName));
                }
            }
            return true;
        }

        if (!VALID_CARGOS.contains(cargo)) {
            sender.sendMessage(pluginConfig.getMessage("invalid-cargo", "\u00A7cCargo invalido."));
            return true;
        }

        if (isStaff && !isAdmin) {
            if (cargo.equals("admin") || cargo.equals("staff")) {
                sender.sendMessage(pluginConfig.getMessage("staff-cannot-set", "\u00A7cApenas Admins podem definir Admin ou Staff."));
                sender.sendMessage(pluginConfig.getMessage("staff-can-builder", "\u00A77Staff pode promover a Builder."));
                return true;
            }
        }

        if (target != null) {
            playerCargos.put(target.getUniqueId(), cargo);
            if (database != null) database.setCargo(target.getUniqueId(), target.getName(), cargo);
            applyCargo(target);
            applyPrefix(target);
        } else {
            UUID uuid = findUUIDByName(targetName);
            if (uuid != null && !uuid.toString().equals("00000000-0000-0000-0000-000000000000")) {
                playerCargos.put(uuid, cargo);
                if (database != null) database.setCargo(uuid, targetName, cargo);
                sender.sendMessage(pluginConfig.getMessage("offline-cargo-set", "\u00A7aCargo sera aplicado quando entrar.").replace("%player%", targetName));
            } else {
                cargosPendingByName.put(targetName.toLowerCase(), cargo);
                if (database != null) database.setPendingCargo(targetName, cargo);
                sender.sendMessage(pluginConfig.getMessage("offline-cargo-set", "\u00A7aCargo sera aplicado quando entrar.").replace("%player%", targetName));
            }
        }

        if (database == null) saveCargos();

        String cargoDisplay;
        switch (cargo) {
            case "admin":   cargoDisplay = "\u00A74\u00A7lADMIN"; break;
            case "staff":   cargoDisplay = "\u00A79\u00A7lSTAFF"; break;
            case "builder": cargoDisplay = "\u00A72\u00A7lBUILDER"; break;
            default:        cargoDisplay = cargo; break;
        }

        sender.sendMessage(pluginConfig.getMessage("cargo-set", "\u00A7aCargo definido.").replace("%player%", targetName).replace("%cargo%", cargoDisplay));
        if (target != null) {
            target.sendMessage(pluginConfig.getMessage("cargo-changed-target", "\u00A7eSeu cargo foi alterado.").replace("%cargo%", cargoDisplay).replace("%sender%", sender.getName()));
        }
        return true;
    }

    private boolean handleCargos(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return handleMeuCargo(sender);
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("list") || sub.equals("lista")) {
            if (!sender.hasPermission("spartabuilds.staff")) {
                sender.sendMessage("\u00A7cApenas Staff ou Admin podem listar cargos.");
                return true;
            }
            return handleCargosList(sender);
        }

        if (sub.equals("info")) {
            if (!sender.hasPermission("spartabuilds.staff")) {
                sender.sendMessage("\u00A7cApenas Staff ou Admin podem ver info de cargos.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("\u00A7cUso: /cargos info <nick>");
                return true;
            }
            return handleCargosInfo(sender, args[1]);
        }

        if (sub.equals("set") || sub.equals("definir")) {
            String[] setArgs = Arrays.copyOfRange(args, 1, args.length);
            return handleSetCargo(sender, setArgs);
        }

        sender.sendMessage("\u00A7cSubcomando desconhecido. Use: \u00A7e/cargos\u00A7c, \u00A7e/cargos list\u00A7c, \u00A7e/cargos info <nick>");
        if (sender.hasPermission("spartabuilds.admin")) {
            sender.sendMessage("\u00A7cOu \u00A7e/cargos set <nick> <cargo>\u00A7c para definir cargo.");
        }
        return true;
    }

    private boolean handleMeuCargo(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00A7cApenas jogadores podem usar este comando.");
            return true;
        }
        Player p = (Player) sender;
        String role = getPlayerRole(p);
        Tag tag = getPlayerTag(p);

        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        sender.sendMessage("\u00A76\u00A7l\u2726 \u00A7e\u00A7lMeu Cargo \u00A76\u00A7l\u2726");
        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        sender.sendMessage("\u00A77 Cargo \u00A78\u00BB \u00A7f" + role.toUpperCase());
        sender.sendMessage("\u00A77 Tag \u00A78\u00BB \u00A7f" + (tag != null ? tag.formatted() : "\u00A77nenhuma"));
        sender.sendMessage("");

        switch (role) {
            case "admin":
                sender.sendMessage("\u00A74\u00A7lADMIN \u00A78\u00BB \u00A7fAcesso total");
                sender.sendMessage("\u00A78  \u00A77Todos os comandos, schematics, setcargo");
                break;
            case "staff":
                sender.sendMessage("\u00A79\u00A7lSTAFF \u00A78\u00BB \u00A7fFerramentas avancadas");
                sender.sendMessage("\u00A78  \u00A77randomfill, terraform, heightmap, sphere");
                sender.sendMessage("\u00A78  \u00A77Pode promover a \u00A72Builder");
                break;
            case "builder":
                sender.sendMessage("\u00A72\u00A7lBUILDER \u00A78\u00BB \u00A7fFerramentas basicas");
                sender.sendMessage("\u00A78  \u00A77gradient, checkerboard, outline, undo/redo");
                break;
            default:
                sender.sendMessage("\u00A77Sem cargo - contate um admin.");
                break;
        }
        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        return true;
    }

    private boolean handleCargosList(CommandSender sender) {
        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        sender.sendMessage("\u00A76\u00A7l\u2726 \u00A7e\u00A7lJogadores com Cargo \u00A76\u00A7l\u2726");
        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");

        int count = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            String cargo = playerCargos.get(p.getUniqueId());
            if (cargo != null) {
                String cargoColor = cargo.equals("admin") ? "\u00A74" : cargo.equals("staff") ? "\u00A79" : "\u00A72";
                sender.sendMessage("\u00A78  \u00A77" + cargoColor + p.getName() + "\u00A7r \u00A78\u00BB \u00A7f" + cargo.toUpperCase());
                count++;
            } else if (p.hasPermission("spartabuilds.admin") || p.isOp()) {
                sender.sendMessage("\u00A78  \u00A77\u00A74" + p.getName() + "\u00A7r \u00A78\u00BB \u00A7fADMIN");
                count++;
            }
        }

        if (count == 0) {
            sender.sendMessage("\u00A78  \u00A77Nenhum jogador com cargo online.");
        }
        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        return true;
    }

    private boolean handleCargosInfo(CommandSender sender, String nick) {
        Player target = Bukkit.getPlayer(nick);
        if (target == null) {
            sender.sendMessage("\u00A7cJogador \u00A7e" + nick + "\u00A7c nao esta online.");
            return true;
        }

        String cargo = playerCargos.get(target.getUniqueId());
        Tag tag = getPlayerTag(target);

        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        sender.sendMessage("\u00A76\u00A7l\u2726 \u00A7e\u00A7lCargo de " + target.getName() + " \u00A76\u00A7l\u2726");
        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        sender.sendMessage("\u00A77 Cargo \u00A78\u00BB \u00A7f" + (cargo != null ? cargo.toUpperCase() : (target.isOp() ? "ADMIN" : "nenhum")));
        sender.sendMessage("\u00A77 Tag \u00A78\u00BB \u00A7f" + (tag != null ? tag.formatted() : "nenhuma"));
        sender.sendMessage("\u00A78\u00A7m─────────────────────\u00A7r");
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();

        boolean isCustom = false;
        for (String c : CUSTOM_COMMANDS) {
            if (c.equals(cmd)) { isCustom = true; break; }
        }

        if (!isCustom) return super.onCommand(sender, command, label, args);

        if (cmd.equals("sb")) {
            sendHelp(sender);
            if (sender instanceof Player && pluginConfig.isScoreboardEnabled()) {
                applyScoreboard((Player) sender);
            }
            return true;
        }
        if (cmd.equals("sbreload")) return handleReload(sender);
        if (cmd.equals("setcargo")) return handleSetCargo(sender, args);
        if (cmd.equals("cargos") || cmd.equals("meucargo")) return handleCargos(sender, args);

        if (cmd.equals("tag") && args.length > 0) {
            String sub = args[0].toLowerCase();
            if (sub.equals("set")) return handleTagSet(sender, Arrays.copyOfRange(args, 1, args.length));
            if (sub.equals("lista") || sub.equals("list")) return handleTagLista(sender);
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00A7cApenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.equals("tag")) return handleTag(player, args);
        if (cmd.equals("bs")) return handleBs(player, args);

        return handleDirect(player, cmd, args);
    }

    private boolean handleBs(Player player, String[] args) {
        if (args.length < 1) { sendHelp(player); return true; }

        String sub = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        if (!checkPermission(player, sub)) return true;

        if (BS_SCRIPTS.contains(sub)) {
            dispatchScript(player, sub, subArgs);
        } else {
            switch (sub) {
                case "clear":
                    player.performCommand("/set 0");
                    break;
                case "undo":
                    player.performCommand("/undo " + (subArgs.length > 0 ? subArgs[0] : "1"));
                    break;
                case "redo":
                    player.performCommand("/redo " + (subArgs.length > 0 ? subArgs[0] : "1"));
                    break;
                case "save":
                    if (subArgs.length < 1) { player.sendMessage("\u00A7cUso: /bs save <nome>"); return true; }
                    player.performCommand("/schematic save " + subArgs[0]);
                    break;
                case "load":
                    if (subArgs.length < 1) { player.sendMessage("\u00A7cUso: /bs load <nome>"); return true; }
                    player.performCommand("/schematic load " + subArgs[0]);
                    break;
                case "list":
                    player.performCommand("/schematic list");
                    break;
                default:
                    player.sendMessage("\u00A7cSubcomando desconhecido. Use \u00A7e/sb \u00A7cpara ver os comandos.");
                    break;
            }
        }
        return true;
    }

    private boolean handleDirect(Player player, String cmd, String[] args) {
        if (!checkPermission(player, cmd)) return true;
        dispatchScript(player, cmd, args);
        return true;
    }

    private void dispatchScript(Player player, String scriptName, String[] args) {
        StringBuilder csCmd = new StringBuilder("cs " + scriptName);
        for (String arg : args) csCmd.append(" ").append(arg);
        player.performCommand(csCmd.toString());
    }

    private boolean checkPermission(Player player, String subCommand) {
        if (player.hasPermission("spartabuilds.admin")) return true;

        String perm = PERM_MAP.get(subCommand);
        if (perm != null && !player.hasPermission(perm)) {
            player.sendMessage("\u00A7c\u00A7lSpartaMC \u00A7cVoce nao tem permissao para usar este comando.");
            player.sendMessage("\u00A77Permissao: \u00A7e" + perm + " \u00A77| Cargo minimo: " + getRequiredRole(subCommand));
            return false;
        }
        return true;
    }

    private String getRequiredRole(String subCommand) {
        switch (subCommand) {
            case "randomfill": case "terraform": case "heightmap": case "circle":
            case "sphere": case "cylinder": case "pyramid": case "ellipsoid":
            case "save": case "load": case "list":
                return "\u00A79\u00A7lSTAFF";
            default:
                return "\u00A72\u00A7lBUILDER";
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("\u00A78\u00A7m─────────────────────────────\u00A7r");
        sender.sendMessage("\u00A76\u00A7l\u2726 \u00A7e\u00A7lSPARTA \u00A76\u00A7lBUILDS \u00A76\u00A7l\u2726");
        sender.sendMessage("\u00A78\u00A7m─────────────────────────────\u00A7r");

        if (sender instanceof Player) {
            Player p = (Player) sender;
            String role = getPlayerRole(p);
            Tag tag = getPlayerTag(p);
            sender.sendMessage("\u00A77 Cargo \u00A78\u00BB \u00A7f" + role.toUpperCase() + "\u00A77  \u00A78| \u00A77 Tag \u00A78\u00BB \u00A7f" + (tag != null ? tag.displayName : "nenhuma"));
        }
        sender.sendMessage("");

        boolean hasBuild = sender.hasPermission("spartabuilds.build");
        boolean hasStaff = sender.hasPermission("spartabuilds.staff");
        boolean hasAdmin = sender.hasPermission("spartabuilds.admin");

        if (hasBuild) {
            sender.sendMessage("\u00A72\u00A7l\u2726 BUILDER \u00A78\u00BB \u00A7fComandos de construcao");
            sender.sendMessage("\u00A7e  /bs gradient \u00A77<bloco1> <bloco2> [v|h|d]");
            sender.sendMessage("\u00A7e  /bs checkerboard \u00A77<bloco1> <bloco2> [tam]");
            sender.sendMessage("\u00A7e  /bs stripes \u00A77<bloco1> <bloco2> [larg] [eixo]");
            sender.sendMessage("\u00A7e  /bs outline \u00A77<bloco>");
            sender.sendMessage("\u00A7e  /bs colorswap \u00A77<cor_de> <cor_para>");
            sender.sendMessage("\u00A7e  /bs palette \u00A77[wool|clay|glass]");
            sender.sendMessage("\u00A7e  /bs measure \u00A77- Medir selecao");
            sender.sendMessage("\u00A7e  /bs walls \u00A77<bloco> \u00A7f| \u00A7e/bs floor \u00A77<bloco> \u00A7f| \u00A7e/bs replace \u00A77<origem> <destino>");
            sender.sendMessage("\u00A7e  /bs clear \u00A77- Limpar selecao");
            sender.sendMessage("\u00A7e  /bs undo \u00A77[n] \u00A7f| \u00A7e/bs redo \u00A77[n]");
            sender.sendMessage("");
        }

        if (hasStaff) {
            sender.sendMessage("\u00A79\u00A7l\u2726 STAFF \u00A78\u00BB \u00A7fComandos avancados");
            sender.sendMessage("\u00A7e  /bs randomfill \u00A77<bloco> <peso> ...");
            sender.sendMessage("\u00A7e  /bs terraform \u00A77[camadas]");
            sender.sendMessage("\u00A7e  /bs heightmap \u00A77<raise|lower|smooth> [forca]");
            sender.sendMessage("\u00A7e  /bs circle \u00A77<bloco> <raio> [preenchido]");
            sender.sendMessage("\u00A7e  /bs sphere \u00A77<bloco> <raio> [preenchido] \u00A7f| \u00A7e/bs cylinder \u00A77<bloco> <raio> <altura> [preenchido]");
            sender.sendMessage("\u00A7e  /bs pyramid \u00A77<bloco> <tamanho> [preenchido] \u00A7f| \u00A7e/bs ellipsoid \u00A77<bloco> <rx> <ry> <rz> [preenchido]");
            sender.sendMessage("\u00A7e  /bs save \u00A77<nome> \u00A7f| \u00A7e/bs load \u00A77<nome> \u00A7f| \u00A7e/bs list");
            sender.sendMessage("");
        }

        if (hasBuild) {
            sender.sendMessage("\u00A7d\u00A7l\u2726 PREFIXOS \u00A78\u00BB \u00A7f/tag para trocar");
            sender.sendMessage("\u00A7e  /tag \u00A77- Ver prefixos disponiveis");
            sender.sendMessage("\u00A7e  /tag <nome> \u00A77- Trocar prefixo");
            sender.sendMessage("");
            sender.sendMessage("\u00A76\u00A7lPrefixos disponiveis:");
            if (sender.hasPermission("spartabuilds.tag.builder")) {
                sender.sendMessage("  \u00A72\u00A7lBUILDER \u00A72Nick \u00A7f- /tag builder");
            }
            if (sender.hasPermission("spartabuilds.tag.staff")) {
                sender.sendMessage("  \u00A79\u00A7lSTAFF \u00A79Nick \u00A7f- /tag staff");
            }
            if (sender.hasPermission("spartabuilds.tag.admin")) {
                sender.sendMessage("  \u00A74\u00A7lADMIN \u00A74Nick \u00A7f- /tag admin");
            }
            if (sender.hasPermission("spartabuilds.tag.master")) {
                sender.sendMessage("  \u00A7d\u00A7oPP \u00A7f- /tag master");
            }
            sender.sendMessage("");
        }

        if (hasAdmin) {
            sender.sendMessage("\u00A74\u00A7l\u2726 ADMIN \u00A78\u00BB \u00A7fGerenciamento");
            sender.sendMessage("\u00A7e  /setcargo <nick> <admin|staff|builder|none> \u00A77- Funciona offline");
            sender.sendMessage("\u00A7e  /cargos set <nick> <cargo>");
            sender.sendMessage("\u00A7e  /tag set <nick> <tag> \u00A77- Definir tag de jogador");
            sender.sendMessage("\u00A7e  /tag lista \u00A77- Listar jogadores com tag");
            sender.sendMessage("\u00A7e  /sbreload \u00A77- Recarregar config");
            sender.sendMessage("");
        } else if (hasStaff) {
            sender.sendMessage("\u00A79\u00A7l\u2726 STAFF \u00A78\u00BB \u00A7fGerenciamento");
            sender.sendMessage("\u00A7e  /setcargo <nick> <builder|none> \u00A77- Promover a builder");
            sender.sendMessage("\u00A7e  /cargos list \u00A77- Listar jogadores com cargo");
            sender.sendMessage("\u00A7e  /cargos info <nick> \u00A77- Ver cargo de jogador");
            sender.sendMessage("\u00A7e  /tag lista \u00A77- Listar jogadores com tag");
            sender.sendMessage("");
        }

        if (!hasBuild && !hasStaff && !hasAdmin) {
            sender.sendMessage("\u00A77Sem cargo - contate um administrador para acesso.");
            sender.sendMessage("");
        }

        sender.sendMessage("\u00A78\u00A7m─────────────────────────────\u00A7r");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmd = command.getName().toLowerCase();

        boolean isCustom = false;
        for (String c : CUSTOM_COMMANDS) {
            if (c.equals(cmd)) { isCustom = true; break; }
        }

        if (!isCustom) return super.onTabComplete(sender, command, alias, args);

        String current = args.length > 0 ? args[args.length - 1] : "";

        if (cmd.equals("tag")) return tabTag(sender, args, current);
        if (cmd.equals("setcargo")) return tabSetCargo(sender, args, current);
        if (cmd.equals("cargos")) return tabCargos(sender, args, current);
        if (cmd.equals("bs")) return tabBs(sender, args, current);
        if (cmd.equals("sbreload")) return Collections.emptyList();

        return tabForScript(cmd, args, current);
    }

    private List<String> tabTag(CommandSender sender, String[] args, String current) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if (sender.hasPermission("spartabuilds.admin")) {
                options.add("set");
            }
            if (sender.hasPermission("spartabuilds.staff")) {
                options.add("lista");
            }
            options.add("membro");
            for (Tag tag : Tag.values()) {
                if (sender.hasPermission(tag.permission) || sender.hasPermission("spartabuilds.admin")) {
                    options.add(tag.name().toLowerCase());
                }
            }
            return filter(options, current);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set") && sender.hasPermission("spartabuilds.admin")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return filter(names, current);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("set") && sender.hasPermission("spartabuilds.admin")) {
            List<String> options = new ArrayList<>();
            options.add("membro");
            for (Tag tag : Tag.values()) options.add(tag.name().toLowerCase());
            return filter(options, current);
        }
        return Collections.emptyList();
    }

    private List<String> tabSetCargo(CommandSender sender, String[] args, String current) {
        if (!sender.hasPermission("spartabuilds.staff")) return Collections.emptyList();

        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return filter(names, current);
        }
        if (args.length == 2) {
            List<String> cargos = new ArrayList<>();
            cargos.add("none");
            if (sender.hasPermission("spartabuilds.admin")) {
                cargos.add("admin");
                cargos.add("staff");
            }
            cargos.add("builder");
            return filter(cargos, current);
        }
        return Collections.emptyList();
    }

    private List<String> tabCargos(CommandSender sender, String[] args, String current) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if (sender.hasPermission("spartabuilds.staff")) {
                options.add("list");
                options.add("info");
            }
            if (sender.hasPermission("spartabuilds.setcargo")) {
                options.add("set");
            }
            return filter(options, current);
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("info")) {
                List<String> names = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
                return filter(names, current);
            }
            if (sub.equals("set")) {
                List<String> names = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
                return filter(names, current);
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            List<String> cargos = new ArrayList<>();
            cargos.add("none");
            if (sender.hasPermission("spartabuilds.admin")) {
                cargos.add("admin");
                cargos.add("staff");
            }
            cargos.add("builder");
            return filter(cargos, current);
        }
        return Collections.emptyList();
    }

    private List<String> tabBs(CommandSender sender, String[] args, String current) {
        if (args.length == 1) {
            List<String> available = new ArrayList<>();
            for (String sub : BS_ALL_SUBS) {
                String perm = PERM_MAP.get(sub);
                if (perm == null || sender.hasPermission(perm) || sender.hasPermission("spartabuilds.admin")) {
                    available.add(sub);
                }
            }
            return filter(available, current);
        }

        String sub = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        String subCurrent = subArgs.length > 0 ? subArgs[subArgs.length - 1] : "";

        switch (sub) {
            case "undo": case "redo":
                if (subArgs.length == 1) return filter(UNDO_COUNTS, subCurrent);
                return Collections.emptyList();
            case "save": case "load": case "clear": case "list":
                return Collections.emptyList();
            default:
                return tabForScript(sub, subArgs, subCurrent);
        }
    }

    private List<String> tabForScript(String script, String[] args, String current) {
        switch (script) {
            case "gradient":     return tabGradient(args, current);
            case "checkerboard": return tabCheckerboard(args, current);
            case "stripes":      return tabStripes(args, current);
            case "outline":      return tabOutline(args, current);
            case "randomfill":   return tabRandomFill(args, current);
            case "colorswap":    return tabColorSwap(args, current);
            case "terraform":    return tabTerraform(args, current);
            case "heightmap":    return tabHeightmap(args, current);
            case "palette":      return tabPalette(args, current);
            case "circle":       return tabCircle(args, current);
            case "sphere":       return tabSphere(args, current);
            case "cylinder":     return tabCylinder(args, current);
            case "pyramid":      return tabPyramid(args, current);
            case "ellipsoid":    return tabEllipsoid(args, current);
            case "walls":        return tabOutline(args, current);
            case "floor":        return tabOutline(args, current);
            case "replace":      return tabReplace(args, current);
            default:             return Collections.emptyList();
        }
    }

    private List<String> tabGradient(String[] a, String c) {
        switch (a.length) {
            case 1: case 2: return filter(ALL_BLOCKS, c);
            case 3: return filter(GRADIENT_DIRS, c);
            default: return Collections.emptyList();
        }
    }

    private List<String> tabCheckerboard(String[] a, String c) {
        switch (a.length) {
            case 1: case 2: return filter(ALL_BLOCKS, c);
            case 3: return filter(SMALL_NUMBERS, c);
            default: return Collections.emptyList();
        }
    }

    private List<String> tabStripes(String[] a, String c) {
        switch (a.length) {
            case 1: case 2: return filter(ALL_BLOCKS, c);
            case 3: return filter(SMALL_NUMBERS, c);
            case 4: return filter(AXES, c);
            default: return Collections.emptyList();
        }
    }

    private List<String> tabOutline(String[] a, String c) {
        return a.length == 1 ? filter(ALL_BLOCKS, c) : Collections.<String>emptyList();
    }

    private List<String> tabRandomFill(String[] a, String c) {
        return a.length % 2 == 1 ? filter(ALL_BLOCKS, c) : filter(WEIGHT_NUMBERS, c);
    }

    private List<String> tabColorSwap(String[] a, String c) {
        return a.length <= 2 ? filter(COLORS, c) : Collections.<String>emptyList();
    }

    private List<String> tabTerraform(String[] a, String c) {
        return a.length == 1 ? filter(SMALL_NUMBERS, c) : Collections.<String>emptyList();
    }

    private List<String> tabHeightmap(String[] a, String c) {
        switch (a.length) {
            case 1: return filter(HEIGHTMAP_MODES, c);
            case 2: return filter(INTENSITY_NUMBERS, c);
            default: return Collections.emptyList();
        }
    }

    private List<String> tabPalette(String[] a, String c) {
        return a.length == 1 ? filter(PALETTE_TYPES, c) : Collections.<String>emptyList();
    }

    private List<String> tabCircle(String[] a, String c) {
        switch (a.length) {
            case 1: return filter(ALL_BLOCKS, c);
            case 2: return filter(RADIUS_NUMBERS, c);
            case 3: return filter(BOOL_OPTIONS, c);
            default: return Collections.emptyList();
        }
    }

    private List<String> tabSphere(String[] a, String c) {
        switch (a.length) {
            case 1: return filter(ALL_BLOCKS, c);
            case 2: return filter(RADIUS_NUMBERS, c);
            case 3: return filter(BOOL_OPTIONS, c);
            default: return Collections.emptyList();
        }
    }

    private List<String> tabCylinder(String[] a, String c) {
        switch (a.length) {
            case 1: return filter(ALL_BLOCKS, c);
            case 2: return filter(RADIUS_NUMBERS, c);
            case 3: return filter(INTENSITY_NUMBERS, c);
            case 4: return filter(BOOL_OPTIONS, c);
            default: return Collections.emptyList();
        }
    }

    private List<String> tabReplace(String[] a, String c) {
        return a.length <= 2 ? filter(ALL_BLOCKS, c) : Collections.emptyList();
    }

    private List<String> tabPyramid(String[] a, String c) {
        switch (a.length) {
            case 1: return filter(ALL_BLOCKS, c);
            case 2: return filter(RADIUS_NUMBERS, c);
            case 3: return filter(BOOL_OPTIONS, c);
            default: return Collections.emptyList();
        }
    }

    private List<String> tabEllipsoid(String[] a, String c) {
        switch (a.length) {
            case 1: return filter(ALL_BLOCKS, c);
            case 2: case 3: case 4: return filter(RADIUS_NUMBERS, c);
            case 5: return filter(BOOL_OPTIONS, c);
            default: return Collections.emptyList();
        }
    }

    private List<String> filter(List<String> options, String input) {
        if (input.isEmpty()) return new ArrayList<>(options);
        String lower = input.toLowerCase();
        return options.stream()
            .filter(s -> s.toLowerCase().startsWith(lower))
            .collect(Collectors.toList());
    }
}
