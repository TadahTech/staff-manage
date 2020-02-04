package com.tadahtech.mc.staffmanage.command;

import com.tadahtech.mc.staffmanage.gui.category.PunishmentCategoryMenu;
import com.tadahtech.mc.staffmanage.util.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        new PunishmentCategoryMenu().open(player);
        return true;
    }
}
