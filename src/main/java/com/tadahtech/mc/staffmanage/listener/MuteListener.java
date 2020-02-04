package com.tadahtech.mc.staffmanage.listener;

import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.mute.MuteManager;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteListener implements PunishmentListener {

    private MuteManager muteManager;
    private PunishmentManager punishmentManager;

    public MuteListener(PunishmentManager punishmentManager) {
        this.punishmentManager = punishmentManager;
        this.muteManager = punishmentManager.getMuteManager();

        this.startListening();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        PlayerPunishmentData mute = this.muteManager.getMute(player);

        if (mute == null) {
            return;
        }

        if (mute.isTemporary() && mute.isExpired()) {
            this.punishmentManager.getSQLManager().deletePunishment(player.getUniqueId(), player.getName(), PunishmentType.MUTE);
            this.muteManager.unmute(player);
            return;
        }

        player.sendMessage(this.punishmentManager.getMessage(mute));

        event.setCancelled(true);

    }
}
