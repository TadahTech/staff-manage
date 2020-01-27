package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.commands.SubCommand;
import com.tadahtech.mc.staffmanage.commands.sub.ActivePunishmentsCommand;
import com.tadahtech.mc.staffmanage.commands.sub.BanCommand;
import com.tadahtech.mc.staffmanage.commands.sub.KickCommand;
import com.tadahtech.mc.staffmanage.commands.sub.MuteCommand;
import com.tadahtech.mc.staffmanage.commands.sub.RecordCommand;
import com.tadahtech.mc.staffmanage.commands.sub.TempBanCommand;
import com.tadahtech.mc.staffmanage.commands.sub.TempMuteCommand;
import com.tadahtech.mc.staffmanage.commands.sub.UnbanCommand;
import com.tadahtech.mc.staffmanage.commands.sub.UnmuteCommand;
import com.tadahtech.mc.staffmanage.commands.sub.WarnCommand;
import com.tadahtech.mc.staffmanage.listener.BanListener;
import com.tadahtech.mc.staffmanage.listener.MuteListener;
import com.tadahtech.mc.staffmanage.punishments.mutes.Mute;
import com.tadahtech.mc.staffmanage.punishments.persist.PunishmentSQLManager;
import com.tadahtech.mc.staffmanage.punishments.record.RecordEntry;
import com.tadahtech.mc.staffmanage.punishments.record.RecordSQLManager;
import com.tadahtech.mc.staffmanage.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Manages negative sanctions towards players. This module features very little
 * caching since there is no need. For example, bans are enforced on connect,
 * if the player can connect, he/she isn't banned.
 */
public class PunishmentManager {

    private PunishmentSQLManager sqlManager;
    private RecordSQLManager recordSQLManager;
    private FileConfiguration config;
    private Map<UUID, Mute> mutes;

    public void PunishmentManager(FileConfiguration config) {
        this.config = config;
        this.sqlManager = new PunishmentSQLManager();
        this.recordSQLManager = new RecordSQLManager();
        this.mutes = Maps.newHashMap();

        new BanListener(this);
        new MuteListener(this);

        this.registerCommand(new KickCommand());
        this.registerCommand(new BanCommand());
        this.registerCommand(new MuteCommand());
        this.registerCommand(new WarnCommand());
        this.registerCommand(new TempBanCommand());
        this.registerCommand(new TempMuteCommand());
        this.registerCommand(new UnbanCommand());
        this.registerCommand(new UnmuteCommand());
        this.registerCommand(new RecordCommand());
        this.registerCommand(new ActivePunishmentsCommand());
    }

    private void registerCommand(SubCommand command) {
        StaffManager.getInstance().getCommandManager().register(command);
    }

    public PunishmentSQLManager getSQLManager() {
        return sqlManager;
    }

    public RecordSQLManager getRecordSQLManager() {
        return recordSQLManager;
    }

    public boolean isMuted(Player player) {
        return this.mutes.containsKey(player.getUniqueId());
    }

    public void apply(Player player, Punishment punishment) {
        Bukkit.getServer().getLogger().info("New punishment applied to " + player.getName() + " (" + player.getUniqueId() + "); " + punishment.getClass().getSimpleName());
        Bukkit.getServer().getLogger().info(Common.GSON.toJson(punishment));

        RecordEntry entry = new RecordEntry(player.getUniqueId(), punishment);
        this.getRecordSQLManager().saveEntry(entry);

        if (punishment.shouldPersist()) {
            this.getSQLManager().savePunishment(player.getUniqueId(), punishment);
        }

        punishment.onApply(player);

        if (punishment.getType() == PunishmentType.MUTE) {
            this.mutes.put(player.getUniqueId(), (Mute) punishment);
        }
    }

    public void remove(Player by, Player player, Punishment punishment) {
        if (punishment.shouldPersist()) {
            this.getSQLManager().deletePunishment(player.getUniqueId(), punishment.getType());
        }

        punishment.onRemove(player);

        if (punishment.getType() == PunishmentType.MUTE) {
            this.mutes.remove(player.getUniqueId());
        }

        String reason;
        switch (punishment.getType()) {
            case BAN:
                reason = "Unban";
                break;
            case MUTE:
                reason = "Unmute";
                break;
            case WARNING:
                reason = "Remove warning";
                break;
            default:
                return;
        }

        RecordEntry entry = new RecordEntry(player.getUniqueId(), PunishmentType.REMOVE, null, reason, by.getName(), by.getUniqueId(), new Date());

        this.getRecordSQLManager().saveEntry(entry);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public Mute getMute(Player player) {
        return this.mutes.get(player.getUniqueId());
    }

    public void unmute(Player player) {
        this.mutes.remove(player.getUniqueId());
    }
}
