package com.tadahtech.mc.staffmanage.command;

import com.tadahtech.mc.staffmanage.util.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {

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

        sender.sendMessage(Colors.DARK_GRAY + "-=-=-=- [" + Colors.GOLD + "Staff Manager Commands" + Colors.DARK_GRAY + "] -=-=-=-");
        sendHelp(sender, "Punish", "/punish <player>", "Enter the Punishment GUI for a player (offline or online)");
        sendHelp(sender, "History", "/phistory <player>", "Check a players punishment history (offline or online)");
        sendHelp(sender, "Check", "/pcheck <player> <type>", "Check the details of an active punishment");
        sendHelp(sender, "Pardon", "/pardon <player> <type>", "Removes a punishment form the player (offline or online)");
        sendHelp(sender, "Freeze", "/freeze <player>", "Freeze / Unfreeze a player");
        sendHelp(sender, "staffchat", "/staffchat", "Enter / Exit StaffChat mode");

        return true;
    }

    private void sendHelp(CommandSender sender, String command, String usage, String desc) {
        sender.sendMessage(Colors.DARK_GRAY + " Command: " + Colors.WHITE + command);
        sender.sendMessage(Colors.DARK_GRAY + " Usage: " + Colors.WHITE + usage);
        sender.sendMessage(Colors.DARK_GRAY + " Description: " + Colors.WHITE + desc);
    }
}
