package com.happybavarian07.reportplugin.handling;

import com.happybavarian07.reportplugin.reports.ReportStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLReportHandler {
    private MySQL mySQL;

    public MySQLReportHandler(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    public String getReason(String uuid) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT reason FROM " + mySQL.getTablePrefix() + "reports WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString("reason");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    public String getName(String uuid) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT NAME FROM " + mySQL.getTablePrefix() + "reports WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString("NAME");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    private Integer getReportID(String uuid) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT ID FROM " + mySQL.getTablePrefix() + "reports WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }

    public String getOperator(String uuid) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT operator FROM " + mySQL.getTablePrefix() + "reports WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString("operator");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    private String getUUID(String uuid) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT UUID FROM " + mySQL.getTablePrefix() + "reports WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString("UUID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    public ReportStatus getReportStatus(String uuid) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT status FROM " + mySQL.getTablePrefix() + "reports WHERE UUID = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return ReportStatus.valueOf(rs.getString("status").toUpperCase());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void removeReport(String uuid) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getTablePrefix() + "reports WHERE UUID = ?");
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addReport(String name, String uuid, String reason, String operator, String status) {
        try {
            PreparedStatement report = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getTablePrefix() + "reports(NAME,UUID,reason,operator,status) VALUES (?,?,?,?,?)");
            report.setString(1, name);
            report.setString(2, uuid);
            report.setString(3, reason);
            report.setString(4, operator);
            report.setString(5, status);
            report.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
