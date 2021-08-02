package com.happybavarian07.reportplugin.commands;

import com.happybavarian07.reportplugin.reports.Report;
import com.happybavarian07.reportplugin.reports.ReportInv;
import com.happybavarian07.reportplugin.reports.ReportPlugin;
import com.happybavarian07.reportplugin.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReportsCommand extends ChatUtils implements CommandExecutor {

    private final ReportPlugin plugin;

    public ReportsCommand() {
        this.plugin = ReportPlugin.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(format("&4You have to be a Player!", null, true));
            return true;
        }
        Player player = (Player) sender;
        if(args.length == 0) {
            if(player.hasPermission("reports.list")) {
                ReportInv.openInv(player);
                player.sendMessage(format("&aList of all Reports!", player, true));
                return true;
            }
        }
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("clear") && player.hasPermission("reports.clear")) {
                try {
                    if(plugin.useMySQL()) {
                        PreparedStatement clearAll = plugin.getMySQL().getConnection().prepareStatement("DELETE FROM " + plugin.getMySQL().getTablePrefix() + "reports");
                        clearAll.executeUpdate();
                    } else {
                        plugin.getReportFileHandler().getReportConfig().set("Reports", null);
                    }
                    player.sendMessage(format("&2Successfully removed all Reports!", player, true));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                return true;
            }
            if(!player.hasPermission("reports.listplayer")) {
                player.sendMessage(format("&4You don't have Permissions to do that!", player, true));
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            Report report = new Report(target.getUniqueId());
            if(report.getReported() == null || report.getOperator() == null) {
                player.sendMessage(format("&4This Player isn't reported yet!", player, true));
                return true;
            }
            player.sendMessage(format("&2+-----------------------------------------+", player, true));
            player.sendMessage(format("&2Player: &6" + report.getReported().getName(), player, true));
            player.sendMessage(format("&2By: &6" + report.getOperator().getName(), player, true));
            player.sendMessage(format("&2Reason: &6" + report.getReason(), player, true));
            player.sendMessage(format("&2Status: &6" + report.getReportStatus().toString(), player, true));
            player.sendMessage(format("&2+-----------------------------------------+", player, true));
        }
        return true;
    }
}
