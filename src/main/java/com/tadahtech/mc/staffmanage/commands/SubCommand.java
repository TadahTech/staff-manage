package com.tadahtech.mc.staffmanage.commands;

import com.tadahtech.mc.staffmanage.util.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface SubCommand {

    String getName();

    String getDescription();

    String getUsage();

    String getPermission();

    default boolean isPlayerOnly() {
        return false;
    }

    void execute(CommandSender sender, String[] args);

    default boolean canUse(Player player) {
        return getPermission() == null || getPermission().isEmpty() || player.hasPermission(getPermission());
    }

    default void run(CommandSender sender, List<String> args) {
        if (!isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(Colors.RED + "This command is for players only!");
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            execute(sender, args.toArray(new String[0]));
            return;
        }

        Player player = (Player) sender;

        if (!canUse(player)) {
            player.sendMessage(Colors.RED + "You don't have permission to use this command.");
            return;
        }

        execute(player, args.toArray(new String[0]));
    }

    default void printHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(Colors.GRAY + "[Command Help] " + Colors.BLUE + getName() + Colors.GRAY + ":");
        sender.sendMessage(Colors.GRAY + "  Description: " + Colors.BLUE + getDescription());
        sender.sendMessage(Colors.GRAY + "  Usage: " + Colors.BLUE + getUsage());
        sender.sendMessage(" ");
    }


}
