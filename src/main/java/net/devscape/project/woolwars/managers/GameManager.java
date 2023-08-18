package net.devscape.project.woolwars.managers;

import net.devscape.project.woolwars.WoolWars;
import net.devscape.project.woolwars.handlers.Game;
import net.devscape.project.woolwars.handlers.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.devscape.project.woolwars.utils.Utils.getLocation;

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
        getGame().setActiveGame(false);
        Game game = getRandomGame();
        if (game != null) {
            game.setActiveGame(true);
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