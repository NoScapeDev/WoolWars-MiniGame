package net.devscape.woolwars.listeners;

import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.managers.abilities.Ability;
import net.devscape.woolwars.menus.guis.KitSelectorMenu;
import net.devscape.woolwars.menus.guis.TeamSelectorMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static net.devscape.woolwars.utils.Utils.format;

public class ItemListener implements Listener {

    private WoolWars main = WoolWars.getWoolWars();

    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if (item != null && item.getItemMeta() != null) {
            String displayName = item.getItemMeta().getDisplayName();

            if (displayName.equalsIgnoreCase(format(WoolWars.getWoolWars().getConfig().getString("items.team-selector.displayname")))) {
                new TeamSelectorMenu(WoolWars.getMenuUtil(player)).open();
            }

            if (displayName.equalsIgnoreCase(format(WoolWars.getWoolWars().getConfig().getString("items.kit-selector.displayname")))) {
                new KitSelectorMenu(WoolWars.getMenuUtil(player)).open();
            }

            for (Ability ability : this.main.getAbilityManager().getAbilities()) {
                if (!displayName.equalsIgnoreCase(ability.getItemStack().getItemMeta().getDisplayName())) {
                    continue;
                }

                switch (ability.getName()) {
                    case "Shuriken":
                        this.main.getAbilityManager().getAbility("Shuriken").getAbilityCallable().executeAs(player);

                        break;
                    case "Eskimo":
                        this.main.getAbilityManager().getAbility("Eskimo").getAbilityCallable().executeAs(player);

                        break;
                }
            }
        }
    }
}
