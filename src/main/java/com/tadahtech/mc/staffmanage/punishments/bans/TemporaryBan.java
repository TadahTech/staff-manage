package com.tadahtech.mc.staffmanage.punishments.bans;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.UtilTime;
import org.bukkit.ChatColor;

import java.util.Date;
import java.util.UUID;

/**
 * A temporary ban, which expires after a given amount of time.
 */
public class TemporaryBan implements Ban {

    private String initiator;
    private UUID initiatorUUID;
    private Date timestamp;
    private String reason;
    private Date expiry;

    public TemporaryBan(String initiator, UUID initiatorUUID, Date timestamp, String reason, Date expiry) {
        this.initiator = initiator;
        this.initiatorUUID = initiatorUUID;
        this.timestamp = timestamp;
        this.reason = reason;
        this.expiry = expiry;
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
    public PunishmentType getType() {
        return PunishmentType.BAN;
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public Date getExpiry() {
        return this.expiry;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public Date getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getBaseMessage(String player) {
        String base = StaffManager.getInstance().getMessagesSection().getString("messages.tempban.message");
        base = base.replace("%player%", player).replace("%reason%", getReason());
        return base;
    }

    @Override
    public String formatMessage(String player) {
        return ChatColor.translateAlternateColorCodes('&', getBaseMessage(player)
          .replace("%by%", getInitiator())
          .replace("%until%", UtilTime.format(getExpiry())));
    }
}
