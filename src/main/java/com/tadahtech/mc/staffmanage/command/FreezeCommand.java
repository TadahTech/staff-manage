package com.tadahtech.mc.staffmanage.command;

import com.tadahtech.mc.staffmanage.StaffManager;
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

        UtilFreeze freeze = StaffManager.getInstance().getUtilFreeze();

        if (!player.hasPermission("sms.freeze")) {
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(Colors.RED + "Please enter a target");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Colors.RED + "Invalid player name");
            return true;
        }

        if (freeze.getFrozenPlayers().contains(target.getUniqueId())) {
            freeze.unfreeze(target);
            player.sendMessage(Colors.GREEN + "Unfroze " + target.getName());
        } else {
            player.sendMessage(Colors.GOLD + "Froze " + target.getName());
            player.sendMessage(Colors.GRAY + "Run /freeze " + target.getName() + " to unfreeze the player.");
            freeze.freeze(target, target.getLocation());
        }

        return true;
    }
}
