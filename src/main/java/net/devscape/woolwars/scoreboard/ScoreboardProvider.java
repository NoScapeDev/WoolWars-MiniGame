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
        return Utils.format(this.main.getConfig().getString("scoreboards.title"));
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


        for (String line : this.main.getConfig().getStringList("scoreboards.waiting.lines")) {
            lines.add(Utils.format(line));
        }

        lines.replaceAll(s -> s.replaceAll("%kills%", String.valueOf(kills)));
        lines.replaceAll(s -> s.replaceAll("%deaths%", String.valueOf(deaths)));
        lines.replaceAll(s -> s.replaceAll("%kd%", killsDeathsRatioFormatted));

        return lines;
    }

    private List<String> getInGameScoreboard(Player player, PlayerData playerData) {
        List<String> lines = new ArrayList<>();

        Game game = this.main.getGameManager().getGame();

        lines.add("");

        lines.add(Utils.format(this.main.getConfig().getString("scoreboards.in-game.map-name").replace("%map%", game.getMapName())));

        if (game.isCountdownStarted()) {
            lines.add(Utils.format(this.main.getConfig().getString("scoreboards.in-game.countdown").replace("%time%", String.valueOf(game.getCountdown()))));
        }

        for (String line : this.main.getConfig().getStringList("scoreboards.in-game.lines")) {
            lines.add(Utils.format(line));
        }

        lines.replaceAll(s -> s.replaceAll("%red%", String.valueOf(game.getRed().size())));
        lines.replaceAll(s -> s.replaceAll("%blue%", String.valueOf(game.getBlue().size())));
        lines.replaceAll(s -> s.replaceAll("%kills%", String.valueOf(playerData.getPlayerCurrentGameData().getKills())));
        lines.replaceAll(s -> s.replaceAll("%deaths%", String.valueOf(playerData.getPlayerCurrentGameData().getDeaths())));

        return lines;
    }
}
