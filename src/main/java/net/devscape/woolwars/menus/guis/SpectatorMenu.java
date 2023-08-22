package net.devscape.woolwars.menus.guis;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.menus.Menu;
import net.devscape.woolwars.menus.MenuUtil;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.devscape.woolwars.utils.Utils.color;
import static net.devscape.woolwars.utils.Utils.format;

public class SpectatorMenu extends Menu {

    public SpectatorMenu(MenuUtil menuUtil) {
        super(menuUtil);
    }

    @Override
    public String getMenuName() {
        return format("&8Spectator Menu");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        List<UUID> inGamePlayers = new ArrayList<>();

        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        inGamePlayers.addAll(game.getRed());
        inGamePlayers.addAll(game.getBlue());

        ItemStack item = e.getCurrentItem();

        if (item != null && item.getItemMeta() != null && e.getSlot() != 49) {
            String players_name = Utils.deformat(item.getItemMeta().getDisplayName());

            Player target = Bukkit.getPlayer(players_name);

            if (target != null) {
                if (inGamePlayers.contains(target.getUniqueId())) {
                    player.teleport(target.getLocation());
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        List<UUID> inGamePlayers = new ArrayList<>();

        Game game = WoolWars.getWoolWars().getGameManager().getGame();

        inGamePlayers.addAll(game.getRed());
        inGamePlayers.addAll(game.getBlue());

        for (UUID uuid : inGamePlayers) {
            Player target = Bukkit.getPlayer(uuid);

            if (target != null) {
                ItemStack item = getPlayerHead(uuid);
                ItemMeta itemMeta = item.getItemMeta();

                itemMeta.setDisplayName("&f" + target.getName());

                List<String> lore = new ArrayList<>();
                lore.add("&7Click to teleport to this player.");

                itemMeta.setLore(color(lore));

                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_UNBREAKABLE);

                getInventory().addItem(item);
            }
        }

        getInventory().setItem(49, Utils.makeItem(Material.BARRIER, "&#FF3A3A&lClose", Utils.format("&7Click to close the menu!")));
    }


    public ItemStack getPlayerHead(UUID playerUUID) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        assert skullMeta != null;
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
        playerHead.setItemMeta(skullMeta);

        return playerHead;
    }
}