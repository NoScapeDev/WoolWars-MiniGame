package net.devscape.woolwars.listeners;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.menus.guis.KitSelectorMenu;
import net.devscape.woolwars.menus.guis.TeamSelectorMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static net.devscape.woolwars.utils.Utils.format;

public class ItemListener implements Listener {

    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if (item != null && item.getItemMeta() != null) {
            String displayname = item.getItemMeta().getDisplayName();

            if (displayname.equalsIgnoreCase(format(WoolWars.getWoolWars().getConfig().getString("items.team-selector.displayname")))) {
                new TeamSelectorMenu(WoolWars.getMenuUtil(player)).open();
            }

            if (displayname.equalsIgnoreCase(format(WoolWars.getWoolWars().getConfig().getString("items.kit-selector.displayname")))) {
                new KitSelectorMenu(WoolWars.getMenuUtil(player)).open();
            }
        }
    }
}
