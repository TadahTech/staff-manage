package com.tadahtech.mc.staffmanage.record;

import com.tadahtech.mc.staffmanage.database.ColumnType;
import com.tadahtech.mc.staffmanage.database.Savable;
import com.tadahtech.mc.staffmanage.database.Saved;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;

import java.util.Date;
import java.util.UUID;

public class RecordEntry implements Savable {

    @Saved(primaryKey = true, columnType = ColumnType.UUID)
    private UUID uuid;

    @Saved
    private String name;

    @Saved(primaryKey = true, columnType = ColumnType.ENUM)
    private RecordEntryType type;

    @Saved(size = 64)
    private String category;

    @Saved(size = 64)
    private String subType;

    @Saved
    private String initiatorName;

    @Saved(columnType = ColumnType.UUID)
    private UUID initiatorUUID;

    @Saved(columnType = ColumnType.DATE)
    private Date timestamp;

    @Saved(columnType = ColumnType.BOOLEAN)
    private boolean removed;

    @Deprecated
    public RecordEntry() {
    }

    public RecordEntry(RecordEntryType type, PlayerPunishmentData data) {
        this(data.getUuid(), data.getName(), type, data.getCategory(), data.getSubType(), data.getInitiatorName(), data.getInitiatorUUID(), new Date());
    }

    public RecordEntry(UUID uuid, String name, RecordEntryType type, String category, String subType, String initiatorName, UUID initiatorUUID, Date timestamp) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.category = category;
        this.subType = subType;
        this.initiatorName = initiatorName;
        this.initiatorUUID = initiatorUUID;
        this.timestamp = timestamp;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public RecordEntryType getType() {
        return type;
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

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public void remove() {
        this.setRemoved(true);
    }
}
