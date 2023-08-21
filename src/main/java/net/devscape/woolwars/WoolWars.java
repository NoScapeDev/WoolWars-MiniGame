package net.devscape.woolwars;

import aether.Aether;
import lombok.Getter;
import lombok.Setter;
import net.devscape.woolwars.managers.*;
import net.devscape.woolwars.managers.abilities.AbilityManager;
import net.devscape.woolwars.menus.MenuUtil;
import net.devscape.woolwars.runnables.CooldownRunnable;
import net.devscape.woolwars.scoreboard.ScoreboardProvider;
import net.devscape.woolwars.storage.H2Data;
import net.devscape.woolwars.storage.MySQL;
import net.devscape.woolwars.utils.BungeeUtils;
import net.devscape.woolwars.utils.ClassRegistrationUtils;
import net.devscape.woolwars.utils.command.CommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

@Getter
@Setter
public class WoolWars extends JavaPlugin {

    @Getter private static WoolWars woolWars;

    private BungeeUtils bungeeUtils;
    private PlayerDataManager playerDataManager;
    private GameManager gameManager;
    private KitManager kitManager;
    private ResetMapManager resetManager;
    private AbilityManager abilityManager;
    private CooldownManager cooldownManager;
    private CombatManager combatManager;
    private LocalStatsManager localStatsManager;

    private H2Data h2Data;
    private MySQL mySQL;

    @Getter private static HashMap<Player, MenuUtil> menuUtilMap = new HashMap<>();

    private CommandFramework commandFramework = new CommandFramework(this);

    @Override
    public void onEnable() {
        woolWars = this;

        saveDefaultConfig();

        this.h2Data = new H2Data();
        //this.mySQL = new MySQL("", 3306, "", "", "", "");

        bungeeUtils = new BungeeUtils(this);

        this.loadCommands();
        this.loadManagers();
        this.loadListeners();
        this.loadRunnables();

    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            bungeeUtils.sendPlayerToServer(player, "hub");
        }

        getResetManager().resetMapInstant();
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    private void loadCommands() {
        ClassRegistrationUtils.loadCommands("net.devscape.woolwars.commands");
    }

    private void loadManagers() {
        playerDataManager = new PlayerDataManager();
        gameManager = new GameManager();
        kitManager = new KitManager(getConfig());
        resetManager = new ResetMapManager();
        abilityManager = new AbilityManager();
        cooldownManager = new CooldownManager();
        combatManager = new CombatManager();
        localStatsManager = new LocalStatsManager();
    }

    private void loadListeners() {
        ClassRegistrationUtils.loadListeners("net.devscape.woolwars.listeners");
    }

    private void loadRunnables() {
        new Aether(this, new ScoreboardProvider());
        new CooldownRunnable().runTaskTimer(this, 0, 20);
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

    public void reload() {
        super.reloadConfig();
    }

    public void saveSelectedKit(Player player, String kitName) {
        h2Data.setSelectedKit(player.getUniqueId(), kitName);
    }

    public String getSelectedKit(Player player) {
        String selectedKit = h2Data.getSelectedKit(player.getUniqueId());
        return selectedKit != null ? selectedKit : "Archer"; // Provide a default kit name if none is selected
    }
}