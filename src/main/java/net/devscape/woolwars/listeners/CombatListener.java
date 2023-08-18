package net.devscape.woolwars.listeners;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.handlers.GameState;
import net.devscape.woolwars.handlers.LocalData;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static net.devscape.woolwars.utils.Utils.*;

public class CombatListener implements Listener {

    private final Map<Player, Integer> teleportTasks = new HashMap<>();
    private Map<UUID, UUID> lastDamagerMap = new HashMap<>();
    public static List<UUID> respawningMap = new ArrayList<>();

    public List<UUID> diedFromFall = new ArrayList<>();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || event.getDamager() instanceof Projectile || event.getDamager() instanceof ThrownPotion)) {

            Player victim = (Player) event.getEntity();
            Player damager = null;

            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                if (projectile.getShooter() instanceof Player) {
                    damager = (Player) projectile.getShooter();
                }
            } else if (event.getDamager() instanceof ThrownPotion) {
                ThrownPotion potion = (ThrownPotion) event.getDamager();
                if (potion.getShooter() instanceof Player) {
                    damager = (Player) potion.getShooter();
                }
            }

            if (damager == null) {
                return;
            }

            Game game = WoolWars.getWoolWars().getGameManager().getGame();

            if (game.getGameState() == GameState.IN_PROGRESS) {
                if (game.getPlayers().contains(damager.getUniqueId())) {
                    event.setCancelled(true);
                    msgPlayer(damager, "&c&l[GAME] &7You can't damage players while in spectator mode!");
                }

                if (game.getBlue().contains(victim.getUniqueId()) && game.getBlue().contains(damager.getUniqueId())) {
                    event.setCancelled(true);
                    msgPlayer(damager, "&c&l[GAME] &7You can not damage your team mates!");
                }

                if (game.getRed().contains(victim.getUniqueId()) && game.getRed().contains(damager.getUniqueId())) {
                    event.setCancelled(true);
                    msgPlayer(damager, "&c&l[GAME] &7You can not damage your team mates!");
                }

                if (game.getPlayers().contains(victim.getUniqueId())) {
                    event.setCancelled(true);
                }

                UUID lastDamagerId = lastDamagerMap.get(victim.getUniqueId());
                if (lastDamagerId == null || !lastDamagerId.equals(damager.getUniqueId())) {
                    lastDamagerMap.put(victim.getUniqueId(), damager.getUniqueId());
                }

                double dmg = event.getDamage();

                if (dmg >= victim.getHealth()) {
                    LocalData damagerLocalData = WoolWars.getWoolWars().getLocalStatsManager().getData(damager.getUniqueId());
                    damagerLocalData.setKills(damagerLocalData.getKills() + 1);

                    LocalData victimLocalData = WoolWars.getWoolWars().getLocalStatsManager().getData(victim.getUniqueId());
                    victimLocalData.setDeaths(victimLocalData.getDeaths() + 1);

                    event.setCancelled(true);
                    soundPlayer(damager, Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
                    soundPlayer(victim, Sound.ENTITY_PLAYER_DEATH, 2, 2);

                    WoolWars.getWoolWars().getH2Data().addKills(damager.getUniqueId(), 1);
                    WoolWars.getWoolWars().getH2Data().addDeaths(victim.getUniqueId(), 1);

                    announceKill(damager, victim);
                    respawnPlayer(victim);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    private void respawnPlayer(Player player) {
        Game game = WoolWars.getWoolWars().getGameManager().getGame();
        Location teamSpawnLocation = null;

        if (game.getRed().contains(player.getUniqueId())) {
            teamSpawnLocation = game.getRedSpawn();
        } else if (game.getBlue().contains(player.getUniqueId())) {
            teamSpawnLocation = game.getBlueSpawn();
        }

        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);

        player.setHealth(20);
        player.setFoodLevel(20);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        titlePlayer(player, "&c&lDIED", "&7Respawning in 3 seconds.", 0, 20 * 4, 0);

        lastDamagerMap.remove(player.getUniqueId());
        respawningMap.add(player.getUniqueId());

        Location finalTeamSpawnLocation = teamSpawnLocation;
        int teleportTaskId = Bukkit.getScheduler().runTaskLater(WoolWars.getWoolWars(), () -> {
            if (finalTeamSpawnLocation != null) {
                player.teleport(finalTeamSpawnLocation);
                player.setGameMode(GameMode.SURVIVAL);

                respawningMap.remove(player.getUniqueId());
                diedFromFall.remove(player.getUniqueId());

                WoolWars.getWoolWars().getKitManager().applyKit(player, WoolWars.getWoolWars().getSelectedKit(player));
            }
        }, 3 * 20).getTaskId();

        teleportTasks.put(player, teleportTaskId);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Game game = WoolWars.getWoolWars().getGameManager().getGame();
        Location to = event.getTo();

        if (to != null && to.getY() < -1) {
            if (game.getGameState() == GameState.IN_PROGRESS) {

                if (!diedFromFall.contains(player.getUniqueId())) {
                    if (game.getPlayers().contains(player.getUniqueId())) {
                        event.setTo(game.getLobbyLoc());
                        player.setGameMode(GameMode.SURVIVAL);

                        giveWaitingItems(player);

                        player.setAllowFlight(true);
                        player.setFlying(true);
                        return;
                    }

                    if (respawningMap.contains(player.getUniqueId())) {
                        return;
                    }

                    Location teamSpawnLocation = null;
                    if (game.getRed().contains(player.getUniqueId())) {
                        teamSpawnLocation = game.getRedSpawn();
                    } else if (game.getBlue().contains(player.getUniqueId())) {
                        teamSpawnLocation = game.getBlueSpawn();
                    }

                    // Clear existing teleport tasks before scheduling a new one
                    cancelTeleportTask(player);

                    UUID lastDamagerId = lastDamagerMap.get(player.getUniqueId());
                    if (lastDamagerId != null) {
                        Player lastDamager = Bukkit.getPlayer(lastDamagerId);
                        if (lastDamager != null) {
                            LocalData damagerLocalData = WoolWars.getWoolWars().getLocalStatsManager().getData(lastDamager.getUniqueId());
                            damagerLocalData.setKills(damagerLocalData.getKills() + 1);

                            WoolWars.getWoolWars().getH2Data().addKills(lastDamager.getUniqueId(), 1);
                            announceKill(lastDamager, player);
                        }
                    } else {
                        LocalData victimLocalData = WoolWars.getWoolWars().getLocalStatsManager().getData(player.getUniqueId());
                        victimLocalData.setDeaths(victimLocalData.getDeaths() + 1);

                        Bukkit.broadcastMessage(format("&c&l[GAME] " + getTeamColor(game.getTeam(player)) + player.getName() + " &7fell in the void."));
                    }

                    Location finalTeamSpawnLocation = teamSpawnLocation;

                    player.getInventory().clear();

                    player.setGameMode(GameMode.SPECTATOR);

                    player.setHealth(20);
                    player.setFoodLevel(20);

                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }

                    if (finalTeamSpawnLocation != null) {
                        player.teleport(finalTeamSpawnLocation);
                    }

                    WoolWars.getWoolWars().getH2Data().addDeaths(player.getUniqueId(), 1);

                    respawningMap.add(player.getUniqueId());
                    lastDamagerMap.remove(player.getUniqueId());

                    titlePlayer(player, "&c&lDIED", "&7Respawning in 3 seconds.", 0, 20 * 4, 0);

                    int teleportTaskId = Bukkit.getScheduler().runTaskLater(WoolWars.getWoolWars(), () -> {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (finalTeamSpawnLocation != null) {
                                    player.teleport(finalTeamSpawnLocation);
                                }

                                player.setGameMode(GameMode.SURVIVAL);
                                respawningMap.remove(player.getUniqueId());

                                WoolWars.getWoolWars().getKitManager().applyKit(player, WoolWars.getWoolWars().getSelectedKit(player));
                            }
                        }.runTaskLater(WoolWars.getWoolWars(), 3 * 20);
                    }, 20L).getTaskId();

                    // Store the task ID for cancellation if needed
                    teleportTasks.put(player, teleportTaskId);
                }
            } else {
                event.setTo(game.getLobbyLoc());
                player.setGameMode(GameMode.SURVIVAL);

                giveWaitingItems(player);
            }

            // Prevent fall damage during teleportation
            if (player.getFallDistance() > 0.0F) {
                EntityDamageEvent damageEvent = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, player.getFallDistance());
                Bukkit.getPluginManager().callEvent(damageEvent);
                if (!damageEvent.isCancelled()) {
                    player.setFallDistance(0.0F);
                }
            }
        }
    }

    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            Game game = WoolWars.getWoolWars().getGameManager().getGame();

            if (game.getGameState() == GameState.IN_PROGRESS) {
                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    double dmg = e.getDamage();
                    if (dmg >= victim.getHealth()) {
                        e.setCancelled(true);
                        diedFromFall.add(victim.getUniqueId());

                        if (diedFromFall.contains(victim.getUniqueId())) {
                            UUID lastDamagerId = lastDamagerMap.get(victim.getUniqueId());
                            if (lastDamagerId != null) {
                                Player damager = Bukkit.getPlayer(lastDamagerId);
                                if (damager != null) {
                                    soundPlayer(damager, Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
                                    WoolWars.getWoolWars().getH2Data().addKills(damager.getUniqueId(), 1);
                                    announceKill(damager, victim);
                                }
                            } else {
                                Bukkit.broadcastMessage(format("&c&l[GAME] " + getTeamColor(game.getTeam(victim)) + victim.getName() + " &7died from falling."));
                            }

                            soundPlayer(victim, Sound.ENTITY_PLAYER_DEATH, 2, 2);
                            WoolWars.getWoolWars().getH2Data().addDeaths(victim.getUniqueId(), 1);
                            respawnPlayer(victim);
                        }
                    }
                }
            }
        }
    }

    private void cancelTeleportTask(Player player) {
        if (teleportTasks.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(teleportTasks.get(player));
            teleportTasks.remove(player);
        }
    }

    private void announceKill(Player killer, Player victim) {
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        if (game.getRed().contains(killer.getUniqueId()) && game.getBlue().contains(victim.getUniqueId())) {
            Bukkit.broadcastMessage(format("&c&l[GAME] " + getTeamColor("red") + killer.getName() + " &7killed " + getTeamColor("blue") + victim.getName() + "&7!"));
        } else if (game.getBlue().contains(killer.getUniqueId()) && game.getRed().contains(victim.getUniqueId())) {
            Bukkit.broadcastMessage(format("&c&l[GAME] " + getTeamColor("blue") + killer.getName() + " &7killed " + getTeamColor("red") + victim.getName() + "&7!"));
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Firework) {
            event.setCancelled(true); // Cancel the explosion caused by the firework
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            event.setCancelled(true); // Cancel any damage caused by a firework
        }
    }
}