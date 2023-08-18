package net.devscape.woolwars.managers;

import lombok.Getter;
import lombok.Setter;
import net.devscape.woolwars.WoolWars;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class CooldownManager {

    private WoolWars main = WoolWars.getWoolWars();

    private Set<Player> cooldownSet = new HashSet<Player>();

    private Map<Player, Integer> cooldownTimeMap = new HashMap<Player, Integer>();

    private int cooldownCount = 0;
    private int refillCount = 0;

    public void setCooldownSet(Player player, boolean inSpawn, int number) {
        if (inSpawn) {
            this.cooldownSet.add(player);
            this.cooldownTimeMap.put(player, number);
        } else {
            this.cooldownSet.remove(player);
            this.cooldownTimeMap.remove(player);
        }
    }

    public int getCooldownTime(Player player) {
        return this.cooldownTimeMap.get(player);
    }

    public void setCooldownTime(Player player, int time) {
        this.cooldownTimeMap.remove(player);
        this.cooldownTimeMap.put(player, time);
    }

    public boolean isOnCooldown(Player player) {
        return this.cooldownSet.contains(player);
    }
}
