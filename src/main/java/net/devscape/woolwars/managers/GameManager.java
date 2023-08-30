package net.devscape.woolwars.managers;

import lombok.Getter;
import lombok.Setter;
import net.devscape.project.minerave_ranks.MineraveRanks;
import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.handlers.GameState;
import net.devscape.woolwars.listeners.CombatListener;
import net.devscape.woolwars.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static net.devscape.woolwars.utils.Utils.*;
import static net.devscape.woolwars.utils.Utils.format;

@Getter
@Setter
public class GameManager {

    private List<Game> gameMap = new ArrayList<>();
    private Random random = new Random();
    private BossBar bossBarStats;
    private BossBar bossBarTextures;

    private int red_wool = 100;
    private int blue_wool = 100;
    int game_time = 1800;

    private String mapInPlay;

    public GameManager() {
        loadGame();

        updateBossBar();
        updateTab();
        updateHealth();
    }

    public void pickNewMap() {
        if (gameMap.size() > 1) {
            String current = getGame().getMapName();
            getGame(current).setActiveGame(false);

            List<Game> newGameList = new ArrayList<>(getGameMap());
            newGameList.remove(getGame(current));

            Game newGame = getRandomGame(newGameList);

            if (newGame != null) {
                newGame.setActiveGame(true);
                mapInPlay = newGame.getMapName();

                newGame.getPlayers().clear();
                newGame.getRed().clear();
                newGame.getBlue().clear();

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players != null) {
                        newGame.getPlayers().add(players.getUniqueId());

                        players.teleport(newGame.getLobby_loc());
                        giveWaitingItems(players);
                    }
                }
            }
        }
    }

    public Game getGame(String map) {
        for (Game games : gameMap) {
            if (games.getMapName().equalsIgnoreCase(map)) {
                return games;
            }
        }

        return null;
    }

    private void loadGame() {
        for (String str : WoolWars.getWoolWars().getConfig().getConfigurationSection("games").getKeys(false)) {
            Game game = new Game(
                    str,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    GameState.WAITING,
                    50,
                    getLocation(str, "lobby"),
                    getLocation(str, "blue"),
                    getLocation(str, "red"),
                    getLocation(str, "red-obj"),
                    getLocation(str, "blue-obj"),
                    false);

            gameMap.add(game);
        }

        Game game = getRandomGame();
        assert game != null;
        game.setActiveGame(true);
        mapInPlay = game.getMapName();
    }

    public Game getGame() {
        return getGame(mapInPlay);
    }

    private Game getRandomGame(List<Game> newGameMap) {
        if (!newGameMap.isEmpty()) {
            int randomIndex = random.nextInt(newGameMap.size());
            return newGameMap.get(randomIndex);
        }
        return null;
    }

    private Game getRandomGame() {
        if (!gameMap.isEmpty()) {
            int randomIndex = random.nextInt(gameMap.size());
            return gameMap.get(randomIndex);
        }
        return null;
    }

    private void updateHealth() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (getGame() != null) {
                            if (!getGame().getPlayers().contains(player.getUniqueId()) && getGame().getGameState() == GameState.IN_PROGRESS) {
                                double healthDouble = player.getHealth();
                                int health = (int) Math.round(healthDouble);

                                if (CombatListener.respawningMap.contains(player.getUniqueId())) {
                                    return;
                                }

                                String health_action = WoolWars.getWoolWars().getConfig().getString("health.health-" + health);
                                if (health_action != null) {
                                    sendActionBar(player, format(health_action));
                                }
                            } else {
                                if (getGame().getGameState() == GameState.IN_PROGRESS) {
                                    sendActionBar(player, format("&7You're Spectating!"));
                                } else if (getGame().getGameState() == GameState.STARTING) {
                                    sendActionBar(player, format("&7Game Starting!"));
                                } else {
                                    sendActionBar(player, format("&7Waiting for players!"));
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(WoolWars.getWoolWars(), 0, 1);
    }

    private void updateBossBar() {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (bossBarStats == null && bossBarTextures == null) {
                    String redBar = createProgressBar(red_wool);
                    String blueBar = createProgressBar(blue_wool);

                    bossBarStats = Bukkit.createBossBar(
                            format("&#FF3A3A&lRED &#FF3A3A" + redBar + " &f│ &f&lLOADING..f│ &#0051FF&lBLUE &#0051FF" + blueBar + ""),
                            BarColor.BLUE,
                            BarStyle.SOLID,
                            BarFlag.DARKEN_SKY);

                    bossBarTextures = Bukkit.createBossBar(
                            format("&f"),
                            BarColor.BLUE,
                            BarStyle.SOLID,
                            BarFlag.DARKEN_SKY);
                }

                if (getGame() != null ) {
                    if (getGame().getGameState() == GameState.WAITING) {
                        if (getGame().getBlue().size() == 0 && getGame().getRed().size() == 0) {
                            bossBarStats.setTitle(format("&7Select a Team"));
                        } else if (getGame().getBlue().size() == 1 && getGame().getRed().size() == 0) {
                            bossBarStats.setTitle(format("&7Waiting for other players"));
                        } else if (getGame().getBlue().size() == 0 && getGame().getRed().size() == 1) {
                            bossBarStats.setTitle(format("&7Waiting for other players"));
                        } else {
                            bossBarStats.setTitle(format("&7Select a Team"));
                        }
                    }

                    if (getGame().getGameState() == GameState.STARTING) {
                        int secondsLeft = getGame().getCountdown();

                        if (secondsLeft <= 15) {
                            String countdown_action = WoolWars.getWoolWars().getConfig().getString("countdown.countdown-" + secondsLeft);
                            if (countdown_action != null) {
                                bossBarStats.setTitle(format(countdown_action));
                            }
                        } if (secondsLeft <= 0) {
                            bossBarStats.setTitle(format("&f&lFIGHT!"));
                        }
                    }

                    if (getGame().getGameState() == GameState.IN_PROGRESS) {
                        String redBar = createProgressBar(red_wool);
                        String blueBar = createProgressBar(blue_wool);

                        if (game_time <= 0) {
                            getGame().endGame();
                        } else {
                            game_time--;
                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            PlayerData playerData = WoolWars.getWoolWars().getPlayerDataManager().getPlayerData(player.getUniqueId());

                            int kills = playerData.getPlayerCurrentGameData().getKills();
                            int deaths = playerData.getPlayerCurrentGameData().getDeaths();

                            String bb_stats = WoolWars.getWoolWars().getConfig().getString("bossbars.stats");
                            assert bb_stats != null;
                            bb_stats = bb_stats.replaceAll("%red_flag%", getTeamFlag("red"));
                            bb_stats = bb_stats.replaceAll("%blue_flag%", getTeamFlag("blue"));
                            bb_stats = bb_stats.replaceAll("%red_percentage%", String.valueOf(red_wool));
                            bb_stats = bb_stats.replaceAll("%blue_percentage%", String.valueOf(blue_wool));
                            bb_stats = bb_stats.replaceAll("%kills%", String.valueOf(kills));
                            bb_stats = bb_stats.replaceAll("%deaths%", String.valueOf(deaths));
                            bb_stats = bb_stats.replaceAll("%game_time%", formatGameTime(game_time));

                            bossBarStats.setTitle(format(bb_stats));

                            String bb_textures = WoolWars.getWoolWars().getConfig().getString("bossbars.textures");
                            bossBarTextures.setTitle(format(bb_textures));
                        }

                        if (getGame().getBlue().size() == 0) {
                            getGame().endGame();
                        }

                        if (getGame().getRed().size() == 0) {
                            getGame().endGame();
                        }

                        for (UUID uuid : getGame().getPlayers()) {
                            Player spectator = Bukkit.getPlayer(uuid);

                            if (spectator != null) {

                                for (UUID blueUUID : getGame().getBlue()) {
                                    Player blue = Bukkit.getPlayer(blueUUID);
                                    if (blue != null) {
                                        blue.hidePlayer(WoolWars.getWoolWars(), spectator);
                                    }
                                }

                                for (UUID redUUID : getGame().getRed()) {
                                    Player red = Bukkit.getPlayer(redUUID);
                                    if (red != null) {
                                        red.hidePlayer(WoolWars.getWoolWars(), spectator);
                                    }
                                }
                            }
                        }
                    }

                    if (getGame().getGameState() == GameState.CHANGING_PROCESS) {
                        bossBarStats.setTitle(format("&f│ &f&lRESETTING GAME..&f│"));
                    }
                }
            }
        }.runTaskTimer(WoolWars.getWoolWars(), 0, 20L);
    }

    private void updateTab() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTask(WoolWars.getWoolWars(), () -> {
                    if (Bukkit.getOnlinePlayers().size() > 0) {

                        if (getGame() != null) {
                            if (getGame().getGameState() == GameState.IN_PROGRESS) {
                                List<Player> sortedPlayers = new ArrayList<>();

                                // Add red team players first
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (getGame().getRed().contains(player.getUniqueId())) {
                                        sortedPlayers.add(player);
                                    }
                                }

                                // Add blue team players next
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (getGame().getBlue().contains(player.getUniqueId())) {
                                        sortedPlayers.add(player);
                                    }
                                }

                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (getGame().getPlayers().contains(player.getUniqueId())) {
                                        sortedPlayers.add(player);
                                    }
                                }

                                for (Player player : sortedPlayers) {
                                    String tabName;

                                    if (getGame().getRed().contains(player.getUniqueId())) {
                                        tabName = format("&f" + WoolWars.getWoolWars().getConfig().getString("game.settings.red-flag") + " " + getTeamColor("red") + player.getName());
                                    } else if (getGame().getBlue().contains(player.getUniqueId())) {
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

                                    if (getGame().getTeam(player).equalsIgnoreCase("spectator")) {
                                        WoolWars.getWoolWars().getTeamManager().applyTo(player, format("&7"));
                                    } else {
                                        WoolWars.getWoolWars().getTeamManager().applyTo(player, format("&f" + getTeamFlag(getGame().getTeam(player)) + " &f"));
                                    }
                                }
                            } else {

                                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

                                // Sort the players based on rank weights
                                Collections.sort(players, (player1, player2) -> {
                                    // Get rank weights for each player and compare
                                    int weight1 = getRankWeight(player1);
                                    int weight2 = getRankWeight(player2);
                                    return Integer.compare(weight2, weight1); // Reverse order for highest weight first
                                });

                                int index = 0;
                                for (Player player : players) {
                                    WoolWars.getWoolWars().getTeamManager().applyTo(player, format(MineraveRanks.getMineraveRanks().getPlayerDataClass().getRank(player).getPrefix()));

                                    String tabName = format(MineraveRanks.getMineraveRanks().getPlayerDataClass().getRank(player).getPrefix() + "" + player.getName());

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

                                    // Increment index for footer
                                    index++;
                                }
                            }
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(WoolWars.getWoolWars(), 0, 20L);
    }

    private String formatGameTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d", minutes, remainingSeconds);
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

}