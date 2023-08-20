package net.devscape.woolwars.listeners;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.playerdata.PlayerData;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Iterator;
import java.util.UUID;

public class AsyncPlayerChatListener implements Listener {

    private WoolWars main = WoolWars.getWoolWars();

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        PlayerData playerData = this.main.getPlayerDataManager().getPlayerData(player.getUniqueId());

        Game game = this.main.getGameManager().getGame();

        if (game == null) {
            return;
        }

        switch (playerData.getPlayerState()) {
            case SPAWN:
                if (game.getRed().contains(player.getUniqueId())) {
                    event.setFormat(Utils.format("来 &#fc372d" + player.getName() + "&7: &f" + event.getMessage()));
                } else if (game.getBlue().contains(player.getUniqueId())) {
                    event.setFormat(Utils.format("例 &#138bf4" + player.getName() + "&7: &f" + event.getMessage()));
                } else if (game.getPlayers().contains(player.getUniqueId())) {
                    event.setFormat(Utils.format("&7[SPECTATOR] &b" + player.getName() + "&7: &f" + event.getMessage()));
                }

                break;
            case IN_GAME:
                Iterator<Player> recipients = event.getRecipients().iterator();

                while (recipients.hasNext()) {
                    Player recipient = recipients.next();

                    if (game.getRed().contains(recipient.getUniqueId())) {
                        event.setFormat(Utils.format("来 &#fc372d" + player.getName() + "&7: &f" + event.getMessage()));
                    } else if (game.getBlue().contains(recipient.getUniqueId())) {
                        event.setFormat(Utils.format("例 &#138bf4" + player.getName() + "&7: &f" + event.getMessage()));
                    } else if (game.getPlayers().contains(player.getUniqueId())) {
                        event.setFormat(Utils.format("&7[SPECTATOR] &b" + player.getName() + "&7: &f" + event.getMessage()));
                    }

                    if (!game.getTeam(player).equalsIgnoreCase(game.getTeam(recipient))) {
                        recipients.remove();
                    }
                }

                break;
        }
    }
}
