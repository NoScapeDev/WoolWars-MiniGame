package net.devscape.woolwars.managers;

import lombok.Getter;
import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.playerdata.PlayerData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

	private WoolWars main = WoolWars.getWoolWars();

	@Getter private Map<UUID, PlayerData> players = new HashMap<>();

	public PlayerData getOrCreate(UUID uniqueId) {
		return this.players.computeIfAbsent(uniqueId, PlayerData::new);
	}

	public PlayerData getPlayerData(UUID uniqueId) {
		return this.players.getOrDefault(uniqueId, new PlayerData(uniqueId));
	}

	public Collection<PlayerData> getAllPlayers() {
		return this.players.values();
	}

	public void deletePlayer(UUID uniqueId) {
		this.main.getServer().getScheduler().runTaskAsynchronously(this.main, () -> {
			this.players.remove(uniqueId);
		});
	}
}
