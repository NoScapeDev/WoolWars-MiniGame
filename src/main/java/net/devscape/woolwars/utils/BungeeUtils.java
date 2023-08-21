package net.devscape.woolwars.utils;

import lombok.Getter;
import net.devscape.woolwars.WoolWars;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

@Getter
public class BungeeUtils implements PluginMessageListener {

    private final WoolWars plugin;

    public BungeeUtils(WoolWars plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            /// add check
        }
    }

    public void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere was an problem connecting to " + server + "!"));
            return;
        }

        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }
}
