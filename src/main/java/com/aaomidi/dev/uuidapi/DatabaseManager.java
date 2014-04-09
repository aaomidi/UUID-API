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

    private boolean createTables() {
        String query = "CREATE TABLE IF NOT EXISTS `uuid_api`(`uuid` VARCHAR(32) NOT NULL, `username` VARCHAR(16), `isMain` BOOLEAN, PRIMARY KEY (`uuid`,`userName`))ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try {
            databaseAPI.executeUpdate(query);
            return true;
        } catch (Exception ex) {
            _plugin.getLogger().log(Level.SEVERE, "ERROR OCCURRED WHEN CREATING THE TABLE. \n StackTrace: " + ex.getMessage() + "\nDisabling plugin.");
            _plugin.getServer().getPluginManager().disablePlugin(_plugin);
            return false;
        }
    }

    protected boolean initializeUser(Player player) {
        String query = "INSERT IGNORE INTO `uuid_api` (`uuid`,`username`,`isMain`) VALUES (?,?,?);";
        String uuid = player.getUniqueId().toString().replace("-", "");
        this.setAllOldUsernamesInactive(uuid);
        try {
            databaseAPI.executeUpdate(query, uuid, player.getName(), true);
            return true;
        } catch (Exception ex) {
            _plugin.getLogger().log(Level.SEVERE, "ERROR OCCURRED WHEN INITIALIZING THE PLAYER " + player.getName() + "\nStackTrace: " + ex.getMessage() + "\nDisabling plugin");
            _plugin.getServer().getPluginManager().disablePlugin(_plugin);
            return false;
        }
    }

    private boolean setAllOldUsernamesInactive(String uuid) {
        String query = "SET `isMain`=? WHERE `uuid`=? AND `isMain`=?";
        try {
            databaseAPI.executeUpdate(query, false, uuid, true);
            return true;
        } catch (Exception ex) {
            _plugin.getLogger().log(Level.SEVERE, "ERROR OCCURRED WHEN SETTING THE isMain to FALSE.\n StackTrace: " + ex.getMessage() + "\nDisabling plugin.");
            _plugin.getServer().getPluginManager().disablePlugin(_plugin);
            return false;
        }

    }

    protected ArrayList<String> getOldUsernames(String uuid) {
        String query = "SELECT `username` FROM `uuid_api`WHERE `uuid`=?";
        ArrayList<String> usernames = new ArrayList<>();
        try {
            ResultSet resultSet = databaseAPI.executeQuery(uuid);
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

    protected String getActiveUsername(String uuid) {
        String query = "SELECT `username` FROM `uuid_api` WHERE `uuid`=? AND `isMain`=?";
        try {
            ResultSet resultSet = databaseAPI.executeQuery(query, uuid, true);
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

    public boolean initializePlayer(Player player) {
        return this.initializeUser(player);
    }
}
