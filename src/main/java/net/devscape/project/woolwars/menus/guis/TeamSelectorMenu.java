package net.devscape.project.woolwars.menus.guis;

import net.devscape.project.woolwars.WoolWars;
import net.devscape.project.woolwars.handlers.Game;
import net.devscape.project.woolwars.menus.Menu;
import net.devscape.project.woolwars.menus.MenuUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

import static net.devscape.project.woolwars.utils.Utils.*;

public class TeamSelectorMenu extends Menu {

    public TeamSelectorMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return format("&8Select a Team");
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        if (e.getSlot() == 2) {
            if (player.hasPermission("game.select.any")) {
                game.selectTeam(player, "red");
            } else {
                msgPlayer(player, "&c&l[GAME] &7Only players with &f&l<Rank+> &7can select any team.");
            }
            player.closeInventory();
        }

        if (e.getSlot() == 4) {
            if (!game.getTeam(player).equalsIgnoreCase("spectator")) {
                msgPlayer(player, "&c&l[GAME] &7You're already in a team.");
                return;
            }

            int blue = game.getBlue().size();
            int red = game.getRed().size();

            if (red > blue) {
                game.selectTeam(player, "blue");
            }

            if (blue > red) {
                game.selectTeam(player, "red");
            }

            if (blue == red) {
                if (Math.random() < 0.5) {
                    game.selectTeam(player, "blue");
                } else {
                    game.selectTeam(player, "red");
                }
            }

            player.closeInventory();
        }

        if (e.getSlot() == 6) {
            if (player.hasPermission("game.select.any")) {
                game.selectTeam(player, "blue");
            } else {
                msgPlayer(player, "&c&l[GAME] &7Only players with &f&l<Rank+> &7can select any team.");
            }
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        for (String str : Objects.requireNonNull(WoolWars.getWoolWars().getConfig().getConfigurationSection("items")).getKeys(false)) {
            if (str.contains("-wool")) {
                String displayname = WoolWars.getWoolWars().getConfig().getString("items." + str + ".displayname");
                int slot = WoolWars.getWoolWars().getConfig().getInt("items." + str + ".slot");
                int custom_model_data = WoolWars.getWoolWars().getConfig().getInt("items." + str + ".custom-model-data");
                List<String> lore = WoolWars.getWoolWars().getConfig().getStringList("items." + str + ".lore");
                String material = WoolWars.getWoolWars().getConfig().getString("items." + str + ".material");

                assert material != null;
                getInventory().setItem(slot, makeItem(Material.valueOf(material.toUpperCase()), displayname, color(lore), custom_model_data));
            }
        }
    }
}