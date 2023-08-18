package net.devscape.project.woolwars.menus.guis;

import net.devscape.project.woolwars.WoolWars;
import net.devscape.project.woolwars.handlers.Game;
import net.devscape.project.woolwars.menus.Menu;
import net.devscape.project.woolwars.menus.MenuUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.devscape.project.woolwars.utils.Utils.*;

public class KitSelectorMenu extends Menu {

    public KitSelectorMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return format("&8Select a Kit");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        //Game game = WoolWars.getWoolWars().getGameManager().getGame();

        ItemStack item = e.getCurrentItem();

        if (item != null && item.getItemMeta() != null && e.getSlot() != 49) {
            String displayname = deformat(item.getItemMeta().getDisplayName());

            boolean isValidKit = false;

            for (String kit : Objects.requireNonNull(WoolWars.getWoolWars().getConfig().getConfigurationSection("kits")).getKeys(false)) {
                if (kit.equalsIgnoreCase(displayname)) {
                    isValidKit = true;
                    break;
                }
            }

            if (isValidKit) {
                if (WoolWars.getWoolWars().getSelectedKit(player).equalsIgnoreCase(displayname)) {
                    msgPlayer(player, "&c&l[GAME] &7You already have this kit selected.");
                    return;
                }

                WoolWars.getWoolWars().saveSelectedKit(player, displayname);
                msgPlayer(player, "&a&l[GAME] &7Kit &f&l" + displayname + " &7selected!");
            }
        }

        if (e.getSlot() == 49) {
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        for (String kit : Objects.requireNonNull(WoolWars.getWoolWars().getConfig().getConfigurationSection("kits")).getKeys(false)) {
            List<String> lore = new ArrayList<>();

            lore.add("&eItems:");
            if (WoolWars.getWoolWars().getKitManager().getKitList(kit) != null) {
                for (ItemStack items : WoolWars.getWoolWars().getKitManager().getKitList(kit)) {
                    if (items.getType() == Material.AIR) return;

                    lore.add("&7- &d" + items.getAmount() + "x " + items.getType());
                }
            }

            lore.add("");
            lore.add("&e&m--------------=[&f &6&lAbilities &e&m]=-------------");
            lore.add(" ");
            lore.add("&f<kit abilities here>");
            lore.add("");
            lore.add("&e&m---------------=[&f &6&lLore &e&m]=---------------");
            lore.add(" ");
            lore.add("&f<kit lore here>");

            getInventory().addItem(makeItem(Material.IRON_SWORD, "&f" + kit, color(lore)));
        }

        getInventory().setItem(49, makeItem(Material.BARRIER, "&#FF3A3A&lClose", format("&7Click to close the menu!")));
    }
}