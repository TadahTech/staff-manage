package com.tadahtech.mc.staffmanage.command;

import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.gui.category.PunishmentCategoryMenu;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PunishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sms.punish")) {
            sender.sendMessage(Colors.RED + "You cannot run this command!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Colors.RED + "Only players can enter into the UI.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(Colors.RED + "Please enter a target");
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

            UUID targetUuid = uuid;
            String targetName = name;

            UtilConcurrency.runSync(() -> {
                PunishmentManager manager = StaffManager.getInstance().getPunishmentManager();
                PunishmentBuilder builder = manager.getBuilderManager().makeBuilder(player, targetUuid, targetName);

                new PunishmentCategoryMenu(builder).open(player);
            });
        });
        return true;
    }
}
