package com.aaomidi.dev.uuidapi.storage;


import com.aaomidi.dev.uuidapi.UUIDAPI;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseAPI {
    private final UUIDAPI _plugin;
    private final DatabaseConnector database;

    public DatabaseAPI(UUIDAPI plugin) {
        _plugin = plugin;
        database = _plugin.getDatabaseConnector();

    }

    /**
     * @param query
     * @param parameters
     */
    public void executeUpdate(String query, Object... parameters) {
        this.database.executeUpdate(query, parameters);

    }

    /**
     * @param query
     * @param parameters
     * @return ResultSet
     */
    public ResultSet executeQuery(String query, Object... parameters) {
        return this.database.executeQuery(query, parameters);
    }

    /**
     * Reconnect to Database;
     */
    public void reconnect() {
        try {
            this.database.reconnect();
        } catch (SQLException ex) {
            this._plugin.getLogger().log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Disconnect from Database;
     */
    public void disconnect() {
        this.database.disconnect();
    }

    /**
     * @param query
     * @param result
     * @param parameters
     */
    public void buildAndFetchColumn(String query, String result, Object... parameters) {
        this.buildAndFetchColumn(query, result, parameters);
    }


}
