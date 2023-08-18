package net.devscape.project.woolwars.handlers;

public class LocalData {

    private int kills;
    private int deaths;
    private int wool_broken;

    public LocalData(int kills, int deaths, int woolBroken) {
        this.kills = kills;
        this.deaths = deaths;
        wool_broken = woolBroken;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getWool_broken() {
        return wool_broken;
    }

    public void setWool_broken(int wool_broken) {
        this.wool_broken = wool_broken;
    }
}
