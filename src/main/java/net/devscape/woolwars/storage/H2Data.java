package net.devscape.woolwars.storage;

import net.devscape.woolwars.WoolWars;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class H2Data {

    private final String connectionURL = "jdbc:h2:" + WoolWars.getWoolWars().getDataFolder().getAbsolutePath() + "/database";

    public H2Data() {
        createTableIfNotExists();
    }

    public Connection getConnection() {

        Connection connection = null;

        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(connectionURL);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
            WoolWars.getWoolWars().getLogger().info("------------------------------");
            WoolWars.getWoolWars().getLogger().info("H2: Something wrong with connecting to h2-sql for SupremeTags, contact the developer if you see this.");
            WoolWars.getWoolWars().getLogger().info("------------------------------");
        }

        return connection;
    }

    public boolean exists(Player player) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM `ww_users` WHERE (player_uuid=?)");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayer(Player player) {
        if (!exists(player)) {
            UUID playerUUID = player.getUniqueId();

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "INSERT INTO ww_users (player_uuid, selected_kit, kills, deaths, wins, losses, mana, wool_broken) VALUES (?, ?, 0, 0, 0, 0, 0, 0)")) {

                String defaultKit = "Archer";

                preparedStatement.setString(1, playerUUID.toString());
                preparedStatement.setString(2, defaultKit);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTableIfNotExists() {
        try (Connection connection = getConnection()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS ww_users (" +
                    "player_uuid VARCHAR(36) PRIMARY KEY, " +
                    "selected_kit VARCHAR(255), " +
                    "kills INT NOT NULL DEFAULT 0, " +
                    "deaths INT NOT NULL DEFAULT 0, " +
                    "wins INT NOT NULL DEFAULT 0, " +
                    "losses INT NOT NULL DEFAULT 0, " +
                    "mana INT NOT NULL DEFAULT 0, " +
                    "wool_broken INT NOT NULL DEFAULT 0" +
                    ")";

            try (PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSelectedKit(UUID playerUUID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT selected_kit FROM ww_users WHERE player_uuid = ?")) {
                statement.setString(1, playerUUID.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("selected_kit");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if player data doesn't exist
    }

    public void setSelectedKit(UUID playerUUID, String kitName) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE ww_users SET selected_kit=? WHERE player_uuid = ?")) {
                statement.setString(1, kitName);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getKills(UUID playerUUID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT kills FROM ww_users WHERE player_uuid = ?")) {
                statement.setString(1, playerUUID.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("kills");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Return 0 if player data doesn't exist
    }

    public int getWoolBroken(UUID playerUUID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT wool_broken FROM ww_users WHERE player_uuid = ?")) {
                statement.setString(1, playerUUID.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("wool_broken");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Return 0 if player data doesn't exist
    }

    public int getDeaths(UUID playerUUID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT deaths FROM ww_users WHERE player_uuid = ?")) {
                statement.setString(1, playerUUID.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("deaths");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Return 0 if player data doesn't exist
    }

    public int getWins(UUID playerUUID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT wins FROM ww_users WHERE player_uuid = ?")) {
                statement.setString(1, playerUUID.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("wins");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Return 0 if player data doesn't exist
    }

    public int getLosses(UUID playerUUID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT losses FROM ww_users WHERE player_uuid = ?")) {
                statement.setString(1, playerUUID.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("losses");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Return 0 if player data doesn't exist
    }

    public int getMana(UUID playerUUID) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT mana FROM ww_users WHERE player_uuid = ?")) {
                statement.setString(1, playerUUID.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("mana");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Return 0 if player data doesn't exist
    }

    public void setKills(UUID playerUUID, int kills) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE ww_users SET kills = ? WHERE player_uuid = ?")) {
                statement.setInt(1, kills);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setWoolBroken(UUID playerUUID, int kills) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE ww_users SET wool_broken = ? WHERE player_uuid = ?")) {
                statement.setInt(1, kills);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addKills(UUID playerUUID, int kills) {
        setKills(playerUUID, getKills(playerUUID) + kills);
    }

    public void addWoolBroken(UUID playerUUID, int wool) {
        setWoolBroken(playerUUID, getWoolBroken(playerUUID) + wool);
    }

    public void addDeaths(UUID playerUUID, int deaths) {
        setDeaths(playerUUID, getDeaths(playerUUID) + deaths);
    }

    public void addWins(UUID playerUUID, int wins) {
        setWins(playerUUID, getWins(playerUUID) + wins);
    }

    public void addLosses(UUID playerUUID, int losses) {
        setLosses(playerUUID, getLosses(playerUUID) + losses);
    }

    public void addMana(UUID playerUUID, int mana) {
        setMana(playerUUID, getLosses(playerUUID) + mana);
    }

    public void removeMana(UUID playerUUID, int mana) {
        setMana(playerUUID, getLosses(playerUUID) - mana);
    }

    public void setDeaths(UUID playerUUID, int deaths) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE ww_users SET deaths = ? WHERE player_uuid = ?")) {
                statement.setInt(1, deaths);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setWins(UUID playerUUID, int wins) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE ww_users SET wins = ? WHERE player_uuid = ?")) {
                statement.setInt(1, wins);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLosses(UUID playerUUID, int losses) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE ww_users SET losses = ? WHERE player_uuid = ?")) {
                statement.setInt(1, losses);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMana(UUID playerUUID, int mana) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE ww_users SET mana = ? WHERE player_uuid = ?")) {
                statement.setInt(1, mana);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}