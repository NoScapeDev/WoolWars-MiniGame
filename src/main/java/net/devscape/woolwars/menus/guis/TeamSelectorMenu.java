package net.devscape.woolwars.menus.guis;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.menus.Menu;
import net.devscape.woolwars.menus.MenuUtil;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.devscape.woolwars.utils.Utils.format;

public class TeamSelectorMenu extends Menu {

    public TeamSelectorMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return format(WoolWars.getWoolWars().getConfig().getString("menus.team-selector.title"));
    }

    @Override
    public int getSlots() {
        return WoolWars.getWoolWars().getConfig().getInt("menus.team-selector.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        if (e.getSlot() == 2) {
            if (player.hasPermission("game.select.any")) {
                game.selectTeam(player, "red");
            } else {
                Utils.msgPlayer(player, "&c&l[GAME] &7Only players with &f&l<Rank+> &7can select any team.");
            }
            player.closeInventory();
        }

        if (e.getSlot() == 4) {
            if (!game.getTeam(player).equalsIgnoreCase("spectator")) {
                Utils.msgPlayer(player, "&c&l[GAME] &7You're already in a team.");
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
                Utils.msgPlayer(player, "&c&l[GAME] &7Only players with &f&l<Rank+> &7can select any team.");
            }
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        for (String str : Objects.requireNonNull(WoolWars.getWoolWars().getConfig().getConfigurationSection("items")).getKeys(false)) {
            if (str.contains("-wool")) {
                String displayname = WoolWars.getWoolWars().getConfig().getString("items." + str + ".displayname");
                List<String> slot = new ArrayList<>(WoolWars.getWoolWars().getConfig().getStringList("items." + str + ".slots"));
                int custom_model_data = WoolWars.getWoolWars().getConfig().getInt("items." + str + ".custom-model-data");
                List<String> lore = WoolWars.getWoolWars().getConfig().getStringList("items." + str + ".lore");
                String material = WoolWars.getWoolWars().getConfig().getString("items." + str + ".material");

                assert material != null;

                for (String slot_string : slot) {
                    getInventory().setItem(Integer.parseInt(slot_string), Utils.makeItem(Material.valueOf(material.toUpperCase()), displayname, Utils.color(lore), custom_model_data));
                }
            }
        }
    }
}