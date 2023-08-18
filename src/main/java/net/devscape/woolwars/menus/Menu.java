package net.devscape.woolwars.menus;

import net.devscape.woolwars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    protected MenuUtil menuUtil;

    public Menu(MenuUtil menuUtil) {
        this.menuUtil = menuUtil;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();

        menuUtil.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack makeItem(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(Utils.format(displayName));

        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack makeItem(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(Utils.format(displayName));

        itemMeta.setLore(Utils.color(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack makeItem(Material material, String displayName, List<String> lore, int custom_model_data) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(Utils.format(displayName));

        if (custom_model_data > 0) {
            itemMeta.setCustomModelData(custom_model_data);
        }

        itemMeta.setLore(Utils.color(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    public void fillEmpty() {
        for (int i = 0; i <inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i,  makeItem(Material.GRAY_STAINED_GLASS_PANE, " "));
            }
        }
    }
}
