package net.devscape.woolwars.listeners;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.handlers.GameState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.devscape.woolwars.utils.Utils.*;

public class GameListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        WoolWars.getWoolWars().getH2Data().createPlayer(e.getPlayer());

        WoolWars.getWoolWars().getGameManager().getGame().include(e);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        WoolWars.getWoolWars().getGameManager().getGame().exclude(e);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        for (String str : WoolWars.getWoolWars().getConfig().getStringList("blocked-commands")) {
            if (e.getMessage().equalsIgnoreCase(str)) {
                e.setCancelled(true);
                msgPlayer(player, "&c✘ &7This command is now allowed in here!");
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        Block block = e.getBlock();

        if (game.getGameState() == GameState.IN_PROGRESS) {
            if (player.getLocation().getY() >= 50) {
                e.setCancelled(true);
                msgPlayer(player, "&c&l[GAME] &7You have reached the game build limit.");
                return;
            }

            if (game.getPlayers().contains(player.getUniqueId())) {
                e.setCancelled(true);
                msgPlayer(player, "&c&l[GAME] &7You cannot build whilst in spectator mode.");
                return;
            }

            if (block.getLocation().distance(game.getBlueSpawn()) < 5) {
                e.setCancelled(true);
                msgPlayer(player, "&c&l[GAME] &7You cannot build next to team spawns.");
                return;
            }

            if (block.getLocation().distance(game.getRedSpawn()) < 5) {
                e.setCancelled(true);
                msgPlayer(player, "&c&l[GAME] &7You cannot build next to team spawns.");
                return;
            }

            if (block.getLocation().distance(game.getBlueObjective()) < 6) {
                e.setCancelled(true);
                msgPlayer(player, "&c&l[GAME] &7You cannot build next to team objectives.");
                return;
            }

            if (block.getLocation().distance(game.getRedObjective()) < 6) {
                e.setCancelled(true);
                msgPlayer(player, "&c&l[GAME] &7You cannot build next to team objectives.");
                return;
            }

            game.getBlockMap().add(e.getBlock());
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        Block block = e.getBlock();

        if (game.getGameState() == GameState.IN_PROGRESS) {

            if (game.getPlayers().contains(player.getUniqueId())) {
                e.setCancelled(true);
                msgPlayer(player, "&c&l[GAME] &7You cannot break whilst in spectator mode.");
                return;
            }

            if (game.getBlockMap().contains(block)) {
                e.setCancelled(false);
                game.getBlockMap().remove(block);
                return;
            }

            if (block.getLocation().distance(game.getBlueSpawn()) < 5) {
                e.setCancelled(true);
                msgPlayer(player, "&c&l[GAME] &7You cannot break next to team spawns.");
                return;
            }

            if (block.getLocation().distance(game.getRedSpawn()) < 5) {
                e.setCancelled(true);
                msgPlayer(player, "&c&l[GAME] &7You cannot break next to team spawns.");
                return;
            }

            if (block.getType() != Material.BLUE_WOOL) {
                if (block.getLocation().distance(game.getBlueObjective()) < 6) {
                    e.setCancelled(true);
                    msgPlayer(player, "&c&l[GAME] &7You cannot break next to team objectives.");
                    return;
                }
            }

            if (block.getType() != Material.RED_WOOL) {
                if (block.getLocation().distance(game.getRedObjective()) < 6) {
                    e.setCancelled(true);
                    msgPlayer(player, "&c&l[GAME] &7You cannot break next to team objectives.");
                    return;
                }
            }

            if (game.getBlue().contains(player.getUniqueId())) {
                if (e.getBlock().getType().equals(Material.BLUE_WOOL)) {
                    e.setCancelled(true);
                } else if (e.getBlock().getType().equals(Material.RED_WOOL)) {
                    e.setCancelled(false);
                    e.setDropItems(false);
                    game.removeScore("red");
                    if (!game.getBlockMap().contains(block)) {
                        game.getRedMap().add(block);
                    }

                    WoolWars.getWoolWars().getH2Data().addWoolBroken(player.getUniqueId(), 1);
                    Bukkit.broadcastMessage(format("&a&l[GAME] " + getTeamColor("blue") + player.getName() + " &f&lbroke" + getTeamColor("red") + " Red's Wool"));

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        soundPlayer(all, Sound.BLOCK_AMETHYST_CLUSTER_HIT, 2, 2);
                    }

                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1.0f);
                    block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation(), 1, 0, 0, 0, 0, dustOptions);

                    game.checkGameWin();
                }
            } else if (game.getRed().contains(player.getUniqueId())) {
                if (e.getBlock().getType().equals(Material.BLUE_WOOL)) {
                    e.setCancelled(false);
                    e.setDropItems(false);
                    if (!game.getBlockMap().contains(block)) {
                        game.getBlueMap().add(block);
                    }

                    for (Player all : Bukkit.getOnlinePlayers()) {
                        soundPlayer(all, Sound.BLOCK_AMETHYST_CLUSTER_HIT, 2, 2);
                    }

                    game.removeScore("blue");

                    WoolWars.getWoolWars().getH2Data().addWoolBroken(player.getUniqueId(), 1);
                    Bukkit.broadcastMessage(format("&a&l[GAME] " + getTeamColor("red") + player.getName() + " &f&lbroke" + getTeamColor("blue") + " Blue's Wool"));

                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.BLUE, 1.0f);
                    block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation(), 1, 0, 0, 0, 0, dustOptions);

                    game.checkGameWin();
                } else if (e.getBlock().getType().equals(Material.RED_WOOL)) {
                    e.setCancelled(true);
                }
            } else if (game.getPlayers().contains(player.getUniqueId())) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventory(PlayerDropItemEvent e) {
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        if (game.getGameState() != GameState.IN_PROGRESS) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent e) {
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        if (game.getGameState() != GameState.IN_PROGRESS) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryDragEvent e) {
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        if (game.getGameState() != GameState.IN_PROGRESS) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();

            arrow.setTicksLived(arrow.getTicksLived() + 6000);
        }
    }
}