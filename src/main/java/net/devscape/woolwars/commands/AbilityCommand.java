package net.devscape.woolwars.commands;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.managers.abilities.Ability;
import net.devscape.woolwars.menus.guis.StatsMenu;
import net.devscape.woolwars.utils.Utils;
import net.devscape.woolwars.utils.command.BaseCommand;
import net.devscape.woolwars.utils.command.Command;
import net.devscape.woolwars.utils.command.CommandArguments;
import org.bukkit.entity.Player;

public class AbilityCommand extends BaseCommand {

    private WoolWars main = WoolWars.getWoolWars();

    @Command(name = "ability", permission = "ability.admin", usage = "&cUsage: /ability help")
    @Override
    public void executeAs(CommandArguments command) {
        Player player = command.getPlayer();

        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Utils.format(command.getCommand().getUsage()));
        } else {
            switch (args[0]) {
                case "help":
                    player.sendMessage(Utils.format("&cUsage:"));
                    player.sendMessage(Utils.format("  &c/ability help"));
                    player.sendMessage(Utils.format("  &c/ability give <player> <ability>"));
                    
                    break;
                case "give":
                    Player target = this.main.getServer().getPlayer(args[1]);

                    if (target == null) {
                        return;
                    }

                    String abilityName = args[2];

                    Ability ability = this.main.getAbilityManager().getAbility(abilityName);

                    if (ability == null) {
                        return;
                    }

                    target.getInventory().addItem(ability.getItemStack());
                    player.sendMessage(Utils.format("&aYou've given an ability to &a&l" + target.getName()));

                    break;
            }
        }
    }
}
