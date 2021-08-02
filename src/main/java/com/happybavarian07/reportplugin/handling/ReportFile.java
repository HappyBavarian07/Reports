package com.happybavarian07.reportplugin.handling;

import com.happybavarian07.reportplugin.reports.ReportPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ReportFile {

    private final ReportPlugin plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public ReportFile(ReportPlugin plugin) {
        this.plugin = plugin;
        // saves/initializes the Config
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if(this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), "reports.yml");

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource("reports.yml");
        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public File getConfigFile() {
        return configFile;
    }

    public FileConfiguration getConfig() {
        if(this.dataConfig == null)
            reloadConfig();

        return this.dataConfig;
    }

    public void saveConfig() {
        if(this.dataConfig == null || this.configFile == null)
            return;

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save Report Config to " + this.configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if(this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), "reports.yml");

        if(!this.configFile.exists()) {
            this.plugin.saveResource("reports.yml", false);
        }
    }
}
