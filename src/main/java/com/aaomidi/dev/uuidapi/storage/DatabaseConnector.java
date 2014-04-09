package com.aaomidi.dev.uuidapi.storage;


import com.aaomidi.dev.uuidapi.UUIDAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.logging.Level;

public class DatabaseConnector {
    private final UUIDAPI _plugin;
    private Connection connection;
    private ResultSet resultSet;
    private String databaseHost;
    private String databasePort;
    private String databaseName;
    private String databaseUser;
    private String databasePass;

    public DatabaseConnector(UUIDAPI plugin) throws SQLException {
        _plugin = plugin;
        this.connect();

    }

    public void connect() throws SQLException {
        FileConfiguration config = this._plugin.getConfig();
        this.databaseHost = _plugin.getConfigReader().getDatabaseHost();
        this.databasePort = String.valueOf(_plugin.getConfigReader().getDatabasePort());
        this.databaseName = _plugin.getConfigReader().getDatabaseName();
        this.databaseUser = _plugin.getConfigReader().getDatabaseUsername();
        this.databasePass = _plugin.getConfigReader().getDatabasePassword();
        this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", this.databaseHost, this.databasePort, this.databaseName), this.databaseUser, this.databasePass);
    }

    public void connect(String databaseHost, String databasePort, String databaseName, String databaseUser, String databasePass) {
        String url = String.format("jdbc:mysql://%s:%s/%s", databaseHost, databasePort, databaseName);
        try {
            this.connection = DriverManager.getConnection(url, databaseUser, databasePass);
        } catch (SQLException ex) {
            this.log(Level.SEVERE, String.format("Error whilst connecting to database '%s'", url), ex);
            _plugin.getServer().getPluginManager().disablePlugin(_plugin);
            _plugin.getLogger().log(Level.SEVERE, "DISABLING PLUGIN, CANNOT CONNECT TO DATABASE!");
        }
    }

    public void disconnect() {
        try {
            this.connection.close();
        } catch (SQLException ex) {
            this.log(Level.SEVERE, "Error whilst disconnecting from database. Was the connection ever opened?", ex);
        }
    }

    public void reconnect() throws SQLException {
        this.connection.close();
        this.connect(this.databaseHost, this.databasePort, this.databaseName, this.databaseUser, this.databasePass);
    }

    private void checkConnection() {
        try {
            if (this.connection == null) {
                this.log(Level.WARNING, "Connection is null. You were never connected.");
                return;
            }

            if (!this.connection.isValid(2)) {
                this.reconnect();
            }
        } catch (SQLException ex) {
            this.log(Level.SEVERE, "Error whilst checking connection. Was the connection ever opened?", ex);
        }
    }

    public ResultSet executeQuery(String query, Object... parameters) {
        this.checkConnection();

        int parameterCount = parameters == null ? 0 : parameters.length;
        if (StringUtils.countMatches(query, "?") != parameterCount) {
            this.log(Level.SEVERE, "PreparedStatement Error: Incorrect number of '?' for number of insertUpdate locations!");
            return null;
        }

        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            Object parameter;

            for (int i = 0, j = 1; i < parameterCount; i++, j++) {
                parameter = parameters[i];

                if (parameter instanceof String) {
                    statement.setString(j, (String) parameter);
                } else if (parameter instanceof Integer) {
                    statement.setInt(j, (Integer) parameter);
                } else if (parameter instanceof Boolean) {
                    statement.setBoolean(j, (Boolean) parameter);
                } else {
                    statement.setObject(j, parameter);
                }
            }

            this.resultSet = statement.executeQuery();
            return this.resultSet;
        } catch (SQLException ex) {
            this.log(Level.SEVERE, "Error whilst querying database!", ex);
        }

        return null;
    }

    public int executeUpdate(String query, Object... parameters) {
        this.checkConnection();

        int parameterCount = parameters == null ? 0 : parameters.length;
        if (StringUtils.countMatches(query, "?") != parameterCount) {
            this.log(Level.SEVERE, "PreparedStatement Error: Incorrect number of '?' for number of insertUpdate locations!");
            return -1;
        }

        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            Object parameter;

            for (int i = 0, j = 1; i < parameterCount; i++, j++) {
                parameter = parameters[i];

                if (parameter instanceof String) {
                    statement.setString(j, (String) parameter);
                } else if (parameter instanceof Integer) {
                    statement.setInt(j, (Integer) parameter);
                } else if (parameter instanceof Boolean) {
                    statement.setBoolean(j, (Boolean) parameter);
                } else {
                    statement.setObject(j, parameter);
                }
            }

            int result = statement.executeUpdate();
            return result;
        } catch (SQLException ex) {
            this.log(Level.SEVERE, "Error whilst querying database!", ex);
        }

        return -1;
    }

    public Object buildAndFetchColumn(String query, String result, Object... parameters) {
        this.executeQuery(query, parameters);

        try {
            Object objectResult = this.resultSet.getObject(result);

            if (objectResult instanceof String) {
                return this.resultSet.getString(result);
            } else if (objectResult instanceof Integer) {
                return this.resultSet.getInt(result);
            } else if (objectResult instanceof Boolean) {
                return this.resultSet.getBoolean(result);
            }

            return objectResult;
        } catch (SQLException ex) {
            this.log(Level.SEVERE, "Could not retrieve data from fetch!", ex);
        }

        return null;
    }

    public ResultSet build(String query, Object... parameters) {
        this.executeQuery(query, parameters);
        return this.resultSet;
    }

    public ResultSet getResultSet() {
        return this.resultSet;
    }

    private void log(Level level, String message) {
        this._plugin.getLogger().log(level, String.format("[MySQL] %s", message));
    }

    private void log(Level level, String message, Throwable ex) {
        this._plugin.getLogger().log(level, message, ex);
    }
}



