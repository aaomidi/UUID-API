package com.aaomidi.dev.uuidapi;


import com.aaomidi.dev.uuidapi.storage.DatabaseAPI;
import com.aaomidi.dev.uuidapi.storage.DatabaseConnector;
import com.aaomidi.dev.uuidapi.utils.ConfigReader;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class UUIDAPI extends JavaPlugin {
    @Getter
    private ConfigReader configReader;
    @Getter
    private DatabaseConnector databaseConnector;
    @Getter
    private DatabaseAPI databaseAPI;
    @Getter
    private DatabaseManager databaseManager;

    public final void onEnable() {

    }

    public final void onDisable() {

    }

    /**
     * Get the username history of a identifier.
     *
     * @param identifier Name of Player (Player MUST be online) / UUID of player (Enter without dashes).
     * @return ArrayList with the history of usernames.
     */
    public ArrayList<String> getUsernameHistory(String identifier) {
        if (identifier.length() == 32) {
             return this.getDatabaseManager().getOldUsernames(identifier);
        } else {
            if (this.getServer().getPlayer(identifier) != null) {
                String uuid = this.getServer().getPlayer(identifier).getUniqueId().toString().replace("-", "");
                return this.getUsernameHistory(uuid);
            } else {
                return null;
            }
        }
    }

    /**
     * Get active username of a uuid.
     *
     * @param uuid Unique identifier.
     * @return Active username of uuid.
     */
    public String getActiveUsername(String uuid) {
       return this.getDatabaseManager().getActiveUsername(uuid);
    }


}