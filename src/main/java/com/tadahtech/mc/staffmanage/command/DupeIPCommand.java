package com.tadahtech.mc.staffmanage.command;

import com.google.common.collect.Lists;
import com.tadahtech.mc.staffmanage.PunishmentMessage;
import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.dupeip.DupeIPManager;
import com.tadahtech.mc.staffmanage.dupeip.PlayerConnectionInfo;
import com.tadahtech.mc.staffmanage.lang.Messaging;
import com.tadahtech.mc.staffmanage.lang.types.RegularMessage;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentSQLManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import com.tadahtech.mc.staffmanage.util.UtilMath;
import com.tadahtech.mc.staffmanage.util.UtilText;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DupeIPCommand implements CommandExecutor {

    private static final int PER_PAGE = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sms.dupeip")) {
            sender.sendMessage(Colors.RED + "You cannot run this command!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Colors.RED + "Only players can do this.");
            return true;
        }

        AtomicInteger page = new AtomicInteger(1);
        UUID test = UUID.randomUUID();
        UUID targetUUID = null;

        if (args.length >= 1) {
            String arg = args[0];
            Player target = Bukkit.getPlayer(arg);

            if (target != null) {
                targetUUID = target.getUniqueId();
            } else {
                targetUUID = test;
            }
        }

        if (args.length >= 2) {
            page.set(Integer.parseInt(args[1]));
        }

        page.getAndDecrement();

        Player player = (Player) sender;
        DupeIPManager dupeManager = StaffManager.getInstance().getDupeIPManager();
        PunishmentSQLManager sqlManager = StaffManager.getInstance().getPunishmentManager().getSQLManager();

        if (targetUUID != null) {
            if (!targetUUID.equals(test)) {
                dupeManager.getDuplicateIPsForUser(targetUUID, ips -> {
                    sendPage(page, player, sqlManager, ips);
                });
                return true;
            }

            UtilConcurrency.runAsync(() -> {
                UUID uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
                UtilConcurrency.runSync(() -> {
                    dupeManager.getDuplicateIPsForUser(uuid, ips -> {
                        sendPage(page, player, sqlManager, ips);
                    });
                });
            });
            return true;
        }

        dupeManager.getDuplicateIPs(ips -> {
            if (ips.size() == 0) {
                Messaging.send(player, new RegularMessage(PunishmentMessage.PREFIX, PunishmentMessage.STAFF_DUPEIP_NO_DUPLICATES));
                return;
            }

            sendPage(page, player, sqlManager, ips);
        });
        return true;
    }

    private void sendPage(AtomicInteger page, Player player, PunishmentSQLManager sqlManager, Map<String, PlayerConnectionInfo[]> ips) {
        if (ips.isEmpty()) {
            player.sendMessage(Colors.RED + "No duplicate IPs found!");
            return;
        }

        int totalPages = (int) Math.ceil((double) ips.size() / (double) PER_PAGE);
        page.set(UtilMath.clamp(page.get(), 0, totalPages - 1));

        List<String> currentPage = Lists.newArrayList(ips.keySet());
        currentPage = currentPage.subList(page.get() * PER_PAGE, Math.min((page.get() + 1) * PER_PAGE, currentPage.size()));

        player.sendMessage(UtilText.createLineCenteredMessage(ChatColor.DARK_GRAY, new RegularMessage(PunishmentMessage.STAFF_DUPEIP_HEADER).toString()));
        player.sendMessage(UtilText.center(Colors.GREEN + "Online" + Colors.GRAY + " | " + Colors.RED + "Banned" + Colors.GRAY + " | Offline"));
        for (String ip : currentPage) {
            PlayerConnectionInfo[] infoArray = ips.get(ip);
            for (PlayerConnectionInfo connectionInfo : infoArray) {
                UUID uuid = connectionInfo.getUuid();
                String name = connectionInfo.getName();
                String color = Colors.GREEN;

                Player other = Bukkit.getPlayer(uuid);
                if (other == null) {
                    Optional<PlayerPunishmentData> banOptional = sqlManager.getPunishment(uuid, PunishmentType.BAN);
                    if (banOptional.isPresent()) {
                        color = Colors.RED;
                    } else {
                        color = Colors.GRAY;
                    }
                }

                String message = color + name;
                player.sendMessage(message);
            }
        }

        player.sendMessage(UtilText.createLine(ChatColor.DARK_GRAY));
    }


}
