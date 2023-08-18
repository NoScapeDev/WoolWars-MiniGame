package net.devscape.woolwars.commands;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.utils.Utils;
import net.devscape.woolwars.utils.command.BaseCommand;
import net.devscape.woolwars.utils.command.Command;
import net.devscape.woolwars.utils.command.CommandArguments;
import org.bukkit.entity.Player;

public class GameCommand extends BaseCommand {

    private WoolWars main = WoolWars.getWoolWars();

    @Command(name = "game", permission = "game.admin", usage = "&cUsage: /game help")
    @Override
    public void executeAs(CommandArguments command) {
        Player player = command.getPlayer();

        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Utils.format(command.getCommand().getUsage()));
        } else {
            switch (args[0]) {
                case "help":
                    Utils.msgPlayer(player, "&c&l[GAME] &7Admin Commands", "&c/game setlocation <mapname> <lobby/red/blue/red-obj/blue-obj>", "&c/game savekit <kit>", "&c/game reload");

                    break;
                case "setlocation":
                    String mapName = args[1];
                    String option = args[2];

                    switch (option) {
                        case "lobby":
                            Utils.setLocation(mapName, "lobby", player);
                            Utils.msgPlayer(player, "&c&l[GAME] &7Location &f&lLOBBY &7saved.");

                            break;
                        case "red":
                            Utils.setLocation(mapName, "red", player);
                            Utils.msgPlayer(player, "&c&l[GAME] &7Location &f&lRED &7saved.");

                            break;
                        case "blue":
                            Utils.setLocation(mapName, "blue", player);
                            Utils.msgPlayer(player, "&c&l[GAME] &7Location &f&lBLUE &7saved.");

                            break;
                        case "red-obj":
                            Utils.setLocation(mapName, "red-obj", player);
                            Utils.msgPlayer(player, "&c&l[GAME] &7Location &f&lRED-OBJ &7saved.");

                            break;
                        case "blue-obj":
                            Utils.setLocation(mapName, "blue-obj", player);
                            Utils.msgPlayer(player, "&c&l[GAME] &7Location &f&lBLUE-OBJ &7saved.");

                            break;
                    }

                    break;
                case "savekit":
                    String kit = args[1];

                    this.main.getKitManager().saveKit(kit, player.getInventory().getContents());

                    Utils.msgPlayer(player, "&c&l[GAME] &7Kit &f&l" + kit + " &7saved.");

                    break;
                case "reload":
                    this.main.reload();

                    Utils.msgPlayer(player, "&c&l[GAME] &7Game reloaded.");

                    break;
            }
        }
    }
}
