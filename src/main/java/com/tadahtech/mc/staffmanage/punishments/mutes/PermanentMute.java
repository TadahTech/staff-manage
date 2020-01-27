package com.tadahtech.mc.staffmanage.punishments.mutes;

import com.tadahtech.mc.staffmanage.punishments.PunishmentType;

import java.util.Date;
import java.util.UUID;

public class PermanentMute implements Mute {

    private String initiator;
    private UUID initiatorUUID;
    private Date timestamp;
    private String reason;

    public PermanentMute(String initiator, UUID initiatorUUID, Date timestamp, String reason) {
        this.initiator = initiator;
        this.initiatorUUID = initiatorUUID;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    @Override
    public PunishmentType getType() {
        return PunishmentType.MUTE;
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

}
