package net.devscape.woolwars.managers;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.handlers.GameState;
import net.devscape.woolwars.playerdata.PlayerData;
import net.devscape.woolwars.playerdata.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static net.devscape.woolwars.utils.Utils.getLocation;
import static net.devscape.woolwars.utils.Utils.giveWaitingItems;

public class GameManager {

    private List<Game> gameMap = new ArrayList<>();
    private Random random = new Random();

    public GameManager() {
        loadGame();
        getGame().updateBossBar();
        getGame().updateTab();
        getGame().updateHealth();
    }

    public void pickNewMap() {
        if (gameMap.size() > 1) {
            getGame().setActiveGame(false);

            List<UUID> players = new ArrayList<>(getGame().getPlayers());

            Game game = getRandomGame();
            if (game != null) {
                game.setActiveGame(true);

                game.getPlayers().addAll(players);
                for (UUID player : game.getPlayers()) {
                    Player p = Bukkit.getPlayer(player);

                    if (p != null) {
                        p.teleport(game.getLobby_loc());
                        giveWaitingItems(p);
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

    public void loadGame() {
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
        if (game != null) {
            game.setActiveGame(true);
        }
    }

    public Game getGame() {
        for (Game games : gameMap) {
            if (games.isActiveGame()) {
                return games;
            }
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

}