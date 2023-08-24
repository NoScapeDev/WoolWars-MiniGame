package net.devscape.woolwars.managers;

import lombok.Getter;
import net.devscape.woolwars.WoolWars;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class KitManager {

    private final FileConfiguration config;
    private Map<String, List<ItemStack>> kitMap = new HashMap<>();

    public KitManager(FileConfiguration config) {
        this.config = config;
        loadKits();
    }

    public void loadKits() {
        ConfigurationSection kitsSection = config.getConfigurationSection("kits");

        if (kitsSection != null) {
            for (String kitName : kitsSection.getKeys(false)) {
                ConfigurationSection kitItemsSection = kitsSection.getConfigurationSection(kitName + ".items");

                if (kitItemsSection != null) {
                    List<ItemStack> kitItems = new ArrayList<>();

                    for (String itemKey : kitItemsSection.getKeys(false)) {
                        ItemStack itemStack = kitItemsSection.getItemStack(itemKey);
                        if (itemStack != null) {
                            kitItems.add(itemStack);
                        }
                    }

                    kitMap.put(kitName, kitItems);
                }
            }
        }
    }

    public void saveKit(String kitName, ItemStack[] items) {
        List<ItemStack> kit_items = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                kit_items.add(items[i]);
            }
        }

        kitMap.put(kitName, kit_items);
    }

    public List<ItemStack> getKitList(String kitName) {
        return kitMap.get(kitName);
    }

    public ItemStack[] getKit(String kitName) {
        List<ItemStack> kitItems = kitMap.get(kitName);
        ItemStack[] items = new ItemStack[41]; // Initialize with correct length

        for (int index = 0; index < kitItems.size(); index++) {
            ItemStack item = kitItems.get(index);
            if (item != null) {
                if (index < 36) {
                    items[index] = item;
                } else if (index == 36) {
                    // Set boots
                    items[36] = item;
                } else if (index == 37) {
                    // Set leggings
                    items[37] = item;
                } else if (index == 38) {
                    // Set chestplate
                    items[38] = item;
                } else if (index == 39) {
                    // Set helmet
                    items[39] = item;
                } else if (index == 40) {
                    // Set offhand
                    items[40] = item;
                }
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

    public void saveAllKits() {
        for (Map.Entry<String, List<ItemStack>> entry : kitMap.entrySet()) {
            String kitName = entry.getKey();
            List<ItemStack> kitItems = entry.getValue();

            ConfigurationSection kitSection = config.getConfigurationSection("kits." + kitName);
            if (kitSection == null) {
                kitSection = config.createSection("kits." + kitName);
            }

            ConfigurationSection kitItemsSection = kitSection.createSection("items"); // Create items section

            for (int index = 0; index < kitItems.size(); index++) {
                ItemStack item = kitItems.get(index);
                if (item != null) {
                    kitItemsSection.set(String.valueOf(index), item);
                }
            }
        }

        WoolWars.getWoolWars().saveConfig();
    }
}