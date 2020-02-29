package com.tadahtech.mc.staffmanage.command;

import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilFreeze;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(Colors.RED + "Please enter a target");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Colors.RED + "Invalid player name");
            return true;
        }

        if (UtilFreeze.getFrozenPlayers().contains(target.getUniqueId())) {
            UtilFreeze.unfreeze(target);
        } else {
            UtilFreeze.freeze(target, target.getLocation());
        }

        return true;
    }
}
