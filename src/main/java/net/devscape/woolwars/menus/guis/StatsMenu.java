package net.devscape.woolwars.menus.guis;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.menus.Menu;
import net.devscape.woolwars.menus.MenuUtil;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.text.DecimalFormat;

import static net.devscape.woolwars.utils.Utils.format;

public class StatsMenu extends Menu {

    private String game = "WoolWars";

    public StatsMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return format(WoolWars.getWoolWars().getConfig().getString("menus.stats.title"));
    }

    @Override
    public int getSlots() {
        return WoolWars.getWoolWars().getConfig().getInt("menus.stats.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

    }

    @Override
    public void setMenuItems() {
        if (game.equalsIgnoreCase("woolwars")) {
            int kills = WoolWars.getWoolWars().getH2Data().getKills(menuUtil.getOwner().getUniqueId());
            int deaths = WoolWars.getWoolWars().getH2Data().getDeaths(menuUtil.getOwner().getUniqueId());

            double KD;

            if (deaths > 0) {
                KD = (double) kills / deaths;
            } else {
                KD = kills; // Handle the case when deaths are zero
            }

            DecimalFormat kdFormat;

            if (KD < 10.0) {
                kdFormat = new DecimalFormat("0.00"); // Format with two decimal places
            } else {
                kdFormat = new DecimalFormat("00.00"); // Format with leading zero if over 10
            }

            String formattedKD = kdFormat.format(KD);

            getInventory().setItem(28, makeItem(Material.LIGHT_BLUE_WOOL, "&c&lKills: &f" + kills));
            getInventory().setItem(46, makeItem(Material.LIGHT_BLUE_WOOL, "&6&lDeaths: &f" + deaths));
            getInventory().setItem(31, makeItem(Material.LIGHT_BLUE_WOOL, "&b&lKD: &f" + formattedKD));
            getInventory().setItem(49, makeItem(Material.LIGHT_BLUE_WOOL, "&e&lWool Broken: &f" + WoolWars.getWoolWars().getH2Data().getWoolBroken(menuUtil.getOwner().getUniqueId())));
            getInventory().setItem(34, makeItem(Material.LIGHT_BLUE_WOOL, "&a&lWins: &f" + WoolWars.getWoolWars().getH2Data().getWins(menuUtil.getOwner().getUniqueId())));
            getInventory().setItem(52, makeItem(Material.LIGHT_BLUE_WOOL, "&4&lLosses: &f" + WoolWars.getWoolWars().getH2Data().getLosses(menuUtil.getOwner().getUniqueId())));
        }
    }
}