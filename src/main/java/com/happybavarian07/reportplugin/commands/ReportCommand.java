package com.happybavarian07.reportplugin.commands;

import com.happybavarian07.reportplugin.reports.Report;
import com.happybavarian07.reportplugin.reports.ReportPlugin;
import com.happybavarian07.reportplugin.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand extends ChatUtils implements CommandExecutor {

    private final ReportPlugin plugin;

    public ReportCommand() {
        this.plugin = ReportPlugin.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(format("&4You have to be a Player!", null, true));
            return true;
        }
        Player player = (Player) sender;
        Report report;
        if(args.length == 2) {
            if(!args[0].equalsIgnoreCase("reload")) {
                if(!player.hasPermission("reports.report")) {
                    player.sendMessage(format("&4You don't have Permissions to do that!", player, true));
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                String reason = args[1];
                if(target == null) {
                    player.sendMessage(format("&4Target is null!", player, true));
                    return true;
                }
                if(target == player) {
                    player.sendMessage(format("&4You cannot report yourself!", player, true));
                    return true;
                }
                report = new Report(target, player, reason);
                if(report.isReported()) {
                    player.sendMessage(format("&4This Player is already reported!", player, true));
                    return true;
                }
                report.addReport();
                player.sendMessage(format("&2Report has been send!", player, true));
            } else {
                if(args[0].equalsIgnoreCase("reload")) {
                    if(!player.hasPermission("reports.reload")) {
                        player.sendMessage(format("&4You don't have Permissions to do that!", player, true));
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("Config")) {
                        plugin.reloadConfig();
                        player.sendMessage(format("&aReloaded the Config successfully!", player, true));
                    }
                    if(args[1].equalsIgnoreCase("File")) {
                        if(plugin.useMySQL()) {
                            player.sendMessage(format("&cReport File cannot be reloaded because it is not activated! &6Run: /report reload MySQL instead", player, true));
                            return true;
                        }
                        plugin.reloadReportFile();
                        player.sendMessage(format("&aReloaded the File Database successfully!", player, true));
                    }
                    if(args[1].equalsIgnoreCase("MySQL")) {
                        if(!plugin.useMySQL()) {
                            player.sendMessage(format("&cMySQL cannot be reloaded because it is not activated! &6Run: /report reload File instead", player, true));
                            return true;
                        }
                        if(plugin.getMySQL() == null) {
                            plugin.setupMySQL();
                        }
                        plugin.getMySQL().disconnect();
                        plugin.getMySQL().connect();
                        player.sendMessage(format("&aReloaded the MySQL Database successfully!", player, true));
                    }
                    if(args[1].equalsIgnoreCase("Plugin")) {
                        Bukkit.getPluginManager().disablePlugin(plugin);
                        Bukkit.getPluginManager().enablePlugin(plugin);
                        player.sendMessage(format("&aReloaded the Plugin successfully!", player, true));
                    }
                }
            }
        }
        return true;
    }
}
