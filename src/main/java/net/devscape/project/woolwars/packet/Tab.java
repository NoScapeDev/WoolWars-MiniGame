package net.devscape.project.woolwars.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.devscape.project.woolwars.WoolWars;
import net.devscape.project.woolwars.handlers.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public class Tab {

    private static ProtocolManager protocolManager = null;

    public Tab() {
        protocolManager = ProtocolLibrary.getProtocolManager();

        // Register packet listener to intercept tab list packets
        protocolManager.addPacketListener(new PacketAdapter(WoolWars.getWoolWars(), ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER) {
            @Override
            public void onPacketSending(PacketEvent event) {
                updatePacketTab(event.getPlayer());
            }
        });
    }

    public static void updatePacketTab(Player player) {
        try {
            Game game = WoolWars.getWoolWars().getGameManager().getGame();

            String tabHeader = "BLUE"; // Customize as needed
            String tabFooter = "RED";  // Customize as needed

            EnumWrappers.ChatType chatType = EnumWrappers.ChatType.GAME_INFO;
            WrappedChatComponent headerComponent = WrappedChatComponent.fromText(tabHeader);
            WrappedChatComponent footerComponent = WrappedChatComponent.fromText(tabFooter);

            // Create and send the packet
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
            packet.getChatComponents().write(0, headerComponent);
            packet.getChatComponents().write(1, footerComponent);
            packet.getChatTypes().write(0, chatType);

            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace(); // Print the exception details to the console
        }
    }

}