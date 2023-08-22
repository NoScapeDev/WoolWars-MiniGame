package net.devscape.woolwars.commands;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.menus.guis.KitSelectorMenu;
import net.devscape.woolwars.menus.guis.StatsMenu;
import net.devscape.woolwars.utils.command.BaseCommand;
import net.devscape.woolwars.utils.command.Command;
import net.devscape.woolwars.utils.command.CommandArguments;
import org.bukkit.entity.Player;

public class KitCommands extends BaseCommand {

    private WoolWars main = WoolWars.getWoolWars();

    @Command(name = "kit", aliases = "kits")
    @Override
    public void executeAs(CommandArguments command) {
        Player player = command.getPlayer();

        String[] args = command.getArgs();

        if (args.length == 0) {
            new KitSelectorMenu(WoolWars.getMenuUtil(player)).open();
        }
    }
}