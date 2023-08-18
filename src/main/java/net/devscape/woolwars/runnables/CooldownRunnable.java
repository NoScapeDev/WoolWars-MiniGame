package net.devscape.woolwars.runnables;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CooldownRunnable extends BukkitRunnable {

    private WoolWars main = WoolWars.getWoolWars();

    @Override
    public void run() {
        for (Player player : this.main.getServer().getOnlinePlayers()) {
            if (this.main.getCooldownManager().isOnCooldown(player)) {
                int count = this.main.getCooldownManager().getCooldownTime(player);
                count--;

                this.main.getCooldownManager().setCooldownTime(player, count);

                if (count == 0) {
                    this.main.getCooldownManager().getCooldownSet().remove(player);
                    this.main.getCooldownManager().getCooldownTimeMap().remove(player);

                    player.setLevel(0);
                    player.setExp(0);
                    player.sendMessage(Utils.format("&aYou can use your ability again"));
                }
            }
        }
    }
}
