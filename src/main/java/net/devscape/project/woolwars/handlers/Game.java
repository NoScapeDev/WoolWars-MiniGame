package net.devscape.project.woolwars.handlers;

import net.devscape.project.woolwars.WoolWars;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.devscape.project.woolwars.listeners.CombatListener.respawningMap;
import static net.devscape.project.woolwars.utils.Utils.*;

public class Game {

    private final String map_name;

    /// teams & spectators
    private final List<UUID> blue;
    private final List<UUID> red;
    private final List<UUID> players;

    private GameState gameState = GameState.WAITING;

    private final int max_players;

    private int game_time = 1800; // in seconds

    private int countdown = 15;
    private boolean countdownStarted = false;

    private int blue_wool = 100;
    private int red_wool = 100;

    private List<Block> blockMap = new ArrayList<>();

    private final List<Block> redBlockMap = new ArrayList<>();
    private final List<Block> blueBlockMap = new ArrayList<>();

    private String winners;

    /// map locations
    private final Location lobby_loc;
    private final Location blue_spawn;
    private final Location red_spawn;
    private Location red_objective;
    private Location blue_objective;

    private BossBar bossBar;

    private boolean active_map;

    public Game(String mapName, List<UUID> blue, List<UUID> red, List<UUID> players, GameState gameState, int maxPlayers, Location lobbyLoc, Location blueSpawn, Location redSpawn, Location redObjective, Location blueObjective, boolean activeMap) {
        map_name = mapName;
        this.blue = blue;
        this.red = red;
        this.players = players;
        this.gameState = gameState;
        max_players = maxPlayers;
        lobby_loc = lobbyLoc;
        blue_spawn = blueSpawn;
        red_spawn = redSpawn;
        red_objective = redObjective;
        blue_objective = blueObjective;
        active_map = activeMap;
    }

    public Game(String mapName, int maxPlayers) {
        map_name = mapName;
        this.blue = new ArrayList<>();
        this.red = new ArrayList<>();
        this.players = new ArrayList<>();
        max_players = maxPlayers;
        lobby_loc = null;
        blue_spawn = null;
        red_spawn = null;
    }

    public String getMapName() {
        return map_name;
    }

    public List<UUID> getBlue() {
        return blue;
    }

