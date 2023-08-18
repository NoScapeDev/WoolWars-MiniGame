package net.devscape.woolwars.managers;

import net.devscape.woolwars.WoolWars;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitManager {

    private final FileConfiguration config;

    public KitManager(FileConfiguration config) {
        this.config = config;
    }

    public void saveKit(String kitName, ItemStack[] items) {
        ConfigurationSection kitSection = config.createSection("kits." + kitName);

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                kitSection.set("items." + i, items[i]);
            }
        }

        WoolWars.getWoolWars().saveConfig();
        WoolWars.getWoolWars().reload();
    }

    public List<ItemStack> getKitList(String kitName) {
        ConfigurationSection kitSection = config.getConfigurationSection("kits." + kitName + ".items");

        if (kitSection == null) {
            return new ArrayList<>(); // Return an empty list
        }

        List<ItemStack> items = new ArrayList<>(); // Initialize the list

        for (String key : kitSection.getKeys(false)) {
            ItemStack item = kitSection.getItemStack(key);
            items.add(item); // Add the item to the end of the list
        }

        return items;
    }


    public ItemStack[] getKit(String kitName) {
        ConfigurationSection kitSection = config.getConfigurationSection("kits." + kitName + ".items");

        if (kitSection == null) {
            return new ItemStack[0];
        }

        ItemStack[] items = new ItemStack[41]; // Initialize with correct length

        for (String key : kitSection.getKeys(false)) {
            int index = Integer.parseInt(key);

            // Validate index to ensure it's within the range of the items array
            if (index >= 0 && index < items.length) {
                ItemStack item = kitSection.getItemStack(key);
                items[index] = item;
            }
        }

        return items;
    }

    public void applyKit(Player player, String kitName) {
        ItemStack[] kitItems = getKit(kitName);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItemInOffHand(null);

        for (int index = 0; index < kitItems.length; index++) {
            ItemStack item = kitItems[index];
            if (item != null) {
                if (index < 36) {
                    player.getInventory().setItem(index, item);
                } else if (index == 36) {
                    // Set boots
                    player.getInventory().setBoots(item);
                } else if (index == 37) {
                    // Set leggings
                    player.getInventory().setLeggings(item);
                } else if (index == 38) {
                    // Set chestplate
                    player.getInventory().setChestplate(item);
                } else if (index == 39) {
                    // Set helmet
                    player.getInventory().setHelmet(item);
                } else if (index == 40) {
                    // Set offhand
                    player.getInventory().setItemInOffHand(item);
                }
            }
        }
    }
}