package net.devscape.woolwars.managers.abilities.impl;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.managers.abilities.Ability;
import net.devscape.woolwars.managers.abilities.AbilityCallable;
import net.devscape.woolwars.playerdata.PlayerData;
import net.devscape.woolwars.playerdata.PlayerState;
import net.devscape.woolwars.utils.ItemBuilder;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.Material;
import java.util.Arrays;

public class FishermanAbility extends Ability {

    private WoolWars main = WoolWars.getWoolWars();

    public FishermanAbility() {
        super("Fisherman", new ItemBuilder(Material.FISHING_ROD).name("&bFishing rod").lore(Arrays.asList(
                "&7&oRight click to throw a hook",
                "&7&othat pulls your enemies",
                "&7&oto you"
        )).addUnbreakable().build());
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

            this.main.getCooldownManager().setCooldownSet(player, true, this.getCooldown());
            this.main.getCooldownManager().setCooldownTime(player, this.getCooldown());
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }
}
