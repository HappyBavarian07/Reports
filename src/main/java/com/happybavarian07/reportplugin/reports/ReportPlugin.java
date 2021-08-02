package com.happybavarian07.reportplugin.reports;

import com.happybavarian07.reportplugin.commands.ReportCommand;
import com.happybavarian07.reportplugin.commands.ReportsCommand;
import com.happybavarian07.reportplugin.handling.ReportFileHandler;
import com.happybavarian07.reportplugin.utils.ChatUtils;
import com.happybavarian07.reportplugin.utils.ReportPlaceholderExpansion;
import com.happybavarian07.reportplugin.utils.StartUpLogger;
import com.happybavarian07.reportplugin.handling.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReportPlugin extends JavaPlugin implements Listener {

    private StartUpLogger startUpLogger;
    private static ReportPlugin instance;
    private String mySQLPrefix = "§7[§5My§6SQL§7] ";
    private String prefix = "§f[§cReports§f] §2";
    private MySQL mySQL;
    private ReportFileHandler reportFileHandler;
    //private MessagesManager messagesManager;

    @Override
    public void onEnable() {
        startUpLogger = new StartUpLogger();
        setIstance(this);
        saveDefaultConfig();
        //messagesManager = new MessagesManager(this);
        prefix = ChatUtils.format(getConfig().getString("Plugin.Prefix"), null, false);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            startUpLogger.message("&2Successfully recognized PlaceholderAPI!");
            new ReportPlaceholderExpansion().register();
        } else {
            startUpLogger.coloredSpacer(ChatColor.RED);
            startUpLogger.message("&4PlaceholderAPI is not installed or enabled or doesn't work properly,");
            startUpLogger.message("&4but this Plugin requires PlaceholderAPI to work!");
            startUpLogger.coloredSpacer(ChatColor.RED);
            Bukkit.getPluginManager().disablePlugin(this);
        }
        setupMySQL();
        setupCommandsAndEvents();
        startUpLogger.message(prefix + "&2successfully started!");
    }

    private void setupCommandsAndEvents() {
        getCommand("report").setExecutor(new ReportCommand());
        getCommand("reports").setExecutor(new ReportsCommand());
        Bukkit.getPluginManager().registerEvents(new ReportInv(), this);
    }

    public void reloadReportFile() {
        this.reportFileHandler.getReportFile().reloadConfig();
    }

    public void setupMySQL() {
        if(useMySQL()) {
            mySQLPrefix = ChatUtils.format(getConfig().getString("MySQL.Prefix"), null, false);
            mySQL = new MySQL(getConfig().getString("MySQL.host"),
                    getConfig().getInt("MySQL.port"),
                    getConfig().getString("MySQL.database"),
                    getConfig().getString("MySQL.username"),
                    getConfig().getString("MySQL.password"));
            mySQL.connect();
            mySQL.createTable();
            startUpLogger.message(prefix + "If you wish to use Files to save Reports! Please enable it in the Config");
        } else {
            reportFileHandler = new ReportFileHandler();
            startUpLogger.message(prefix + "If you wish to use MySQL to save Reports! Please enable it in the Config");
        }
    }

    @Override
    public void onDisable() {
        if(mySQL != null) mySQL.disconnect();
        mySQL = null;
        startUpLogger = null;
        setIstance(null);
        mySQLPrefix = null;
        prefix = null;
    }

    //public String getMessage(String path, Player player) {
    //    return ChatUtils.format(messagesManager.getConfig().getString(path), player);
    //}

    public boolean useMySQL() {
        return getConfig().getBoolean("MySQL.enabled");
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    //public MessagesManager getMessagesManager() {
    //    return messagesManager;
    //}

    public ReportFileHandler getReportFileHandler() {
        return reportFileHandler;
    }

    private void setIstance(ReportPlugin istance) {
        ReportPlugin.instance = istance;
    }

    public static ReportPlugin getInstance() {
        return instance;
    }

    public StartUpLogger getStartUpLogger() {
        return startUpLogger;
    }

    public String getMySQLPrefix() {
        return mySQLPrefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
