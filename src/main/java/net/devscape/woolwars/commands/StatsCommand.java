package net.devscape.woolwars.commands;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.menus.guis.StatsMenu;
import net.devscape.woolwars.utils.command.BaseCommand;
import net.devscape.woolwars.utils.command.Command;
import net.devscape.woolwars.utils.command.CommandArguments;
import org.bukkit.entity.Player;

public class StatsCommand extends BaseCommand {

    private WoolWars main = WoolWars.getWoolWars();

    @Command(name = "stats")
    @Override
    public void executeAs(CommandArguments command) {
        Player player = command.getPlayer();

        String[] args = command.getArgs();

        if (args.length == 0) {
            new StatsMenu(WoolWars.getMenuUtil(player)).open();
        }
    }
}
