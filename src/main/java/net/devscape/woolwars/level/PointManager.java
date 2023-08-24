package net.devscape.woolwars.level;

import net.devscape.woolwars.WoolWars;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static net.devscape.woolwars.utils.Utils.msgPlayer;
import static net.devscape.woolwars.utils.Utils.soundPlayer;

public class PointManager {

    public void checkLevel(Player player) {
        int currentLevel = WoolWars.getWoolWars().getMariaDB().getLevel(player.getUniqueId());
        int currentPoints = WoolWars.getWoolWars().getMariaDB().getPoints(player.getUniqueId());

        // Load the configuration
        FileConfiguration config = WoolWars.getWoolWars().getConfig();

        if (config.getConfigurationSection("levels.level-" + (currentLevel + 1)) == null) {
            return;
        }

        int requiredPoints = config.getInt("levels.level-" + (currentLevel + 1), -1);

        if (currentPoints >= requiredPoints) {
            int newLevel = currentLevel + 1;

            WoolWars.getWoolWars().getMariaDB().setLevel(player.getUniqueId(), newLevel);
            msgPlayer(player, "&f係 &aLevelled up to " + newLevel);
            soundPlayer(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }
    }
}