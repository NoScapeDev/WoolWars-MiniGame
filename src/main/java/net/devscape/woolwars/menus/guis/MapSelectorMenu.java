package net.devscape.woolwars.menus.guis;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.menus.Menu;
import net.devscape.woolwars.menus.MenuUtil;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static net.devscape.woolwars.utils.Utils.color;
import static net.devscape.woolwars.utils.Utils.format;

public class MapSelectorMenu extends Menu {

    public MapSelectorMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return format("&8Map Selector");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        ItemStack item = e.getCurrentItem();

        if (item != null && item.getItemMeta() != null && e.getSlot() != 49) {
            String map_name = Utils.deformat(item.getItemMeta().getDisplayName());

            if (WoolWars.getWoolWars().getGameManager().getGame(map_name) != null) {
                // handle voting
            }
        }

        if (e.getSlot() == 49) {
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        for (Game games : WoolWars.getWoolWars().getGameManager().getGameMap()) {
            if (games != null) {
                if (games.isActiveGame()) {
                    getInventory().addItem(Utils.makeItem(Material.FILLED_MAP, "&f" + games.getMapName() + " &a(Active)", Utils.format("&7Click to vote for this map!")));
                } else {
                    getInventory().addItem(Utils.makeItem(Material.FILLED_MAP, "&f" + games.getMapName() + " &7(In-Active)", Utils.format("&7Click to vote for this map!")));
                }
            }
        }

        getInventory().setItem(49, Utils.makeItem(Material.BARRIER, "&#FF3A3A&lClose", Utils.format("&7Click to close the menu!")));
    }
}