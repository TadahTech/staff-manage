package com.tadahtech.mc.staffmanage.listener;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class StaffChatListener implements PunishmentListener {

    public static final String STAFF_CHAT_META = "staffChatMode";

    public StaffChatListener() {
        this.startListening();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().equalsIgnoreCase("/staffchat")) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (player.hasMetadata(STAFF_CHAT_META)) {
            player.removeMetadata(STAFF_CHAT_META, StaffManager.getInstance());
            player.sendMessage(Colors.RED + Colors.BOLD + "You are no longer in staff chat. Please be careful what you say");
        } else {
            player.sendMessage(Colors.GREEN + "You are now talking in a staff online channel");
            player.setMetadata(STAFF_CHAT_META, new FixedMetadataValue(StaffManager.getInstance(), true));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().hasMetadata(STAFF_CHAT_META)) {
            return;
        }

        event.setCancelled(true);
        String format = event.getFormat();

        Bukkit.getOnlinePlayers().stream()
          .filter(player -> player.hasMetadata(STAFF_CHAT_META))
          .forEach(player -> player.sendMessage(String.format(format, event.getPlayer().getName(), event.getMessage())));
    }

}
