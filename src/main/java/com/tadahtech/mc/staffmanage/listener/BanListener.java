package com.tadahtech.mc.staffmanage.listener;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.punishments.bans.Ban;
import com.tadahtech.mc.staffmanage.punishments.record.RecordEntry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Optional;
import java.util.UUID;

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

        Optional<Ban> banOptional = this.manager.getSQLManager().getPunishment(uuid, PunishmentType.BAN);
        if (!banOptional.isPresent()) {
            return;
        }

        Ban ban = banOptional.get();
        if (ban.isTemporary() && ban.isExpired()) {
            Bukkit.getServer().getLogger().info("Found expired ban for " + name + " in database. Deleting it.");
            this.manager.getSQLManager().deletePunishment(uuid, PunishmentType.BAN);

            RecordEntry entry = new RecordEntry(uuid, name, PunishmentType.REMOVE, null, "Ban expired", "CONSOLE", StaffManager.CONSOLE_UUID, ban.getExpiry());
            this.manager.getRecordSQLManager().saveEntry(entry);
            return;
        }

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ban.formatMessage(name));
        Bukkit.getServer().getLogger().info("Player " + name + " attempted to connect, but is banned! Disallowing connection.");
    }
}
