package net.devscape.project.woolwars;

import lombok.Getter;
import lombok.Setter;
import net.devscape.project.woolwars.managers.GameManager;
import net.devscape.project.woolwars.managers.KitManager;
import net.devscape.project.woolwars.managers.LocalStatsManager;
import net.devscape.project.woolwars.menus.MenuUtil;
import net.devscape.project.woolwars.storage.H2Data;
import net.devscape.project.woolwars.utils.ClassRegistrationUtils;
import net.devscape.project.woolwars.utils.command.CommandFramework;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class WoolWars extends JavaPlugin {

    @Getter private static WoolWars woolWars;

    private GameManager gameManager;
    private KitManager kitManager;
    private LocalStatsManager localStatsManager;

    private H2Data h2Data;

    @Getter private static HashMap<Player, MenuUtil> menuUtilMap = new HashMap<>();

    private CommandFramework commandFramework = new CommandFramework(this);

    @Override
    public void onEnable() {
        woolWars = this;

        saveDefaultConfig();

        this.h2Data = new H2Data();
        this.loadCommands();
        this.loadManagers();
        this.loadListeners();
        this.loadRunnables();
    }

    @Override
    public void onDisable() {
        woolWars = null;
    }

    private void loadCommands() {
        ClassRegistrationUtils.loadCommands("net.devscape.project.woolwars.commands");
    }

    private void loadManagers() {
        gameManager = new GameManager();
        kitManager = new KitManager(getConfig());
        localStatsManager = new LocalStatsManager();
    }

    private void loadListeners() {
        ClassRegistrationUtils.loadListeners("net.devscape.project.woolwars.listeners");
    }

    private void loadRunnables() {

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