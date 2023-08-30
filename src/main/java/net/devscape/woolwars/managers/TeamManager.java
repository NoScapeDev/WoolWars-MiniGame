package net.devscape.woolwars.managers;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.NametagAPI;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import static net.devscape.woolwars.utils.Utils.format;

@Getter
public class TeamManager {

    private Objective healthObjective;
    private ScoreboardManager scoreboardManager;

    public TeamManager() {
        scoreboardManager = Bukkit.getScoreboardManager();
    }

    public void applyTo(Player player, String text) {
        NametagEdit.getApi().setNametag(player, format(text), "");
    }

    public void updateBelowHealth(Player player) {
        org.bukkit.scoreboard.ScoreboardManager sm = Bukkit.getScoreboardManager();
        Scoreboard s = sm.getNewScoreboard();

        Objective h = s.registerNewObjective("showhealth", Criterias.HEALTH);

        h.setDisplaySlot(DisplaySlot.BELOW_NAME);

        h.setDisplayName(ChatColor.RED + "❤");

        player.setScoreboard(s);
    }

}