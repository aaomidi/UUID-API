package com.aaomidi.dev.uuidapi;


import com.aaomidi.dev.uuidapi.storage.DatabaseAPI;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

public class DatabaseManager {
    private final UUIDAPI _plugin;
    private DatabaseAPI databaseAPI;

    public DatabaseManager(UUIDAPI plugin) {
        _plugin = plugin;
        databaseAPI = _plugin.getDatabaseAPI();
    }

    /**
     * Construct tables to be used in UUID API
     *
     * @return Table construction status.
     */
    private boolean createTables() {
        String query1 = "CREATE TABLE IF NOT EXISTS `uuid_index` (`id` INT NOT NULL AUTO_INCREMENT, `uuid` VARCHAR(32) UNIQUE NOT NULL, PRIMARY KEY (`id`))ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        String query2 = "CREATE TABLE IF NOT EXISTS `uuid_entries`(`id` INT NOT NULL, `username` VARCHAR(16), `isMain` BOOLEAN, PRIMARY KEY (`id`,`username`))ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try {
            databaseAPI.executeUpdate(query1);
            databaseAPI.executeUpdate(query2);
            return true;
        } catch (Exception ex) {
            _plugin.getLogger().log(Level.SEVERE, "ERROR OCCURRED WHEN CREATING THE TABLE. \n StackTrace: " + ex.getMessage() + "\nDisabling plugin.");
            _plugin.getServer().getPluginManager().disablePlugin(_plugin);
            return false;
        }
    }

    /**
     * Initialize User
     *
     * @param player Player to initialize.
     * @return The initialization process.
     */
    protected boolean initializeUser(Player player) {
        String uuid = player.getUniqueId().toString().replace("-", "");
        String query1 = "INSERT IGNORE INTO `uuid_index` (`uuid`) VALUES (?)";
        String query2 = "INSERT IGNORE INTO `uuid_entries` (`id`,`username`,`isMain`) VALUES (?,?,?);";
        int userIndex = this.getUserIndex(uuid);
        if (userIndex != -1) {
            try {
                databaseAPI.executeQuery(query1, uuid);
                userIndex = this.getUserIndex(uuid);
                databaseAPI.executeUpdate(query2, userIndex, player.getName(), true);
                return true;
            } catch (Exception ex) {
                _plugin.getLogger().log(Level.SEVERE, "ERROR OCCURRED WHEN INITIALIZING THE PLAYER " + player.getName() + "\nStackTrace: " + ex.getMessage() + "\nDisabling plugin");
                _plugin.getServer().getPluginManager().disablePlugin(_plugin);
                return false;
            }
        } else {
            try {
                this.setAllOldUsernamesInactive(uuid);
                databaseAPI.executeUpdate(query2, userIndex, player.getName(), true);
                return true;
            } catch (Exception ex) {
                _plugin.getLogger().log(Level.SEVERE, "ERROR OCCURRED WHEN INITIALIZING THE PLAYER " + player.getName() + "\nStackTrace: " + ex.getMessage() + "\nDisabling plugin");
                _plugin.getServer().getPluginManager().disablePlugin(_plugin);
                return false;
            }
        }
    }

    /**
     * Get indexed ID for UUID.
     *
     * @param uuid UUID of player.
     * @return The indexed value.
     */
    private int getUserIndex(String uuid) {
        String query = "SELECT * FROM `uuid_index` WHERE `id`=?";
        try {
            ResultSet resultSet = databaseAPI.executeQuery(query, uuid);
            if (resultSet.next()) {
                do {
                    return resultSet.getInt("id");
                } while (resultSet.next());
            } else {
                return -1;
            }
        } catch (SQLException ex) {
            return -1;
        }
    }

    /**
     * Set all the old usernames as inactive.
     *
     * @param uuid UUID of player.
     * @return status.
     */
    private boolean setAllOldUsernamesInactive(String uuid) {
        String query = "SET `isMain`=? WHERE `id`=? AND `isMain`=?";
        Integer userIndex = this.getUserIndex(uuid);
        try {
            databaseAPI.executeUpdate(query, false, userIndex, true);
            return true;
        } catch (Exception ex) {
            _plugin.getLogger().log(Level.SEVERE, "ERROR OCCURRED WHEN SETTING THE isMain to FALSE.\n StackTrace: " + ex.getMessage() + "\nDisabling plugin.");
            _plugin.getServer().getPluginManager().disablePlugin(_plugin);
            return false;
        }

    }

    /**
     * Get usernames.
     *
     * @param uuid UUID of player.
     * @return ArrayList containing ALL usernames
     */
    protected ArrayList<String> getUsernames(String uuid) {
        String query = "SELECT `username` FROM `uuid_api`WHERE `id`=?";
        Integer userIndex = this.getUserIndex(uuid);
        ArrayList<String> usernames = new ArrayList<>();
        try {
            ResultSet resultSet = databaseAPI.executeQuery(query, userIndex);
            if (resultSet.next()) {
                int x = 1;
                do {
                    String username = resultSet.getString(x);
                    usernames.add(username);
                    x = x++;
                } while (resultSet.next());
                return usernames;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            _plugin.getLogger().log(Level.SEVERE, "ERROR OCCURRED WHEN GETTING USERNAMES OF A UUID. StackTrace: " + ex.getMessage() + "\nDisabling plugin.");
            _plugin.getServer().getPluginManager().disablePlugin(_plugin);
            return null;
        }
    }

    /**
     * Get the active username of Player.
     *
     * @param uuid UUID of player.
     * @return Active username of Player.
     */
    private String getAUsername(String uuid) {
        String query = "SELECT `username` FROM `uuid_api` WHERE `id`=? AND `isMain`=?";
        Integer userIndex = this.getUserIndex(uuid);
        try {
            ResultSet resultSet = databaseAPI.executeQuery(query, userIndex, true);
            if (resultSet.next()) {
                do {
                    return resultSet.getString("username");
                } while (resultSet.next());
            } else {
                return null;
            }
        } catch (SQLException ex) {
            _plugin.getLogger().log(Level.SEVERE, "ERROR OCCURRED WHEN GETTING ACTIVE USERNAME OF A UUID. StackTrace: " + ex.getMessage() + "\nDisabling plugin.");
            _plugin.getServer().getPluginManager().disablePlugin(_plugin);
            return null;
        }
    }

    /**
     * Initialize player.
     *
     * @param player Player to be initialized.
     */
    public boolean initializePlayer(Player player) {
        return this.initializeUser(player);
    }

    /**
     * Get all usernames of a UUID.
     *
     * @param uuid UUID of a player.
     */
    protected ArrayList<String> getAllUsernames(String uuid) {
        return this.getUsernames(uuid);
    }

    /**
     * Get active username
     *
     * @param uuid UUID of player.
     */
    protected String getActiveUsername(String uuid) {
        return this.getAUsername(uuid);
    }

    protected int getUI(String uuid) {
        return this.getUserIndex(uuid);
    }
}
