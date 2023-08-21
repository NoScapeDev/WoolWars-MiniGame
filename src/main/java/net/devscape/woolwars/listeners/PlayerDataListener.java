package net.devscape.woolwars.listeners;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerDataListener implements Listener {

	private WoolWars plugin = WoolWars.getWoolWars();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		Player player = Bukkit.getPlayer(event.getUniqueId());
		if (player != null) {
			if (player.isOnline()) {
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
				event.setKickMessage("§cYou tried to login too quickly after disconnecting.\n§cTry again in a few seconds.");

				this.plugin.getServer().getScheduler().runTask(this.plugin, () -> player.kickPlayer("§cDuplicate Login"));
				return;
			}

			PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		PlayerData playerData = this.plugin.getPlayerDataManager().getOrCreate(event.getPlayer().getUniqueId());
		if (playerData == null) {
			event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			event.setKickMessage("§cAn error has occurred while loading your profile. Please reconnect.");
			return;
		}

		if (!playerData.isLoaded()) {
			event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			event.setKickMessage("§cAn error has occurred while loading your profile. Please reconnect.");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();

		PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

		this.handleDataSave(playerData);
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();

		PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

		this.handleDataSave(playerData);
	}

	private void handleDataSave(PlayerData playerData) {
		if (playerData != null) {
			this.plugin.getPlayerDataManager().deletePlayer(playerData.getUniqueId());
		}
	}
}
