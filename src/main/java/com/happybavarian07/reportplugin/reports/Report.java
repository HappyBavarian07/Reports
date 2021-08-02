package com.happybavarian07.reportplugin.reports;

import com.happybavarian07.reportplugin.handling.MySQL;
import com.happybavarian07.reportplugin.handling.MySQLReportHandler;
import com.happybavarian07.reportplugin.handling.ReportFile;
import com.happybavarian07.reportplugin.handling.ReportFileHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Report {
    private OfflinePlayer reported;
    private UUID UUIDFromReported;
    private OfflinePlayer operator;
    private String reason;
    private ReportStatus reportStatus;
    private ReportFile reportFile;
    private ReportFileHandler reportFileHandler;
    //private final int reportID;
    private final String tablePrefix;

    private final ReportPlugin plugin;
    private final MySQL mySQL;

    public Report(OfflinePlayer reported, OfflinePlayer operator, String reason/*, Integer reportID*/) {
        this.reported = reported;
        this.UUIDFromReported = reported.getUniqueId();
        this.operator = operator;
        this.reason = reason;
        this.reportStatus = ReportStatus.NEW;
        //this.reportID = reportID;
        this.plugin = ReportPlugin.getInstance();
        if(plugin.useMySQL()) {
            this.mySQL = plugin.getMySQL();
            this.tablePrefix = mySQL.getTablePrefix();
            this.reportFileHandler = null;
            this.reportFile = null;
        } else {
            this.mySQL = null;
            this.tablePrefix = null;
            this.reportFileHandler = plugin.getReportFileHandler();
            this.reportFile = reportFileHandler.getReportFile();
        }
    }

    public Report(UUID reportedUUID) {
        this.plugin = ReportPlugin.getInstance();
        this.reported = null;
        this.UUIDFromReported = reportedUUID;
        this.operator = null;
        this.reason = null;
        this.reportStatus = null;
        //this.reportID = reportID;
        if(plugin.useMySQL()) {
            this.mySQL = plugin.getMySQL();
            this.tablePrefix = mySQL.getTablePrefix();
            this.reportFileHandler = null;
            this.reportFile = null;

            MySQLReportHandler mySQLReportHandler = new MySQLReportHandler(mySQL);
            if(!isReported()) return;

            this.reported = Bukkit.getOfflinePlayer(mySQLReportHandler.getName(reportedUUID.toString()));
            this.UUIDFromReported = reportedUUID;
            this.operator = Bukkit.getOfflinePlayer(mySQLReportHandler.getOperator(reportedUUID.toString()));
            this.reason = mySQLReportHandler.getReason(reportedUUID.toString());
            this.reportStatus = mySQLReportHandler.getReportStatus(reportedUUID.toString());
        } else {
            this.mySQL = null;
            this.tablePrefix = null;
            this.reportFileHandler = new ReportFileHandler();
            this.reportFile = reportFileHandler.getReportFile();
            if(!isReported()) return;

            this.reported = Bukkit.getOfflinePlayer(reportFileHandler.getName(reportedUUID.toString()));
            this.UUIDFromReported = reportedUUID;
            this.operator = Bukkit.getOfflinePlayer(reportFileHandler.getOperator(reportedUUID.toString()));
            this.reason = reportFileHandler.getReason(reportedUUID.toString());
            this.reportStatus = reportFileHandler.getReportStatus(reportedUUID.toString());
        }
    }

    public ReportFile getReportFile() {
        return reportFile;
    }

    public ReportFileHandler getReportFileHandler() {
        return reportFileHandler;
    }

    public String getReason() {
        return reason;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    /*public int getReportID() {
        return reportID;
    }*/

    public OfflinePlayer getOperator() {
        return operator;
    }

    public OfflinePlayer getReported() {
        return reported;
    }

    public void setReportStatus(ReportStatus reportStatusToSet) {
        if(plugin.useMySQL()) {
            if(!isReported()) return;
            try {
                PreparedStatement report = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getTablePrefix() + "reports SET status = ? WHERE UUID = ?");
                report.setString(1, reportStatusToSet.toString());
                report.setString(2, getUUIDFromReported().toString());
                report.executeUpdate();
                this.reportStatus = reportStatusToSet;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            reportFileHandler.setReportStatus(this.getUUIDFromReported().toString(), reportStatusToSet);
            this.reportStatus = reportStatusToSet;
        }
    }

    public UUID getUUIDFromReported() {
        return UUIDFromReported;
    }

    public void addReport() {
        if(plugin.useMySQL()) {
            if(isReported()) return;
            try {
                PreparedStatement report = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getTablePrefix() + "reports(NAME,UUID,reason,operator,status) VALUES (?,?,?,?,?)");
                report.setString(1, this.reported.getName());
                report.setString(2, getUUIDFromReported().toString());
                report.setString(3, this.reason);
                report.setString(4, this.operator.getName());
                report.setString(5, reportStatus.toString());
                report.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            reportFileHandler.addReport(this.reported.getName(), this.getUUIDFromReported().toString(), this.getReason(), this.getOperator().getName(), this.getReportStatus().toString());
        }
    }

    public void removeReport() {
        if(plugin.useMySQL()) {
            if(!isReported()) return;
            try {
                PreparedStatement report = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getTablePrefix() + "reports WHERE UUID = ?");
                report.setString(1, getUUIDFromReported().toString());
                report.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            reportFileHandler.removeReport(this.getUUIDFromReported().toString());
        }
    }

    public boolean isReported() {
        if(plugin.useMySQL()) {
            try {
                PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT NAME FROM " + tablePrefix + "reports WHERE UUID = ?");
                ps.setString(1, getUUIDFromReported().toString());
                ResultSet rs = ps.executeQuery();
                return rs.next();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            return reportFile.getConfig().contains("Reports." + getUUIDFromReported().toString());
        }
        return false;
    }
}
