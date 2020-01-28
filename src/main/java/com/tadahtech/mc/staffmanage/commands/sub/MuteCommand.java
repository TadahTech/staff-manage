package com.tadahtech.mc.staffmanage.commands.sub;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.commands.SubCommand;
import com.tadahtech.mc.staffmanage.punishments.Punishment;
import com.tadahtech.mc.staffmanage.punishments.mutes.PermanentMute;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilText;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class MuteCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Colors.RED + "Too few arguments!");
            return;
        }

        String playerRaw = args[0];
        String reason = UtilText.joinArgs(1, args);

        Player player = Bukkit.getPlayer(playerRaw);

        if (player == null) {
            sender.sendMessage(Colors.RED + "That player is not online!");
            return;
        }

        UUID uuid = sender instanceof Player ? ((Player) sender).getUniqueId() : StaffManager.CONSOLE_UUID;

        Punishment punishment = new PermanentMute(sender.getName(), uuid, new Date(), reason);

        StaffManager.getInstance().getPunishmentManager().apply(player, punishment);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }
}