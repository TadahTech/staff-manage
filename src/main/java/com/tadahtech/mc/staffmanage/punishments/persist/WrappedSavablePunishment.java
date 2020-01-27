package com.tadahtech.mc.staffmanage.punishments.persist;

import com.tadahtech.mc.staffmanage.database.ColumnType;
import com.tadahtech.mc.staffmanage.database.Savable;
import com.tadahtech.mc.staffmanage.database.Saved;
import com.tadahtech.mc.staffmanage.punishments.Punishment;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.punishments.bans.PermanentBan;
import com.tadahtech.mc.staffmanage.punishments.bans.TemporaryBan;
import com.tadahtech.mc.staffmanage.punishments.mutes.PermanentMute;
import com.tadahtech.mc.staffmanage.punishments.mutes.TemporaryMute;

import java.util.Date;
import java.util.UUID;

public class WrappedSavablePunishment implements Savable {

    @Saved(primaryKey = true, columnType = ColumnType.UUID)
    private UUID uuid;

    @Saved(primaryKey = true, columnType = ColumnType.ENUM)
    private PunishmentType type;

    @Saved(columnType = ColumnType.DATE)
    private Date expiry;

    @Saved(columnType = ColumnType.LONG_STRING)
    private String reason;

    @Saved(columnType = ColumnType.STRING)
    private String initiator;

    @Saved(columnType = ColumnType.UUID)
    private UUID initiatorUUID;

    @Saved(columnType = ColumnType.DATE)
    private Date timestamp;

    public WrappedSavablePunishment() {
    }

    WrappedSavablePunishment(UUID uuid, Punishment punishment) {
        this.uuid = uuid;
        this.type = punishment.getType();
        this.expiry = punishment.getExpiry();
        this.reason = punishment.getReason();
        this.initiator = punishment.getInitiator();
        this.initiatorUUID = punishment.getInitiatorUUID();
        this.timestamp = punishment.getTimestamp();
    }

    public UUID getUuid() {
        return uuid;
    }

    public PunishmentType getType() {
        return type;
    }

    public boolean isTemporary() {
        return this.getExpiry() != null;
    }

    public Date getExpiry() {
        return expiry;
    }

    public String getReason() {
        return reason;
    }

    public String getInitiator() {
        return initiator;
    }

    public Date getTimestamp() {
        return timestamp;
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
            default:
                // We don't save kicks or warnings since they are instant
                throw new UnsupportedOperationException("Not supported!");
        }
    }

    public UUID getInitiatorUUID() {
        return initiatorUUID;
    }
}
