package net.devscape.woolwars.listeners;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null) {
            InventoryHolder holder = e.getClickedInventory().getHolder();

            if (holder instanceof Menu) {
                e.setCancelled(true);

                if (e.getCurrentItem() == null) {
                    return;
                }

                Menu menu = (Menu) holder;
                menu.handleMenu(e);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (e.getInventory().getHolder() != null) {
            InventoryHolder holder = e.getInventory().getHolder();

            if (holder instanceof Menu) {
                WoolWars.getWoolWars().getMenuUtilMap().remove(p);
            }
        }
    }
}