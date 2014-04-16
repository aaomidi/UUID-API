package com.aaomidi.dev.uuidapi.utils;


import com.aaomidi.dev.uuidapi.UUIDAPI;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReader {
    private final UUIDAPI _plugin;
    private final FileConfiguration config;
    @Getter
    private String databaseHost;
    @Getter
    private int databasePort;
    @Getter
    private String databaseName;
    @Getter
    private String databaseUsername;
    @Getter
    private String databasePassword;

    public ConfigReader(UUIDAPI plugin) {
        _plugin = plugin;
        config = _plugin.getConfig();
        this.getDatabaseInformation();
    }

    /**
     * Initialize the database settings.
     */
    private void getDatabaseInformation() {
        this.databaseHost = this.config.getString("MySQL-Settings.Hostname");
        this.databasePort = this.config.getInt("MySQL-Settings.Port");
        this.databaseName = this.config.getString("MySQL-Settings.Database");
        this.databaseUsername = this.config.getString("MySQL-Settings.Username");
        this.databasePassword = this.config.getString("MySQL-Settings.Password");
    }

}
