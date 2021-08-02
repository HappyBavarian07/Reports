package com.happybavarian07.reportplugin.reports;

import com.happybavarian07.reportplugin.handling.MySQL;
import com.happybavarian07.reportplugin.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ReportInv extends ChatUtils implements Listener {

    static int task;
    private static ReportPlugin plugin;
    public static List<Inventory> invs = new ArrayList<>();
    private static MySQL mySQL;

    public ReportInv() {
        plugin = ReportPlugin.getInstance();
        mySQL = plugin.getMySQL();
    }

    public static void initializeItems(Inventory inv, Player player) {
        task = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) == null) {
                    inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " "));
                }
            }
            try {
                List<Report> reportedUUIDs = new ArrayList<>();
                if(plugin.useMySQL()) {
                    PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT UUID FROM " + mySQL.getTablePrefix() + "reports");
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        reportedUUIDs.add(new Report(UUID.fromString(rs.getString("UUID"))));
                    }
                } else {
                    if(plugin.getReportFileHandler().getAllReports().isEmpty()) return;

                    reportedUUIDs.addAll(plugin.getReportFileHandler().getAllReports());
                }
                int count = 0;
                for (Report reportedUUID : reportedUUIDs) {
                    String status = "&4Error";
                    String reason = reportedUUID.getReason();
                    String reported = reportedUUID.getReported().getName();
                    String reportedby = reportedUUID.getOperator().getName();
                    if (reportedUUID.getReportStatus().equals(ReportStatus.NEW)) {
                        status = "&fNew";
                    }
                    if (reportedUUID.getReportStatus().equals(ReportStatus.INVESTIGATED)) {
                        status = "&eBeing Investigated";
                    }
                    if (reportedUUID.getReportStatus().equals(ReportStatus.COMPLETE)) {
                        status = "&aComplete";
                    }
                    inv.setItem(count, createGuiItem(Material.PAPER, format(reported, player, false),
                            "        ",
                            format("&3Â >> &fStatus &3Â >> " + status, player, false),
                            "        ",
                            format("&3Â >> &fReported by &3Â >> " + reportedby, player, false),
                            format("&3Â >> &fReported &3Â >> " + reported, player, false),
                            "        ",
                            format("&3Â >> &fReason &3Â >> " + reason, player, false),
                            "        ",
                            format("&7(Left-Click to teleport to " + reported + ")", player, false),
                            format("&7(Right-Click to remove this Report)", player, false),
                            format("&7(Middle-Click to update the Report Status)", player, false)));
                    count++;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }, 10L, 50L);
    }

    // Nice little method to create a gui item with a custom name, and description
    protected static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        assert meta != null;
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public static void openInv(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 45, "§5§lReport Inv");
        initializeItems(inventory, player);
        invs.add(inventory);
        player.openInventory(inventory);
    }

    // Check for clicks on items
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!invs.contains(e.getInventory())) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        /*
        TODO [21:14:43] [Craft Scheduler Thread - 63/WARN]: [Reports] Plugin Reports v1.0 generated an exception while executing task 755
        TODO java.lang.NullPointerException: null
            at com.happybavarian07.reportplugin.reports.ReportInv.lambda$initializeItems$1(ReportInv.java:58) ~[?:?]
        	at org.bukkit.craftbukkit.v1_16_R3.scheduler.CraftTask.run(CraftTask.java:81) ~[spigot-1.16.5.jar:2991-Spigot-018b9a0-f3f3094]
        	at org.bukkit.craftbukkit.v1_16_R3.scheduler.CraftAsyncTask.run(CraftAsyncTask.java:54) [spigot-1.16.5.jar:2991-Spigot-018b9a0-f3f3094]
        	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128) [?:?]
        	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628) [?:?]
        	at java.lang.Thread.run(Thread.java:830) [?:?]
        	*/
        Player player = (Player) e.getWhoClicked();
        if (clickedItem.getType() == Material.PAPER) {
            OfflinePlayer reported = Bukkit.getOfflinePlayer(clickedItem.getItemMeta().getDisplayName());
            Report report = new Report(reported.getUniqueId());
            if (player.hasPermission("reports.review")) {
                // Delete
                if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                    if (report.getReportStatus().equals(ReportStatus.COMPLETE)) {
                        report.removeReport();
                        player.sendMessage(format("&aSuccessfully removed Report!", player, true));
                        if(report.getOperator() != null && report.getOperator().isOnline()) {
                            report.getOperator().getPlayer().sendMessage(format("&aYour recent Report has been reviewed and handled! Thank You, for Reporting!", report.getOperator().getPlayer(), true));
                        }
                    } else if (report.getReportStatus().equals(ReportStatus.NEW) || report.getReportStatus().equals(ReportStatus.INVESTIGATED)) {
                        report.removeReport();
                        player.sendMessage(format("&aSuccessfully&c denied &athe Report!", player, true));
                        if(report.getOperator() != null && report.getOperator().isOnline()) {
                            report.getOperator().getPlayer().sendMessage(format("&aYour recent Report has been reviewed and &cdenied&a! Thank You, for Reporting!", report.getOperator().getPlayer(), true));
                        }
                    }
                    player.closeInventory();
                    return;
                }
                if (report.getReportStatus().equals(ReportStatus.COMPLETE)) {
                    player.sendMessage(format("&cThis Reported was already handled from someone else!", player, true));
                    return;
                }
                // TP To Player
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    if (reported.isOnline()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + player.getName() + " " + report.getReported().getName());
                    } else {
                        player.sendMessage(format("&4Target is not online!", player, true));
                    }
                    player.closeInventory();
                    return;
                }
                // Change Status
                if (e.getAction().equals(InventoryAction.DROP_ONE_SLOT)) {
                    if (report.getReportStatus().equals(ReportStatus.NEW)) {
                        report.setReportStatus(ReportStatus.INVESTIGATED);
                    } else if (report.getReportStatus().equals(ReportStatus.INVESTIGATED)) {
                        report.setReportStatus(ReportStatus.COMPLETE);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        if (invs.contains(e.getInventory())) {
            Bukkit.getScheduler().cancelTask(task);
        }
    }

    // Cancel dragging in our inventory
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryDragEvent e) {
        if (invs.contains(e.getInventory())) {
            e.setCancelled(true);
        }
    }
}