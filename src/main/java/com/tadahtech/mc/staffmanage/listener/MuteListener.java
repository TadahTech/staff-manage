package com.tadahtech.mc.staffmanage.listener;

import com.tadahtech.mc.staffmanage.punishments.PunishmentManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.punishments.mutes.Mute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteListener implements PunishmentListener {

    private PunishmentManager manager;

    public MuteListener(PunishmentManager manager) {
        this.manager = manager;

        this.startListening();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Mute mute = this.manager.getMute(player);

        if (mute == null) {
            return;
        }

        if (mute.isTemporary() && mute.isExpired()) {
            this.manager.getSQLManager().deletePunishment(player.getUniqueId(), PunishmentType.MUTE);
            this.manager.unmute(player);
            return;
        }

        event.setCancelled(true);
        player.sendMessage(mute.getBaseMessage(player.getName()));
    }
}
