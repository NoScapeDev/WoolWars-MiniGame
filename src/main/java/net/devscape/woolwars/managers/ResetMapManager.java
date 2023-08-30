package net.devscape.woolwars.managers;

import lombok.Getter;
import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.handlers.GameState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResetMapManager {

    private final List<Block> blockBreakMap = new ArrayList<>();
    private final List<Block> blockPlaceMap = new ArrayList<>();

    private final List<Block> redBlockMap = new ArrayList<>();
    private final List<Block> blueBlockMap = new ArrayList<>();

    public void resetMap() {
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        List<Block> copyBlockBreakMap = new ArrayList<>(getBlockBreakMap());
        List<Block> copyBlockPlaceMap = new ArrayList<>(getBlockPlaceMap());
        List<Block> copyBlueMap = new ArrayList<>(blueBlockMap);
        List<Block> copyRedMap = new ArrayList<>(redBlockMap);

        // reset blocks/map
        new BukkitRunnable() {
            @Override
            public void run() {
                game.setGameState(GameState.WAITING);
                game.setWinners("");

                for (Entity entity : game.getLobbyLoc().getWorld().getEntities()) {
                    if (entity instanceof Boat) {
                        entity.remove();
                    }
                }

                if (!copyBlockPlaceMap.isEmpty()) {
                    for (Block block : copyBlockPlaceMap) {
                        if (block.getType() != Material.AIR) {
                            block.setType(Material.AIR);
                            getBlockPlaceMap().remove(block);
                        }
                    }
                }

                if (!copyBlockBreakMap.isEmpty()) {
                    for (Block block : copyBlockBreakMap) {
                        if (block.getLocation().getBlock().getType() == Material.AIR) {
                            block.setType(block.getType());
                            getBlockBreakMap().remove(block);
                        }
                    }
                }

                if (!copyRedMap.isEmpty()) {
                    for (Block block : copyRedMap) {
                        if (block.getType() != Material.RED_WOOL) {
                            block.setType(Material.RED_WOOL);
                            getBlockPlaceMap().remove(block);
                        }
                    }
                }

                if (!copyBlueMap.isEmpty()) {
                    for (Block block : copyBlueMap) {
                        if (block.getType() != Material.BLUE_WOOL) {
                            block.setType(Material.BLUE_WOOL);
                            getBlockPlaceMap().remove(block);
                        }
                    }
                }
            }
        }.runTaskLater(WoolWars.getWoolWars(), 20 * 4);
    }

    public void resetMapInstant() {
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        List<Block> copyBlockBreakMap = new ArrayList<>(getBlockBreakMap());
        List<Block> copyBlockPlaceMap = new ArrayList<>(getBlockPlaceMap());
        List<Block> copyBlueMap = new ArrayList<>(blueBlockMap);
        List<Block> copyRedMap = new ArrayList<>(redBlockMap);

        // reset blocks/map
        game.setGameState(GameState.WAITING);
        game.setWinners("");

        for (Entity entity : game.getLobbyLoc().getWorld().getEntities()) {
            if (entity instanceof Boat) {
                entity.remove();
            }
        }

        if (!copyBlockPlaceMap.isEmpty()) {
            for (Block block : copyBlockPlaceMap) {
                if (block.getType() != Material.AIR) {
                    block.setType(Material.AIR);
                    getBlockPlaceMap().remove(block);
                }
            }
        }

        if (!copyBlockBreakMap.isEmpty()) {
            for (Block block : copyBlockBreakMap) {
                if (block.getLocation().getBlock().getType() == Material.AIR) {
                    block.setType(block.getType());
                    getBlockBreakMap().remove(block);
                }
            }
        }

        if (!copyRedMap.isEmpty()) {
            for (Block block : copyRedMap) {
                if (block.getType() != Material.RED_WOOL) {
                    block.setType(Material.RED_WOOL);
                    getBlockPlaceMap().remove(block);
                }
            }
        }

        if (!copyBlueMap.isEmpty()) {
            for (Block block : copyBlueMap) {
                if (block.getType() != Material.BLUE_WOOL) {
                    block.setType(Material.BLUE_WOOL);
                    getBlockPlaceMap().remove(block);
                }
            }
        }
    }
}
