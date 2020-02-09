package com.tadahtech.mc.staffmanage.command;

import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class PardonCommand implements CommandExecutor {

    private PunishmentManager punishmentManager;

    public PardonCommand() {
        this.punishmentManager = StaffManager.getInstance().getPunishmentManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sms.pardon")) {
            sender.sendMessage(Colors.RED + "You cannot run this command!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Colors.RED + "Please enter a target and type");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        UUID uuid;

        if (target == null) {
            OfflinePlayer offlinePlayer;

            String arg = args[0];

            try {
                offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(arg));
            } catch (Exception e) {
                offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            }

            if (offlinePlayer == null) {
                sender.sendMessage(Colors.RED + "Invalid Player!");
                return true;
            }

            uuid = offlinePlayer.getUniqueId();
        } else {
            uuid = target.getUniqueId();
        }

        UtilConcurrency.runAsync(() -> {
            PunishmentType type = PunishmentType.getByName(args[1]);

            if (type == null) {
                sender.sendMessage(Colors.RED + "Invalid type: " + args[1]);
                return;
            }

            Optional<PlayerPunishmentData> data = this.punishmentManager.getSQLManager().getPunishment(uuid, type);

            if (!data.isPresent()) {
                sender.sendMessage(Colors.RED + "No punishment by that type found for that player");
                return;
            }

            this.punishmentManager.removePunishment(data.get());

            sender.sendMessage(Colors.GOLD + "Removed " + Colors.WHITE + args[1] + Colors.GOLD + " from " + Colors.WHITE + args[0]);
        });
        return true;
    }
}
