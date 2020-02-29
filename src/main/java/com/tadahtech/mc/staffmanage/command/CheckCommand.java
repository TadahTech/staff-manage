package com.tadahtech.mc.staffmanage.command;

import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.PunishmentMessage;
import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.lang.ColorFormatting;
import com.tadahtech.mc.staffmanage.lang.Messaging;
import com.tadahtech.mc.staffmanage.lang.types.RegularMessage;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import com.tadahtech.mc.staffmanage.util.UtilText;
import com.tadahtech.mc.staffmanage.util.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CheckCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sms.check")) {
            sender.sendMessage(Colors.RED + "You cannot run this command!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Colors.RED + "Only players can enter into the UI.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage(Colors.RED + "Please enter a target and type");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Colors.RED + "That player is not online!");
            return true;
        }

        PunishmentManager punishmentManager = StaffManager.getInstance().getPunishmentManager();

        UtilConcurrency.runAsync(() -> {
            PunishmentType type = PunishmentType.getByName(args[1]);

            if (type == null) {
                sender.sendMessage(Colors.RED + "Invalid type: " + args[1]);
                return;
            }

            Optional<PlayerPunishmentData> optionalData = punishmentManager.getSQLManager().getPunishment(target.getUniqueId(), type);

            if (!optionalData.isPresent()) {
                sender.sendMessage(Colors.RED + "No data found for the type " + type.getUiName());
                return;
            }

            PlayerPunishmentData data = optionalData.get();

            Messaging.send(player,
              new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_TIMESTAMP)
                .withArg("" + Colors.DARK_GRAY + UtilTime.format(data.getTimestamp()))
                .withArg("", ColorFormatting.COUNT),
              new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_TYPE).withArg(UtilText.format(data.getType().name()), ColorFormatting.ELEMENT),
              data.getExpiry() == null ? null : new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_LENGTH).withArg(UtilTime.toSentence(data.getLength()), ColorFormatting.ELEMENT),
              new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_REASON).withArg(data.getCategory(), ColorFormatting.ELEMENT).withArg(data.getSubTypePretty(), ColorFormatting.ELEMENT),
              new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_INITIATOR).withArg(data.getInitiatorName(), ColorFormatting.NAME)
            );
        });
        return true;
    }
}
