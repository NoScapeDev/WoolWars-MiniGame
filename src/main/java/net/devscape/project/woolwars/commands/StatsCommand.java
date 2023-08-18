package net.devscape.project.woolwars.commands;

import net.devscape.project.woolwars.WoolWars;
import net.devscape.project.woolwars.menus.guis.StatsMenu;
import net.devscape.project.woolwars.utils.Utils;
import net.devscape.project.woolwars.utils.command.BaseCommand;
import net.devscape.project.woolwars.utils.command.Command;
import net.devscape.project.woolwars.utils.command.CommandArguments;
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
