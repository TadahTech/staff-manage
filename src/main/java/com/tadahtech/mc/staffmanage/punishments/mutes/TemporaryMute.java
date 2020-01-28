package com.tadahtech.mc.staffmanage.punishments.mutes;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;

import java.util.Date;
import java.util.UUID;

public class TemporaryMute implements Mute {

    private String initiator;
    private UUID initiatorUUID;
    private Date timestamp;
    private String reason;
    private Date expiry;

    public TemporaryMute(String initiator, UUID initiatorUUID, Date timestamp, String reason, Date expiry) {
        this.initiator = initiator;
        this.initiatorUUID = initiatorUUID;
        this.timestamp = timestamp;
        this.reason = reason;
        this.expiry = expiry;
    }

    @Override
    public PunishmentType getType() {
        return PunishmentType.MUTE;
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
    public String getBaseMessage(String player) {
        String base = StaffManager.getInstance().getMessagesSection().getString("messages.tempmute.message");
        base = base.replace("%player%", player).replace("%reason%", getReason());
        return base;
    }

}
