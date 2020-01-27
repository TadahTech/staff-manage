package com.tadahtech.mc.staffmanage.punishments.builder;

import com.tadahtech.mc.staffmanage.punishments.Punishment;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.punishments.bans.PermanentBan;
import com.tadahtech.mc.staffmanage.punishments.bans.TemporaryBan;
import com.tadahtech.mc.staffmanage.punishments.kicks.Kick;
import com.tadahtech.mc.staffmanage.punishments.mutes.PermanentMute;
import com.tadahtech.mc.staffmanage.punishments.mutes.TemporaryMute;
import com.tadahtech.mc.staffmanage.punishments.warning.Warning;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PunishmentBuilder {

    private UUID uuid;
    private PunishmentType type;
    private Date expiry;
    private String reason;
    private String initiator;
    private UUID initiatorUUID;
    private Date timestamp;

    public PunishmentBuilder(UUID uuid) {
        this.uuid = uuid;
        this.timestamp = new Date();
    }

    public UUID getAccountId() {
        return this.uuid;
    }

    public PunishmentType getType() {
        return type;
    }

    public PunishmentBuilder setType(PunishmentType type) {
        this.type = type;
        return this;
    }

    public Date getExpiry() {
        return expiry;
    }

    public PunishmentBuilder setExpiry(Date expiry) {
        this.expiry = expiry;
        return this;
    }

    public PunishmentBuilder setLength(long amount, TimeUnit unit) {
        return this.setLength(unit.toMillis(amount));
    }

    public PunishmentBuilder setLength(long millis) {
        this.expiry = new Date(System.currentTimeMillis() + millis);
        return this;
    }

    public String getReason() {
        return reason;
    }

    public boolean isTemporary() {
        return this.getExpiry() != null;
    }

    public PunishmentBuilder setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getInitiator() {
        return initiator;
    }

    public PunishmentBuilder setInitiator(String initiator) {
        this.initiator = initiator;
        return this;
    }

    public UUID getInitiatorUUID() {
        return initiatorUUID;
    }

    public PunishmentBuilder setInitiatorUUID(UUID initiatorUUID) {
        this.initiatorUUID = initiatorUUID;
        return this;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public PunishmentBuilder setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Punishment toPunishment() {
        switch (this.getType()) {
            case BAN:
                if (this.isTemporary()) {
                    return new TemporaryBan(this.getInitiator(), this.getInitiatorUUID(), this.getTimestamp(), this.getReason(), this.getExpiry());
                }

                return new PermanentBan(this.getInitiator(), this.getInitiatorUUID(), this.getTimestamp(), this.getReason());
            case MUTE:
                if (this.isTemporary()) {
                    return new TemporaryMute(this.getInitiator(), this.getInitiatorUUID(), this.getTimestamp(), this.getReason(), this.getExpiry());
                }

                return new PermanentMute(this.getInitiator(), this.getInitiatorUUID(), this.getTimestamp(), this.getReason());
            case KICK:
                return new Kick(this.getInitiator(), this.getInitiatorUUID(), this.getTimestamp(), this.getReason());
            case WARNING:
                return new Warning(this.getInitiator(), this.getInitiatorUUID(), this.getTimestamp(), this.getReason());
            default:
                throw new UnsupportedOperationException("Not supported!");
        }
    }
}
