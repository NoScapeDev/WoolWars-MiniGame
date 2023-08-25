package net.devscape.woolwars.managers;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.NametagAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import static net.devscape.woolwars.utils.Utils.format;

@Getter
public class TeamManager {

    private Objective healthObjective;
    private ScoreboardManager scoreboardManager;

    public TeamManager() {
        scoreboardManager = Bukkit.getScoreboardManager();
    }

    public void applyTo(Player player, String text) {
        NametagEdit.getApi().setPrefix(player, format(text));
    }

    public void updateBelowHealth(Player player, String text) {
        if (healthObjective == null) {
            Scoreboard board = scoreboardManager.getNewScoreboard();
            healthObjective = board.registerNewObjective("customhealth", "dummy");
            healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            player.setScoreboard(board);
        }

        healthObjective.setDisplayName(text);
    }

}