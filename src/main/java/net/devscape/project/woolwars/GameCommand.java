package net.devscape.project.woolwars;

import net.devscape.project.woolwars.menus.guis.StatsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.devscape.project.woolwars.utils.Utils.*;

public class GameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {

            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("game")) {
                if (player.hasPermission("game.admin")) {
                    if (args.length == 0) {
                        msgPlayer(player, "&c&l[GAME] &7Admin Commands",
                                "&c/game setlocation <lobby/red/blue>",
                                "&c/game savekit <kit>",
                                "&c/game reload");
                    } if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            WoolWars.getWoolWars().reload();
                            msgPlayer(player, "&c&l[GAME] &7Game reloaded.");
                        } else if (args[0].equalsIgnoreCase("setlocation")) {
                            msgPlayer(player, "&c&l[GAME] &7/game setlocation <lobby/red/blue>");
                        } else if (args[0].equalsIgnoreCase("savekit")) {
                            msgPlayer(player, "&c&l[GAME] &7/game savekit <kit>");
                        }
                    } else if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("setlocation")) {
                            String option = args[1];

                            if (option.equalsIgnoreCase("lobby")) {
                                setLocation("", "lobby", player);
                                msgPlayer(player, "&c&l[GAME] &7Location &f&lLOBBY &7saved.");
                            } else if (option.equalsIgnoreCase("red")) {
                                setLocation("", "red", player);
                                msgPlayer(player, "&c&l[GAME] &7Location &f&lRED &7saved.");
                            } else if (option.equalsIgnoreCase("blue")) {
                                setLocation("", "blue", player);
                                msgPlayer(player, "&c&l[GAME] &7Location &f&lBLUE &7saved.");
                            } else if (option.equalsIgnoreCase("blue-obj")) {
                                setLocation("", "blue-obj", player);
                                msgPlayer(player, "&c&l[GAME] &7Location &f&lBLUE-OBJ &7saved.");
                            } else if (option.equalsIgnoreCase("red-obj")) {
                                setLocation("", "red-obj", player);
                                msgPlayer(player, "&c&l[GAME] &7Location &f&lRED-OBJ &7saved.");
                            }
                        } else if (args[0].equalsIgnoreCase("savekit")) {
                            String kit = args[1];

                            WoolWars.getWoolWars().getKitManager().saveKit(kit, player.getInventory().getContents());
                            msgPlayer(player, "&c&l[GAME] &7Kit &f&l" + kit + " &7saved.");
                        }
                    }
                }
            } else if (cmd.getName().equalsIgnoreCase("stats")) {
                if (args.length == 0) {
                    new StatsMenu(WoolWars.getMenuUtil(player)).open();
                }
            }
        }
        return false;
    }
}
