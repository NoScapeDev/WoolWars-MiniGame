package net.devscape.woolwars.menus.guis;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.managers.KitManager;
import net.devscape.woolwars.menus.Menu;
import net.devscape.woolwars.menus.MenuUtil;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.devscape.woolwars.utils.Utils.format;

public class KitSelectorMenu extends Menu {

    public KitSelectorMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return format(WoolWars.getWoolWars().getConfig().getString("menus.kit-selector.title"));
    }

    @Override
    public int getSlots() {
        return WoolWars.getWoolWars().getConfig().getInt("menus.kit-selector.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        //Game game = WoolWars.getWoolWars().getGameManager().getGame();

        ItemStack item = e.getCurrentItem();

        if (item != null && item.getItemMeta() != null && e.getSlot() != 49) {
            String displayname = Utils.deformat(item.getItemMeta().getDisplayName());

            boolean isValidKit = false;

            if (WoolWars.getWoolWars().getKitManager().getKitMap().containsKey(displayname)) {
                isValidKit = true;
            }

            if (isValidKit) {
                if (WoolWars.getWoolWars().getSelectedKit(player).equalsIgnoreCase(displayname)) {
                    Utils.msgPlayer(player, "&f侵 &7You already have this kit selected.");
                    return;
                }

                WoolWars.getWoolWars().saveSelectedKit(player, displayname);
                Utils.msgPlayer(player, "&f係 &7Kit &f&l" + displayname + " &7selected!");
            }
        }

        if (e.getSlot() == 49) {
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        int startingSlot = 27; 

        for (String kit : WoolWars.getWoolWars().getKitManager().getKitMap().keySet()) {
            List<String> lore = new ArrayList<>();

            lore.add("&eItems:");
            if (WoolWars.getWoolWars().getKitManager().getKitList(kit) != null) {
                List<ItemStack> kitItems = WoolWars.getWoolWars().getKitManager().getKitList(kit);

                for (ItemStack items : kitItems) {
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

            ItemStack kitItem = Utils.makeItem(Material.IRON_SWORD, "&f" + kit, Utils.color(lore));
            getInventory().setItem(startingSlot, kitItem);

            startingSlot++; // Increment the slot for the next item
        }

        getInventory().setItem(49, Utils.makeItem(Material.BARRIER, "&#FF3A3A&lClose", Utils.format("&7Click to close the menu!")));
    }
}