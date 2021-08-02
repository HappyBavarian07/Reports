package com.happybavarian07.reportplugin.handling;

import com.happybavarian07.reportplugin.reports.Report;
import com.happybavarian07.reportplugin.reports.ReportPlugin;
import com.happybavarian07.reportplugin.reports.ReportStatus;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ReportFileHandler {
    private final ReportFile reportFile;
    private final FileConfiguration configuration;

    public ReportFileHandler() {
        ReportPlugin plugin = ReportPlugin.getInstance();
        this.reportFile = new ReportFile(plugin);
        this.configuration = reportFile.getConfig();
    }

    public String getReason(String uuid) {
        return configuration.getString("Reports." + uuid + ".Reason");
    }

    public String getName(String uuid) {
        return configuration.getString("Reports." + uuid + ".Name");
    }

    public String getOperator(String uuid) {
        return configuration.getString("Reports." + uuid + ".Operator");
    }

    private String getUUID(String uuid) {
        return configuration.getString("Reports." + uuid + ".UUID");
    }

    public ReportStatus getReportStatus(String uuid) {
        return ReportStatus.valueOf(configuration.getString("Reports." + uuid + ".Status").toUpperCase());
    }

    public void setReportStatus(String uuid, ReportStatus reportStatus) {
        configuration.set("Reports." + uuid + ".Status", reportStatus.toString());
        try {
            configuration.save(getReportFile().getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeReport(String uuid) {
        configuration.set("Reports." + uuid, null);
        try {
            configuration.save(getReportFile().getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addReport(String name, String uuid, String reason, String operator, String status) {
        configuration.set("Reports." + uuid + ".Name", name);
        configuration.set("Reports." + uuid + ".UUID", uuid);
        configuration.set("Reports." + uuid + ".Operator", operator);
        configuration.set("Reports." + uuid + ".Reason", reason);
        configuration.set("Reports." + uuid + ".Status", status);
        try {
            configuration.save(getReportFile().getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReportFile getReportFile() {
        return reportFile;
    }

    public FileConfiguration getReportConfig() {
        return configuration;
    }

    public Set<Report> getAllReports() {
        Set<Report> reports = new HashSet<>();
        if(!configuration.contains("Reports")) {
            return reports;
        }
        configuration.getConfigurationSection("Reports").getKeys(false).forEach(toAdd -> reports.add(new Report(UUID.fromString(toAdd))));
        return reports;
    }
}
