package com.happybavarian07.reportplugin.handling;

import com.happybavarian07.reportplugin.reports.ReportPlugin;
import com.happybavarian07.reportplugin.utils.StartUpLogger;
import com.happybavarian07.reportplugin.utils.ChatUtils;
import org.bukkit.ChatColor;

import java.sql.*;

public class MySQL {

    private StartUpLogger slogger;

    private Connection connection;

    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private String tablePrefix;
    private ReportPlugin plugin;

    /**
     * data: host, port, database, username, password
     * @param data
     */
    public MySQL(Object[] data) {
        new MySQL((String) data[0], (Integer) data[1], (String) data[2], (String) data[3], (String) data[4]);
    }

    public MySQL(String host, Integer port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        plugin = ReportPlugin.getInstance();
        slogger = plugin.getStartUpLogger();
        this.tablePrefix = plugin.getConfig().getString("MySQL.tablePrefix");
    }

    public Connection getConnection() {
        return connection;
    }

    public String getDatabase() {
        return database;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getUser() {
        return user;
    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public String getTablePrefix() { return this.tablePrefix; }

    public void connect() {
        try {
            if (!this.isConnected()) {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
                slogger.message(plugin.getMySQLPrefix() + ChatUtils.format("&2successfully connected!", null, false));
            } else {
                plugin.getStartUpLogger().coloredSpacer(ChatColor.RED).message(plugin.getMySQLPrefix() + ChatUtils.format("&4Something tried to Connect the Server to MySQL but its already connected!", null, false)).coloredSpacer(ChatColor.RED);
            }
        } catch (SQLException throwables) {
            plugin.getStartUpLogger().coloredSpacer(ChatColor.RED).message(plugin.getMySQLPrefix() + ChatUtils.format("&4disconnected!", null, false)).coloredSpacer(ChatColor.RED);
            throwables.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (this.isConnected()) {
                this.connection.close();
                plugin.getStartUpLogger().message(plugin.getMySQLPrefix() + ChatUtils.format("&2successfully disconnected!", null, false));
            } else {
                plugin.getStartUpLogger().coloredSpacer(ChatColor.RED).message(plugin.getMySQLPrefix() + ChatUtils.format("&4Something tried to Disconnect the Server from MySQL but its not connected!", null, false)).coloredSpacer(ChatColor.RED);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createTable() {
        try {
            PreparedStatement st = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS "
                    + plugin.getConfig().getString("MySQL.tablePrefix")
                    + "reports(NAME VARCHAR(16), UUID VARCHAR(35), reason VARCHAR(100), operator VARCHAR(16), status VARCHAR(30))");
            st.executeUpdate();
            plugin.getStartUpLogger().message(plugin.getMySQLPrefix() + ChatUtils.format("&2successfully created Table/s in database: " + this.database + "!", null, false));
        } catch (SQLException throwables) {
            plugin.getStartUpLogger().coloredSpacer(ChatColor.RED).message(plugin.getMySQLPrefix() + ChatUtils.format("&4failed to create Table/s in database: " + this.database + "!", null, false)).coloredSpacer(ChatColor.RED);
            throwables.printStackTrace();
        }
    }
}
