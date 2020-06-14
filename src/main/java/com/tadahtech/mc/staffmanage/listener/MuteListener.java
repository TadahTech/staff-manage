package com.tadahtech.mc.staffmanage.listener;

import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.mute.MuteManager;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Optional;
import java.util.UUID;

public class MuteListener implements PunishmentListener {

    private MuteManager muteManager;
    private PunishmentManager punishmentManager;

    public MuteListener(PunishmentManager punishmentManager) {
        this.punishmentManager = punishmentManager;
        this.muteManager = punishmentManager.getMuteManager();

        this.startListening();
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();

        Optional<PlayerPunishmentData> muteOptional = this.punishmentManager.getSQLManager().getPunishment(uuid, PunishmentType.MUTE);

        if (!muteOptional.isPresent()) {
            muteOptional = this.punishmentManager.getSQLManager().getPunishment(uuid, PunishmentType.TEMP_MUTE);
            if (!muteOptional.isPresent()) {
                return;
            }
        }

        PlayerPunishmentData mute = muteOptional.get();

        if (mute.isTemporary() && mute.isExpired()) {
            Bukkit.getServer().getLogger().info("Found expired mute for " + name + " in database. Deleting it.");
            this.punishmentManager.removePunishment(mute);
            return;
        }

        this.muteManager.mute(mute);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        PlayerPunishmentData mute = this.muteManager.getMute(player);

        if (mute == null) {
            return;
        }

        if (mute.isTemporary() && mute.isExpired()) {
            this.punishmentManager.removePunishment(mute);
            this.muteManager.unmute(player);
        }

        String message = this.punishmentManager.getMessage(mute);
        if (message.contains("%next%") && this.punishmentManager.getLengthManager().getNext(mute) != null) {
            message = message.replace("%next%", this.punishmentManager.getLengthManager().getNext(mute));
        }

        player.sendMessage(message);

        event.setCancelled(true);

    }
}