    public List<UUID> getRed() {
        return red;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getMax_players() {
        return max_players;
    }

    public Location getLobbyLoc() {
        return lobby_loc;
    }

    public Location getBlueSpawn() {
        return blue_spawn;
    }

    public Location getRedSpawn() {
        return red_spawn;
    }

    public List<UUID> getPlayerTeam(Player player) {

        if (blue.contains(player.getUniqueId())) {
            return blue;
        } else if (red.contains(player.getUniqueId())) {
            return red;
        }

        return players;
    }

    private String formatGameTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public void updateBossBar() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (bossBar == null) {
                    String redBar = createProgressBar(red_wool);
                    String blueBar = createProgressBar(blue_wool);

                    bossBar = Bukkit.createBossBar(
                            format("&#FF3A3A&lRED &#FF3A3A" + redBar + " &f│ &f&lLOADING..f│ &#0051FF&lBLUE &#0051FF" + blueBar + ""),
                            BarColor.BLUE,
                            BarStyle.SOLID,
                            BarFlag.DARKEN_SKY);
                }

                if (gameState == GameState.WAITING) {
                    if (blue.size() == 0 && red.size() == 0) {
                        bossBar.setTitle(format("&f│ &7Select a Team&f│"));
                    } else if (blue.size() == 1 && red.size() == 0) {
                        bossBar.setTitle(format("&f│ &7Waiting for other players&f│"));
                    } else if (blue.size() == 0 && red.size() == 1) {
                        bossBar.setTitle(format("&f│ &7Waiting for other players&f│"));
                    } else {
                        bossBar.setTitle(format("&f│ &7Select a Team&f│"));
                    }
                }

                if (gameState == GameState.STARTING) {
                    int secondsLeft = countdown;
                    String redBar = createProgressBar(red_wool);
                    String blueBar = createProgressBar(blue_wool);
                    bossBar.setTitle(format("&#FF3A3A&lRED &#FF3A3A" + redBar + " &f│ &f" + secondsLeft + "&f│ &#0051FF&lBLUE &#0051FF" + blueBar + ""));
                }

                if (gameState == GameState.IN_PROGRESS) {
                    String redBar = createProgressBar(red_wool);
                    String blueBar = createProgressBar(blue_wool);

                    bossBar.setTitle(format("&#FF3A3A" + redBar + " &7" + red_wool + "% &f│ &f" + formatGameTime(game_time) + "&f│ &#0051FF" + blueBar + " &7" + blue_wool + "%"));

                    if (game_time <= 0) {
                        endGame();
                    } else {
                        game_time--;
                    }

                    if (blue.size() == 0) {
                        endGame();
                    }

                    if (red.size() == 0) {
                        endGame();
                    }
                }

                if (gameState == GameState.CHANGING_PROCESS) {
                    bossBar.setTitle(format("&f│ &f&lRESETTING GAME..&f│"));
                }
            }
        }.runTaskTimerAsynchronously(WoolWars.getWoolWars(), 0, 20L);
    }

    private String createProgressBar(int progress) {
        int width = 10; // Fixed width of the progress bar
        int symbols = Math.min(progress / 10, width); // Calculate the number of symbols to display
        int remaining = width - symbols;

        StringBuilder progressBar = new StringBuilder();

        for (int i = 0; i < symbols; i++) {
            progressBar.append("▒"); // Use a smaller Unicode character that represents completed progress
        }

        for (int i = 0; i < remaining; i++) {
            progressBar.append(format("&7░")); // Use a character that represents remaining progress
        }

        return progressBar.toString();
    }

    public void removeScore(String team) {
        if (team.equalsIgnoreCase("blue")) {
            blue_wool -= 10;
        } else if (team.equalsIgnoreCase("red")) {
            red_wool -= 10;
        }
    }

    public void include(PlayerJoinEvent e) {
        // add to game/lobby on join
        Player player = e.getPlayer();

        getPlayers().add(player.getUniqueId());

        assert lobby_loc != null;
        player.teleport(lobby_loc);

        player.getInventory().clear();

        giveWaitingItems(player);

        player.setAllowFlight(true);
        player.setFlying(true);

        titlePlayer(player, "&b&lWoolWars", "&eJoined the game!", 20, 20 * 2, 20);
        soundPlayer(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        bossBar.addPlayer(player);

        if (gameState == GameState.IN_PROGRESS) {
            for (UUID blueuuid : getBlue()) {
                Player blue = Bukkit.getPlayer(blueuuid);
                if (blue == null) return;

                assert player != null;
                blue.hidePlayer(WoolWars.getWoolWars(), player);
            }

            for (UUID reduuid : getRed()) {
                Player red = Bukkit.getPlayer(reduuid);
                if (red == null) return;

                assert player != null;
                red.hidePlayer(WoolWars.getWoolWars(), player);
            }
        }

        e.setJoinMessage(format("&a&l[GAME] &7" + player.getName() + " &ejoined woolwars!"));
    }

    public void exclude(PlayerQuitEvent e) {
        // remove from game on leave
        Player player = e.getPlayer();

        getPlayerTeam(player).remove(player.getUniqueId());
        getPlayers().remove(player.getUniqueId());

        bossBar.removePlayer(player);
        e.setQuitMessage(format("&c&l[GAME] &7" + player.getName() + " &eleft woolwars!"));
    }

    public void kick(Player player) {
        // kick function back to hub etc.

        bossBar.removePlayer(player);
        getPlayerTeam(player).remove(player.getUniqueId());
        getPlayers().remove(player.getUniqueId());
        player.kickPlayer("Redirected back to the hub!");
    }

    public void startCountdown() {
        countdownStarted = true;
        gameState = GameState.STARTING;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (blue.size() == 0 || red.size() == 0) {
                    Bukkit.broadcastMessage(format("&c&l[GAME] &7Not enough players to start.."));
                    countdownStarted = false;
                    gameState = GameState.WAITING;
                    countdown = 15;
                    cancel();
                }

                if (countdown <= 0) {
                    start();
                    countdownStarted = false;
                    countdown = 15;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.getWorld().playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 2);
                    }
                    cancel();
                }

                if (countdown > 1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 2, 2);
                    }
                }

                countdown--;
            }
        }.runTaskTimer(WoolWars.getWoolWars(), 0, 20L);
    }

    public boolean isCountdownStarted() {
        return countdownStarted;
    }

    public void start() {
        gameState = GameState.IN_PROGRESS;
        Bukkit.broadcastMessage(format("&a&l[GAME] &7The game has started! FIGHT...."));

        for (UUID uuid : getRed()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;

            player.setAllowFlight(false);
            player.setFlying(false);

            assert red_spawn != null;
            player.teleport(red_spawn);

            player.getInventory().clear();
            WoolWars.getWoolWars().getKitManager().applyKit(player, WoolWars.getWoolWars().getSelectedKit(player));
        }

        for (UUID uuid : getBlue()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;

            player.setAllowFlight(false);
            player.setFlying(false);

            assert blue_spawn != null;
            player.teleport(blue_spawn);

            player.getInventory().clear();
            WoolWars.getWoolWars().getKitManager().applyKit(player, WoolWars.getWoolWars().getSelectedKit(player));
        }

        for (UUID uuid : getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            for (UUID blueuuid : getBlue()) {
                Player blue = Bukkit.getPlayer(blueuuid);
                if (blue == null) return;

                assert player != null;
                blue.hidePlayer(WoolWars.getWoolWars(), player);
            }

            for (UUID reduuid : getRed()) {
                Player red = Bukkit.getPlayer(reduuid);
                if (red == null) return;

                assert player != null;
                red.hidePlayer(WoolWars.getWoolWars(), player);
            }
        }
    }

    public void spawnFirework(Location location, org.bukkit.Color color) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect fireworkEffect = FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(color)
                .trail(true)
                .build();

        fireworkMeta.addEffect(fireworkEffect);
        fireworkMeta.setPower(1); // Set the power of the firework

        firework.setFireworkMeta(fireworkMeta);
    }

    public void endGame() {
        gameState = GameState.CHANGING_PROCESS;

        for (UUID uuid : getBlue()) {
            getPlayers().add(uuid);

            if (winners != null && winners.equalsIgnoreCase("blue")) {
                Player player = Bukkit.getPlayer(uuid);

                Bukkit.getScheduler().runTask(WoolWars.getWoolWars(), () -> {
                    spawnFirework(player.getLocation(), org.bukkit.Color.BLUE);
                    Bukkit.getScheduler().runTaskLater(WoolWars.getWoolWars(), () -> {
                        // Spawn the second firework after the delay
                        spawnFirework(player.getLocation(), org.bukkit.Color.BLUE);
                    }, 20L);
                });
            }
        }

        for (UUID uuid : getRed()) {
            getPlayers().add(uuid);

            if (winners != null && winners.equalsIgnoreCase("red")) {
                Player player = Bukkit.getPlayer(uuid);

                Bukkit.getScheduler().runTask(WoolWars.getWoolWars(), () -> {
                    spawnFirework(player.getLocation(), org.bukkit.Color.RED);
                    Bukkit.getScheduler().runTaskLater(WoolWars.getWoolWars(), () -> {
                        // Spawn the second firework after the delay
                        spawnFirework(player.getLocation(), org.bukkit.Color.RED);
                    }, 20L);
                });
            }
        }

        getBlue().clear();
        getRed().clear();

        countdown = 15;
        game_time = 1800;
        blue_wool = 100;
        red_wool = 100;
        countdownStarted = false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            assert player != null;
            player.getInventory().clear();

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getScheduler().runTask(WoolWars.getWoolWars(), () -> {
                        player.setHealth(20);
                        player.setFoodLevel(20);

                        for (PotionEffect effect : player.getActivePotionEffects()) {
                            player.removePotionEffect(effect.getType());
                        }

                        player.setGameMode(GameMode.SURVIVAL);

                        player.setAllowFlight(true);
                        player.setFlying(true);

                        assert lobby_loc != null;
                        player.teleport(lobby_loc);
                        player.getInventory().clear();
                        giveWaitingItems(player);
                    });
                }
            }.runTaskLaterAsynchronously(WoolWars.getWoolWars(), 20 * 3);
        }

        List<Block> copyBlockMap = new ArrayList<>(getBlockMap());
        List<Block> copyBlueMap = new ArrayList<>(getBlueMap());
        List<Block> copyRedMap = new ArrayList<>(getRedMap());

        // reset blocks/map
        new BukkitRunnable() {
            @Override
            public void run() {
                gameState = GameState.WAITING;
                winners = "";

                if (!copyBlockMap.isEmpty()) {
                    for (Block block : copyBlockMap) {
                        if (block.getType() != Material.AIR) {
                            block.setType(Material.AIR);
                            getBlockMap().remove(block);
                        }
                    }
                }

                if (!copyRedMap.isEmpty()) {
                    for (Block block : copyRedMap) {
                        if (block.getType() != Material.RED_WOOL) {
                            block.setType(Material.RED_WOOL);
                            getBlockMap().remove(block);
                        }
                    }
                }

                if (!copyBlueMap.isEmpty()) {
                    for (Block block : copyBlueMap) {
                        if (block.getType() != Material.BLUE_WOOL) {
                            block.setType(Material.BLUE_WOOL);
                            getBlockMap().remove(block);
                        }
                    }
                }
            }
        }.runTaskLater(WoolWars.getWoolWars(), 20 * 5);
    }

    public void selectTeam(Player player, String team) {
        if (team.equalsIgnoreCase("blue")) {
            if (blue.contains(player.getUniqueId())) {
                msgPlayer(player, "&c&l[GAME] &7You are already in this team.");
                return;
            }

            if (gameState == GameState.IN_PROGRESS) {
                players.remove(player.getUniqueId());
                red.remove(player.getUniqueId());
                blue.add(player.getUniqueId());

                player.getInventory().clear();
                assert blue_spawn != null;

                for (UUID blueuuid : getBlue()) {
                    Player blue = Bukkit.getPlayer(blueuuid);
                    if (blue == null) return;

                    assert player != null;
                    blue.showPlayer(WoolWars.getWoolWars(), player);
                }

                for (UUID reduuid : getRed()) {
                    Player red = Bukkit.getPlayer(reduuid);
                    if (red == null) return;

                    assert player != null;
                    red.showPlayer(WoolWars.getWoolWars(), player);
                }

                player.setAllowFlight(false);
                player.setFlying(false);

                player.teleport(blue_spawn);
                WoolWars.getWoolWars().getKitManager().applyKit(player, WoolWars.getWoolWars().getSelectedKit(player));
                return;
            }

            red.remove(player.getUniqueId());
            blue.add(player.getUniqueId());

            players.remove(player.getUniqueId());
            msgPlayer(player, "&a&l[GAME] &7You've picked the &f&l" + team.toUpperCase() + " &7team.");

            if (!isCountdownStarted()) {
                if (blue.size() > 0 && red.size() > 0) {
                    startCountdown();
                }
            }
        } else if (team.equalsIgnoreCase("red")) {
            if (red.contains(player.getUniqueId())) {
                msgPlayer(player, "&c&l[GAME] &7You are already in this team.");
                return;
            }

            if (gameState == GameState.IN_PROGRESS) {
                players.remove(player.getUniqueId());
                blue.remove(player.getUniqueId());
                red.add(player.getUniqueId());

                player.getInventory().clear();
                assert red_spawn != null;

                for (UUID blueuuid : getBlue()) {
                    Player blue = Bukkit.getPlayer(blueuuid);
                    if (blue == null) return;

                    assert player != null;
                    blue.showPlayer(WoolWars.getWoolWars(), player);
                }

                for (UUID reduuid : getRed()) {
                    Player red = Bukkit.getPlayer(reduuid);
                    if (red == null) return;

                    assert player != null;
                    red.showPlayer(WoolWars.getWoolWars(), player);
                }

                player.setAllowFlight(false);
                player.setFlying(false);

                player.teleport(red_spawn);

                WoolWars.getWoolWars().getKitManager().applyKit(player, WoolWars.getWoolWars().getSelectedKit(player));

                return;
            }

            red.add(player.getUniqueId());
            blue.remove(player.getUniqueId());

            players.remove(player.getUniqueId());
            msgPlayer(player, "&a&l[GAME] &7You've picked the &f&l" + team.toUpperCase() + " &7team.");

            if (!isCountdownStarted()) {
                if (blue.size() > 0 && red.size() > 0) {
                    startCountdown();
                }
            }
        }
    }

    public List<Block> getBlockMap() {
        return blockMap;
    }
    public List<Block> getBlueMap() {
        return blueBlockMap;
    }
    public List<Block> getRedMap() {
        return redBlockMap;
    }

    public void updateTab() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    List<Player> sortedPlayers = new ArrayList<>();

                    // Add red team players first
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (red.contains(player.getUniqueId())) {
                            sortedPlayers.add(player);
                        }
                    }

                    // Add blue team players next
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (blue.contains(player.getUniqueId())) {
                            sortedPlayers.add(player);
                        }
                    }

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (players.contains(player.getUniqueId())) {
                            sortedPlayers.add(player);
                        }
                    }

                    for (Player player : sortedPlayers) {
                        String tabName;

                        if (red.contains(player.getUniqueId())) {
                            tabName = format("&f" + WoolWars.getWoolWars().getConfig().getString("game.settings.red-flag") + " " + getTeamColor("red") + player.getName());
                        } else if (blue.contains(player.getUniqueId())) {
                            tabName = format("&f" + WoolWars.getWoolWars().getConfig().getString("game.settings.blue-flag") + " " + getTeamColor("blue") + player.getName());
                        } else {
                            tabName = format(getTeamColor("spectator") + player.getName());
                        }

                        // Set tab name
                        player.setPlayerListName(tabName);

                        List<String> headerLines = new ArrayList<>();
                        headerLines.add(" ");
                        headerLines.add("&#00E2A2&lM&#00E1BD&lI&#00E1D7&lN&#00E0F2&lE&#00D4FF&lR&#00BCFF&lA&#00A5FF&lV&#008DFF&lE");
                        headerLines.add(" ");

                        List<String> footerLines = new ArrayList<>();
                        footerLines.add(" ");
                        footerLines.add("&eYou're playing on &lWoolWars&e!");

                        String header = String.join("\n", headerLines);
                        String footer = String.join("\n", footerLines);

                        player.setPlayerListHeaderFooter(
                                format(header),
                                format(footer)
                        );

                        player.setDisplayName(tabName);
                        player.setCustomName(tabName);
                        player.setCustomNameVisible(true);
                    }
                }
            }
        }.runTaskTimerAsynchronously(WoolWars.getWoolWars(), 0, 20L);
    }


    public void checkGameWin() {
        if (blue_wool == 0) {
            // red wins
            winners = "red";

            for (UUID players : getRed()) {
                WoolWars.getWoolWars().getH2Data().addWins(players, 1);
            }

            for (UUID players : getBlue()) {
                WoolWars.getWoolWars().getH2Data().addLosses(players, 1);
            }

            Bukkit.broadcastMessage(format("&a&l[GAME] &7RED wins the game."));
            endGame();
        }

        if (red_wool == 0) {
            // blue wins
            winners = "blue";

            for (UUID players : getBlue()) {
                WoolWars.getWoolWars().getH2Data().addWins(players, 1);
            }

            for (UUID players : getRed()) {
                WoolWars.getWoolWars().getH2Data().addLosses(players, 1);
            }

            Bukkit.broadcastMessage(format("&a&l[GAME] &7BLUE wins the game."));
            endGame();
        }
    }

    public Location getRedObjective() {
        return red_objective;
    }

    public Location getBlueObjective() {
        return blue_objective;
    }

    public String getTeam(Player player) {
        if (blue.contains(player.getUniqueId())) {
            return "blue";
        } else if (red.contains(player.getUniqueId())) {
            return "red";
        }

        return "spectator";
    }

    public void updateHealth() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        double healthDouble = player.getHealth();
                        int health = (int) Math.round(healthDouble);

                        if (respawningMap.contains(player.getUniqueId())) {
                            return;
                        }

                        if (health == 20) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-20");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 19) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-19");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 18) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-18");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 17) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-17");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 16) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-16");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 15) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-15");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 14) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-14");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 13) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-13");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 12) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-12");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 11) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-11");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 10) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-10");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 9) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-9");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 8) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-8");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 7) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-7");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 6) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-6");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 5) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-5");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 4) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-4");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 3) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-3");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 2) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-2");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 1) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-1");
                            sendActionBar(player, format(health_action));
                        }

                        if (health == 0) {
                            String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-0");
                            sendActionBar(player, format(health_action));
                        }

                    }
                }
            }
        }.runTaskTimer(WoolWars.getWoolWars(), 0, 1);


    }

    public boolean isActiveGame() {
        return active_map;
    }

    public void setActiveGame(boolean value) {
        this.active_map = value;
    }
}