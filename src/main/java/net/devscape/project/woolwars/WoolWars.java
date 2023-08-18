package net.devscape.project.woolwars;

import net.devscape.project.woolwars.listeners.CombatListener;
import net.devscape.project.woolwars.listeners.GameListener;
import net.devscape.project.woolwars.listeners.ItemListener;
import net.devscape.project.woolwars.managers.GameManager;
import net.devscape.project.woolwars.managers.KitManager;
import net.devscape.project.woolwars.managers.LocalStatsManager;
import net.devscape.project.woolwars.menus.MenuListener;
import net.devscape.project.woolwars.menus.MenuUtil;
import net.devscape.project.woolwars.storage.H2Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class WoolWars extends JavaPlugin {

    private static WoolWars woolWars;
    private GameManager gameManager;
    private KitManager kitManager;
    private H2Data h2Data;
    private final Map<Player, Integer> playerHealthMap = new HashMap<>();
    private LocalStatsManager localStatsManager;
    private static final HashMap<Player, MenuUtil> menuUtilMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        woolWars = this;
        saveDefaultConfig();

        gameManager = new GameManager();
        kitManager = new KitManager(getConfig());
        localStatsManager = new LocalStatsManager();

        h2Data = new H2Data();

        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getCommand("game").setExecutor(new GameCommand());
        getCommand("stats").setExecutor(new GameCommand());

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        new HealthUpdateTask().runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MenuUtil getMenuUtil(Player player) {
        MenuUtil menuUtil;

        if (menuUtilMap.containsKey(player)) {
            return menuUtilMap.get(player);
        } else {
            menuUtil = new MenuUtil(player);
            menuUtilMap.put(player, menuUtil);
        }

        return menuUtil;
    }

    public HashMap<Player, MenuUtil> getMenuUtilMap() {
        return menuUtilMap;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void reload() {
        super.reloadConfig();
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public void saveSelectedKit(Player player, String kitName) {
        h2Data.setSelectedKit(player.getUniqueId(), kitName);
    }

    public String getSelectedKit(Player player) {
        String selectedKit = h2Data.getSelectedKit(player.getUniqueId());
        return selectedKit != null ? selectedKit : "Archer"; // Provide a default kit name if none is selected
    }

    public H2Data getH2Data() {
        return h2Data;
    }

    public void updateHealth(Player player) {
        int health = (int) Math.ceil(player.getHealth());
        playerHealthMap.put(player, health);
    }

    public LocalStatsManager getLocalStatsManager() {
        return localStatsManager;
    }

    private class PlayerListener implements org.bukkit.event.Listener {
        @org.bukkit.event.EventHandler
        public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
            Player player = event.getPlayer();
            updateHealth(player);
        }
    }

    private class HealthUpdateTask extends BukkitRunnable {
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                int health = playerHealthMap.getOrDefault(player, 0);
                String healthText = ChatColor.RED + "❤ " + health;
                String playerName = player.getName();
                String customName = playerName + "\n" + healthText;
                player.setCustomName(customName);
                player.setCustomNameVisible(true);
            }
        }
    }

    public static WoolWars getWoolWars() {
        return woolWars;
    }
}