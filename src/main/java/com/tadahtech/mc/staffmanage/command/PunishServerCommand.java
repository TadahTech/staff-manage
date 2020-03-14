package com.tadahtech.mc.staffmanage.command;

import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PunishServerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission("sms.serverpunish")) {
            sender.sendMessage(Colors.RED + "You cannot do this!");
            return true;
        }

        if (args.length != 4) {
            sender.sendMessage(Colors.RED + "Incorrect usage: /serverpunish <player> <type> <category> <subType>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        UUID test = UUID.randomUUID();
        UUID targetUUID;
        String testName;

        if (target == null) {
            targetUUID = test;
            testName = null;
        } else {
            targetUUID = target.getUniqueId();
            testName = target.getName();
        }

        UUID finalTargetUUID = targetUUID;
        String finalName = testName;

        UtilConcurrency.runAsync(() -> {
            UUID uuid = finalTargetUUID;
            String name = finalName;

            if (uuid == test) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                uuid = offlinePlayer.getUniqueId();
                name = offlinePlayer.getName();
            }

            PunishmentType type = PunishmentType.getByName(args[1]);
            UUID initiatorUUID = StaffManager.CONSOLE_UUID;
            String initiatorName = "Console";

            PunishmentManager punishmentManager = StaffManager.getInstance().getPunishmentManager();
            PunishmentCategory category = punishmentManager.getCategory(args[2]);

            PlayerPunishmentData playerPunishmentData = new PunishmentBuilder(initiatorName, initiatorUUID, name, uuid)
              .setCategory(category)
              .setData(category.getDataFor(args[3]))
              .setType(type)
              .build();

            punishmentManager.punishConsole(playerPunishmentData);

            sender.sendMessage(Colors.RED + "Punished " + name + " for " + category.getName() + " - " + playerPunishmentData.getSubTypePretty());
        });

        return true;
    }
}
