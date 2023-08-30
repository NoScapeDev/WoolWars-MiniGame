package net.devscape.woolwars.level;

import net.devscape.woolwars.WoolWars;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static net.devscape.woolwars.utils.Utils.msgPlayer;
import static net.devscape.woolwars.utils.Utils.soundPlayer;

public class PointManager {

    public boolean hasEnoughPointsForLevelUP(Player player) {
        return WoolWars.getWoolWars().getMariaDB().getPoints(player.getUniqueId()) >= getPointsRequired(player);
    }

    public void levelUp(Player player) {
        if (hasEnoughPointsForLevelUP(player)) {
            int nextLevel = getNextLevel(player);

            WoolWars.getWoolWars().getMariaDB().setLevel(player.getUniqueId(), nextLevel);
            msgPlayer(player, "&f係 &aLevelled up to &l" + nextLevel);
            soundPlayer(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }
    }

    public void addPoints(Player player, int points) {
        WoolWars.getWoolWars().getMariaDB().setPoints(player.getUniqueId(), WoolWars.getWoolWars().getMariaDB().getPoints(player.getUniqueId()) + points);
        levelUp(player);
    }

    public int getLevel(Player player) {
        return WoolWars.getWoolWars().getMariaDB().getLevel(player.getUniqueId());
    }

    public int getNextLevel(Player player) {
        return getLevel(player) + 1;
    }

    public int getPointsRequired(Player player) {
        return WoolWars.getWoolWars().getConfig().getInt("levels." + getNextLevel(player) + ".required-points");
    }
}