package net.devscape.woolwars.managers;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.NametagAPI;
import lombok.Getter;
import org.bukkit.entity.Player;

import static net.devscape.woolwars.utils.Utils.format;

@Getter
public class TeamManager {

    public TeamManager() {}

    public void applyTo(Player player, String text) {

        NametagEdit.getApi().setPrefix(player, format(text));
    }

}