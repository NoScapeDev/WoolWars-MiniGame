package net.devscape.woolwars.managers.abilities.impl;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.managers.abilities.Ability;
import net.devscape.woolwars.managers.abilities.AbilityCallable;
import net.devscape.woolwars.playerdata.PlayerData;
import net.devscape.woolwars.playerdata.PlayerState;
import net.devscape.woolwars.utils.ItemBuilder;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GravityAbility extends Ability {

    private WoolWars main = WoolWars.getWoolWars();

    public GravityAbility() {
        super("Gravity", new ItemBuilder(Material.GLASS).name(Utils.format("&bGravity")).lore(Arrays.asList(
                "&7&oRight click to radiate gravity"
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

            List<Entity> nearbyEntities = new ArrayList<>(player.getNearbyEntities(5, 5, 5));

            if (nearbyEntities.isEmpty()) {
                player.sendMessage(Utils.format("&cThere are no nearby players"));

                return;
            }

            for (Entity entity : nearbyEntities) {
                if (!(entity instanceof Player)) {
                    continue;
                }

                Player target = (Player) entity;

                if (player.getName().equalsIgnoreCase(target.getName())) {
                    continue;
                }

                target.setVelocity(new Vector(target.getVelocity().getX(), target.getVelocity().getY() + 10, target.getVelocity().getZ()));
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
