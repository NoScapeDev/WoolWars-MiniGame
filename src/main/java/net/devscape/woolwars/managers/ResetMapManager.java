package net.devscape.woolwars.managers;

import lombok.Getter;
import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.handlers.GameState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResetMapManager {

    private List<Block> blockMap = new ArrayList<>();

    private final List<Block> redBlockMap = new ArrayList<>();
    private final List<Block> blueBlockMap = new ArrayList<>();

    public void resetMap() {
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        List<Block> copyBlockMap = new ArrayList<>(getBlockMap());
        List<Block> copyBlueMap = new ArrayList<>(blueBlockMap);
        List<Block> copyRedMap = new ArrayList<>(redBlockMap);

        // reset blocks/map
        new BukkitRunnable() {
            @Override
            public void run() {
                game.setGameState(GameState.WAITING);
                game.setWinners("");

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


    public void resetMapInstant() {
        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        List<Block> copyBlockMap = new ArrayList<>(getBlockMap());
        List<Block> copyBlueMap = new ArrayList<>(blueBlockMap);
        List<Block> copyRedMap = new ArrayList<>(redBlockMap);

        // reset blocks/map
        game.setGameState(GameState.WAITING);
        game.setWinners("");

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
}
