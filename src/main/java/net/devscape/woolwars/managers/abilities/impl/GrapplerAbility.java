package net.devscape.woolwars.managers.abilities.impl;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.managers.abilities.Ability;
import net.devscape.woolwars.managers.abilities.AbilityCallable;
import net.devscape.woolwars.playerdata.PlayerData;
import net.devscape.woolwars.playerdata.PlayerState;
import net.devscape.woolwars.utils.ItemBuilder;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class GrapplerAbility extends Ability {

    private WoolWars main = WoolWars.getWoolWars();

    public GrapplerAbility() {
        super("Grappler", new ItemBuilder(Material.FISHING_ROD).name("&bGrappler").lore(Arrays.asList(
                "&7&oRight click to throw a hook",
                "&7&othat pulls you to it"
        )).build());
    }

    @Override
    public AbilityCallable getAbilityCallable() {
        return player -> {
            PlayerData playerData = this.main.getPlayerDataManager().getPlayerData(player.getUniqueId());

            if (playerData.getPlayerState() == PlayerState.SPAWN) {
                return;
            }

            if (this.main.getCooldownManager().isOnCooldown(player)) {
                player.sendMessage(Utils.format("&cYou have to wait &c&l" + this.main.getCooldownManager().getCooldownTime(player) + " seconds &cto use the ability again"));

                return;
            }

            this.main.getCombatManager().cancelNextFallDamage(player, 100);
            this.main.getCooldownManager().setCooldownSet(player, true, this.getCooldown());
            this.main.getCooldownManager().setCooldownTime(player, this.getCooldown());
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }
}
