package net.devscape.woolwars.scoreboard;

import aether.scoreboard.Board;
import aether.scoreboard.BoardAdapter;
import aether.scoreboard.cooldown.BoardCooldown;
import net.devscape.woolwars.WoolWars;
import net.devscape.woolwars.handlers.Game;
import net.devscape.woolwars.handlers.LocalData;
import net.devscape.woolwars.playerdata.PlayerData;
import net.devscape.woolwars.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScoreboardProvider implements BoardAdapter {

    private WoolWars main = WoolWars.getWoolWars();

    @Override
    public String getTitle(Player player) {
        return Utils.format("&bWool Wars");
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        PlayerData playerData = this.main.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (playerData == null) {
            return null;
        }

        switch (playerData.getPlayerState()) {
            case SPAWN:
                return this.getSpawnScoreboard(player, playerData);
            case IN_GAME:
                return this.getInGameScoreboard(player, playerData);
        }

        return null;
    }

    @Override
    public void onScoreboardCreate(Player player, Scoreboard scoreboard) {

    }

    private List<String> getSpawnScoreboard(Player player, PlayerData playerData) {
        List<String> lines = new ArrayList<>();

        int kills = WoolWars.getWoolWars().getH2Data().getKills(player.getUniqueId());
        int deaths = WoolWars.getWoolWars().getH2Data().getDeaths(player.getUniqueId());

        double killsDeathsRatio = 0;

        if (deaths > 0) {
            killsDeathsRatio = (double) kills / deaths;
        } else {
            killsDeathsRatio = kills;
        }

        DecimalFormat killsDeathsRatioFormat = new DecimalFormat();

        if (killsDeathsRatio < 10.0) {
            killsDeathsRatioFormat = new DecimalFormat("0.00");
        } else {
            killsDeathsRatioFormat = new DecimalFormat("00.00");
        }

        String killsDeathsRatioFormatted = killsDeathsRatioFormat.format(killsDeathsRatio);

        lines.add(Utils.scoreboardBar);
        lines.add(Utils.format("&9Join a team and prepare"));
        lines.add(Utils.format("&9to fight enemies on"));
        lines.add(Utils.format("&9multiple islands"));
        lines.add(Utils.format(""));
        lines.add(Utils.format("&bKills: &f" + kills));
        lines.add(Utils.format("&bDeaths: &f" + deaths));
        lines.add(Utils.format("&bKD: &f" + killsDeathsRatioFormatted));
        lines.add(Utils.format(""));
        lines.add(Utils.format("&7&ominerave.net"));
        lines.add(Utils.scoreboardBar);

        return lines;
    }

    private List<String> getInGameScoreboard(Player player, PlayerData playerData) {
        List<String> lines = new ArrayList<>();

        Game game = this.main.getGameManager().getGame();

        LocalData localData = this.main.getLocalStatsManager().getData(player.getUniqueId());

        lines.add(Utils.scoreboardBar);
        lines.add(Utils.format("&bMap Name: &f" + game.getMapName()));

        if (game.isCountdownStarted()) {
            lines.add(Utils.format("&bTime Left: &f" + game.getCountdown()));
        }

        lines.add(Utils.format(""));
        lines.add(Utils.format("&cRed Team: &f" + game.getRed().size()));
        lines.add(Utils.format("&cBlue Team: &f" + game.getBlue().size()));
        lines.add(Utils.format(""));
        lines.add(Utils.format("&bKills: &f" + localData.getKills()));
        lines.add(Utils.format("&bDeaths: &f" + localData.getDeaths()));
        lines.add(Utils.format(""));
        lines.add(Utils.scoreboardBar);

        return lines;
    }
}
