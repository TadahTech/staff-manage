package com.tadahtech.mc.staffmanage.punishments.bans;

import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import org.bukkit.ChatColor;

import java.util.Date;
import java.util.UUID;

/**
 * A permanent ban, which never expires.
 */
public class PermanentBan implements Ban {

    private String initiator;
    private UUID initiatorUUID;
    private Date timestamp;
    private String reason;

    public PermanentBan(String initiator, UUID initiatorUUID, Date timestamp, String reason) {
        this.initiator = initiator;
        this.initiatorUUID = initiatorUUID;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    @Override
    public PunishmentType getType() {
        return PunishmentType.BAN;
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public Date getExpiry() {
        return null;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public String getInitiator() {
        return initiator;
    }

    @Override
    public UUID getInitiatorUUID() {
        return initiatorUUID;
    }

    @Override
    public Date getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String formatMessage(String player) {
        return ChatColor.translateAlternateColorCodes('&', getBaseMessage(player).replace("%by%", getInitiator()));
    }
}
