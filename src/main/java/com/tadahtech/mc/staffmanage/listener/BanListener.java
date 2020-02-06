package com.tadahtech.mc.staffmanage.listener;

import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Optional;
import java.util.UUID;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_BANNED;

public class BanListener implements PunishmentListener {

    private PunishmentManager manager;

    public BanListener(PunishmentManager manager) {
        this.manager = manager;
        this.startListening();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();

        Optional<PlayerPunishmentData> banOptional = this.manager.getSQLManager().getPunishment(uuid, PunishmentType.BAN);
        if (!banOptional.isPresent()) {
            return;
        }

        PlayerPunishmentData ban = banOptional.get();

        if (ban.isTemporary() && ban.isExpired()) {
            Bukkit.getServer().getLogger().info("Found expired ban for " + name + " in database. Deleting it.");
            this.manager.removePunishment(ban);
            return;
        }

        event.disallow(KICK_BANNED, manager.getMessage(ban));

        Bukkit.getServer().getLogger().info("Player " + name + " attempted to connect, but is banned! Disallowing connection.");
    }
}
