package com.tadahtech.mc.staffmanage.player;

import com.tadahtech.mc.staffmanage.database.ColumnType;
import com.tadahtech.mc.staffmanage.database.Savable;
import com.tadahtech.mc.staffmanage.database.Saved;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.UtilTime;

import java.util.Date;
import java.util.UUID;

public class PlayerPunishmentData implements Savable {

    @Saved(primaryKey = true, columnType = ColumnType.UUID)
    private UUID uuid;

    @Saved
    private String name;

    @Saved(primaryKey = true, columnType = ColumnType.ENUM)
    private PunishmentType type;

    @Saved(columnType = ColumnType.DATE)
    private Date expiry;

    @Saved
    private String category;

    @Saved
    private String subType;

    @Saved
    private String initiatorName;

    @Saved(columnType = ColumnType.UUID)
    private UUID initiatorUUID;

    @Saved(columnType = ColumnType.DATE)
    private Date timestamp;

    public PlayerPunishmentData() {
    }

    public boolean isExpired() {
        return UtilTime.hasElapsed(this.timestamp.getTime(), this.expiry.getTime());
    }

    public boolean isTemporary() {
        return this.type == PunishmentType.TEMP_BAN || this.type == PunishmentType.TEMP_MUTE;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public PunishmentType getType() {
        return type;
    }

    public Date getExpiry() {
        return expiry;
    }

    public String getCategory() {
        return category;
    }

    public String getSubType() {
        return subType;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public UUID getInitiatorUUID() {
        return initiatorUUID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTimeRemaining() {
        if (!isTemporary()) {
            return "";
        }

        long time = UtilTime.getTimeLeft(this.timestamp.getTime(), this.expiry.getTime());

        return UtilTime.format(new Date(time));
    }

    public String getTime() {
        return UtilTime.toTimerSecond(this.expiry.getTime());
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(PunishmentType type) {
        this.type = type;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public void setInitiatorUUID(UUID initiatorUUID) {
        this.initiatorUUID = initiatorUUID;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
