package net.devscape.woolwars.playerdata;

import lombok.Getter;
import lombok.Setter;
import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.managers.PlayerDataManager;

import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    private PlayerDataManager playerDataManager = WoolWars.getWoolWars().getPlayerDataManager();
    private PlayerState playerState = PlayerState.SPAWN;
    private PlayerCurrentGameData playerCurrentGameData = new PlayerCurrentGameData();

    private UUID uniqueId;
    private boolean loaded;

    public PlayerData(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.loaded = true;
    }

    public boolean isInSpawn() {
        return this.playerState == PlayerState.SPAWN;
    }
}
