package net.devscape.project.woolwars.utils.command;

import net.devscape.project.woolwars.WoolWars;

public abstract class BaseCommand {

    public BaseCommand() {
        WoolWars.getWoolWars().getCommandFramework().registerCommands(this, null);
    }

    public abstract void executeAs(CommandArguments command);
}
