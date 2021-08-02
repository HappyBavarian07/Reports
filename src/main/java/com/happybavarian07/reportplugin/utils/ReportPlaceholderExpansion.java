package com.happybavarian07.reportplugin.utils;

import com.happybavarian07.reportplugin.reports.ReportPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReportPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "reports";
    }

    @Override
    public @NotNull String getAuthor() {
        return "HappyBavarian07";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player == null) {
            return null;
        }
        if(params.equals("prefix")) {
            return ReportPlugin.getInstance().getPrefix();
        }
        if(params.equals("mysql_prefix")) {
            return ReportPlugin.getInstance().getMySQLPrefix();
        }
        return null;
    }
}
