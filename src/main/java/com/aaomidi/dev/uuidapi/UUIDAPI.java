package com.aaomidi.dev.uuidapi;


import com.aaomidi.dev.uuidapi.events.LoginEvent;
import com.aaomidi.dev.uuidapi.storage.DatabaseAPI;
import com.aaomidi.dev.uuidapi.storage.DatabaseConnector;
import com.aaomidi.dev.uuidapi.utils.ConfigReader;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

public class UUIDAPI extends JavaPlugin {
    @Getter
    private ConfigReader configReader;
    @Getter
    private DatabaseConnector databaseConnector;
    @Getter
    private DatabaseAPI databaseAPI;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private LoginEvent loginEvent;

    public final void onEnable() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
        }
        boolean value = true;
        if (!this.getServer().getOnlineMode()) {
            this.getLogger().log(Level.SEVERE, "Server is not running in online mode. This plugin requires online mode. Turning plugin off.");
            value = false;
        }
        if (value) {
            configReader = new ConfigReader(this);
            try {
                databaseConnector = new DatabaseConnector(this);
            } catch (SQLException e) {
                this.getLogger().log(Level.SEVERE, "Connecting to database was problematic. Disabling plugin to stop future crashes.");
                this.getServer().getPluginManager().disablePlugin(this);
            }
            databaseAPI = new DatabaseAPI(this);
            loginEvent = new LoginEvent(this);
            this.getServer().getPluginManager().registerEvents(loginEvent, this);
        } else {
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public final void onDisable() {
        databaseAPI.disconnect();
    }

    /**
     * Get the username history of a identifier.
     *
     * @param identifier Name of Player (Player MUST be online) / UUID of player (Enter without dashes).
     * @return ArrayList with the history of usernames.
     */
    public ArrayList<String> getUsernames(String identifier) {
        if (identifier.length() == 32) {
            return this.getDatabaseManager().getAllUsernames(identifier);
        } else {
            if (this.getServer().getPlayer(identifier) != null) {
                String uuid = this.getServer().getPlayer(identifier).getUniqueId().toString().replace("-", "");
                return this.getUsernames(uuid);
            } else {
                return null;
            }
        }
    }

    /**
     * Get active username of a uuid.
     *
     * @param uuid UUID of player.
     * @return Active username of uuid.
     */
    public String getActiveUsername(String uuid) {
        return this.getDatabaseManager().getActiveUsername(uuid);
    }

    /**
     * Get user index.
     *
     * @param uuid UUID of the player.
     * @return Index of the username.
     */
    public int getUserIndex(String uuid) {
        return this.getDatabaseManager().getUI(uuid);
    }


}