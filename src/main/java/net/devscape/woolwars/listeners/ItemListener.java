package net.devscape.woolwars.listeners;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.handlers.GameState;
import net.devscape.woolwars.managers.abilities.Ability;
import net.devscape.woolwars.menus.guis.KitSelectorMenu;
import net.devscape.woolwars.menus.guis.MapSelectorMenu;
import net.devscape.woolwars.menus.guis.SpectatorMenu;
import net.devscape.woolwars.menus.guis.TeamSelectorMenu;
import net.devscape.woolwars.playerdata.PlayerData;
import net.devscape.woolwars.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static net.devscape.woolwars.utils.Utils.format;
import static net.devscape.woolwars.utils.Utils.msgPlayer;

public class ItemListener implements Listener {

    private WoolWars main = WoolWars.getWoolWars();

    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        if (item != null && item.getItemMeta() != null) {
            String displayName = item.getItemMeta().getDisplayName();

            if (displayName.equalsIgnoreCase(format(WoolWars.getWoolWars().getConfig().getString("items.team-selector.displayname")))) {
                if (game.getGameState() == GameState.IN_PROGRESS && !game.getPlayers().contains(player.getUniqueId())) {
                    msgPlayer(player, "&f侵 &7This can't be used whilst in-game.");
                    return;
                }

                new TeamSelectorMenu(WoolWars.getMenuUtil(player)).open();
            }

            if (displayName.equalsIgnoreCase(format(WoolWars.getWoolWars().getConfig().getString("items.map-selector.displayname")))) {
                if (game.getGameState() == GameState.IN_PROGRESS) {
                    msgPlayer(player, "&f侵 &7Map selecting can't be used at this time.");
                    return;
                }

                new MapSelectorMenu(WoolWars.getMenuUtil(player)).open();
            }

            if (displayName.equalsIgnoreCase(format(WoolWars.getWoolWars().getConfig().getString("items.kit-selector.displayname")))) {
                if (game.getGameState() == GameState.IN_PROGRESS && !game.getPlayers().contains(player.getUniqueId())) {
                    msgPlayer(player, "&f侵 &7This can't be used whilst in-game.");
                    return;
                }

                new KitSelectorMenu(WoolWars.getMenuUtil(player)).open();
            }

            if (displayName.equalsIgnoreCase(format(WoolWars.getWoolWars().getConfig().getString("items.spectator-selector.displayname")))) {
                if (game.getGameState() == GameState.IN_PROGRESS && !game.getPlayers().contains(player.getUniqueId())) {
                    msgPlayer(player, "&f侵 &7This can't be used whilst in-game.");
                    return;
                }

                if (game.getGameState() == GameState.IN_PROGRESS) {
                    new SpectatorMenu(WoolWars.getMenuUtil(player)).open();
                } else {
                    msgPlayer(player, "&f侵 &7This can't be used in waiting state.");
                }
            }

            if (displayName.equalsIgnoreCase(format(WoolWars.getWoolWars().getConfig().getString("items.back-to-hub.displayname")))) {
                if (game.getGameState() == GameState.IN_PROGRESS && !game.getPlayers().contains(player.getUniqueId())) {
                    msgPlayer(player, "&f侵 &7This can't be used whilst in-game.");
                    return;
                }

                WoolWars.getWoolWars().getBungeeUtils().sendPlayerToServer(player, "hub");
            }

            if (!game.getPlayers().contains(player.getUniqueId()) && game.getGameState() == GameState.IN_PROGRESS) {
                for (Ability ability : this.main.getAbilityManager().getAbilities()) {
                    if (!displayName.equalsIgnoreCase(ability.getItemStack().getItemMeta().getDisplayName())) {
                        continue;
                    }

                    switch (ability.getName()) {
                        case "Shuriken":
                            this.main.getAbilityManager().getAbility("Shuriken").getAbilityCallable().executeAs(player);

                            break;
                        case "Eskimo":
                            this.main.getAbilityManager().getAbility("Eskimo").getAbilityCallable().executeAs(player);

                            break;
                        case "Gravity":
                            this.main.getAbilityManager().getAbility("Gravity").getAbilityCallable().executeAs(player);

                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Player playerCaught;

        Game game = WoolWars.getWoolWars().getGameManager().getGame();
        PlayerData playerData = this.main.getPlayerDataManager().getPlayerData(player.getUniqueId());

        ItemStack item = player.getItemInHand();

        if (item != null && item.getItemMeta() != null) {
            String displayName = item.getItemMeta().getDisplayName();

            for (Ability ability : this.main.getAbilityManager().getAbilities()) {
                if (!displayName.equalsIgnoreCase(ability.getItemStack().getItemMeta().getDisplayName())) {
                    continue;
                }

                switch (ability.getName()) {
                    case "Grappler":
                        if (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT || event.getState() == PlayerFishEvent.State.IN_GROUND) {
                            Location hook = event.getHook().getLocation();
                            hook.getBlock().getLocation().setY(hook.getBlock().getLocation().getY() - 1.0D);
                            Location hookLocation = null;
                            int check = 999;
                            for (int x = -1; x <= 1; x++) {
                                for (int y = -1; y <= 1; y++) {
                                    for (int z = -1; z <= 1; z++) {
                                        Location to = event.getHook().getLocation().clone().add(x, y, z);
                                        int calculate = x * x + y * y + z * z;
                                        if (to.getBlock().getType() == Material.WATER || to
                                                .getBlock().getType() == Material.LEGACY_STATIONARY_WATER || to
                                                .getBlock().getType() == Material.LAVA || to
                                                .getBlock().getType() == Material.LEGACY_STATIONARY_LAVA)
                                            return;
                                        if (calculate < check) {
                                            hookLocation = to;
                                            check = calculate;
                                        }
                                    }
                                }
                            }
                            if (hookLocation == null)
                                return;
                            this.pullPlayerToLocation(player, hookLocation, 1.5D, false);
                            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 4.0F, 4.0F);
                            this.main.getAbilityManager().getAbility("Grappler").getAbilityCallable().executeAs(player);
                        }

                        break;
                    case "Fisherman":
                        if (!(event.getCaught() instanceof Player))
                            return;
                        playerCaught = (Player) event.getCaught();
                        if (playerCaught.equals(player))
                            return;

                        if (game.getTeam(player).equalsIgnoreCase(game.getTeam(playerCaught)))
                            return;

                        playerCaught.teleport(player.getLocation());
                        playerCaught.getWorld().playSound(playerCaught.getLocation(), Sound.ENTITY_CAT_PURR, 20.0F, 10.0F);
                        this.main.getAbilityManager().getAbility("Fisherman").getAbilityCallable().executeAs(player);

                        break;
                }
            }
        }
    }

    public void pullPlayerToLocation(Player e, Location loc, double multiplier, boolean isPowerful) {
        Location entityLoc = e.getLocation();
        double g = -0.08D;
        double d = loc.distance(entityLoc);
        double v_x = (1.0D + 0.07D * d) * (loc.getX() - entityLoc.getX()) / d;
        double v_y = (1.0D + 0.03D * d) * (loc.getY() - entityLoc.getY()) / d - 0.5D * g * d;
        double v_z = (1.0D + 0.07D * d) * (loc.getZ() - entityLoc.getZ()) / d;
        Vector v = new Vector(v_x * 0.75D, v_y, v_z * 0.75D);
        v.add(new Vector(0.0D, 0.05D, 0.0D));
        if (e.isOnGround() || WorldUtils.isEmptyColumn(e.getLocation()))
            e.teleport(e.getLocation().clone().add(0.0D, 0.5D, 0.0D));
        v = v.multiply(multiplier);
        v_y = v.getY();
        v = v.multiply(multiplier);
        v.setY(v_y);
        e.setVelocity(v);
    }
}