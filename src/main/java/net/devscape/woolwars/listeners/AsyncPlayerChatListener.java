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

        Game game = this.main.getGameManager().getGame();

        if (game != null) {
            if (game.getRed().contains(player.getUniqueId())) {
                event.setFormat(Utils.format("&f来 &#fc372d" + player.getName() + "&7: &f" + event.getMessage()));
            } else if (game.getBlue().contains(player.getUniqueId())) {
                event.setFormat(Utils.format("&f例 &#138bf4" + player.getName() + "&7: &f" + event.getMessage()));
            } else if (game.getPlayers().contains(player.getUniqueId())) {
                event.setFormat(Utils.format("&7" + player.getName() + "&7: &f" + event.getMessage()));
            }
        }
    }
}
