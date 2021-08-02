package com.happybavarian07.reportplugin.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static String format(String string, Player player, boolean prefix) {
        if(prefix) {
            return PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', "%reports_prefix%&3Â >> " + string));
        }
        return PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', string));
    }
}
