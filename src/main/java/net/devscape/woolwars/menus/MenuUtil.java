package net.devscape.woolwars.menus;

import org.bukkit.entity.Player;

public class MenuUtil {

    private final Player owner;

    public MenuUtil(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }
}
