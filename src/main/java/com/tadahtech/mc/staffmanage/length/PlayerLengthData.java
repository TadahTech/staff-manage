package com.tadahtech.mc.staffmanage.length;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.database.ColumnType;
import com.tadahtech.mc.staffmanage.database.Savable;
import com.tadahtech.mc.staffmanage.database.Saved;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;

import java.util.Date;
import java.util.UUID;

public class PlayerLengthData implements Savable {

    @Saved(primaryKey = true, columnType = ColumnType.UUID)
    private UUID uuid;

    @Saved(primaryKey = true, size = 64)
    private String subCategory;

    @Saved
    private String name;

    @Saved(columnType = ColumnType.INTEGER)
    private int typeCounter;

    @Saved(columnType = ColumnType.ENUM)
    private PunishmentType lastType;

    @Saved(columnType = ColumnType.INTEGER)
    private int lengthCounter;

    @Saved(columnType = ColumnType.DATE)
    private Date lastUpdated;

    public PlayerLengthData() {
    }

    public PlayerLengthData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.typeCounter = 0;
        this.lengthCounter = 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void addPunishment(PunishmentType type) {
        StaffManager.getInstance().debug("adding length to " + type + " for " + name);
        this.lengthCounter++;
        this.lastUpdated = new Date();

        if (type == lastType) {
            return;
        }

        this.lengthCounter = 0;
        this.typeCounter++;
        setLastType(type);
    }

    public int getTypeCounter() {
        return typeCounter;
    }

    public void setLastType(PunishmentType lastType) {
        this.lastType = lastType;
    }

    public int getLengthCounter() {
        return lengthCounter;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void reset() {
        this.lastType = null;
        this.typeCounter = 0;
        this.lengthCounter = 0;
    }

    public void setLastUpdated() {
        this.lastUpdated = new Date();
    }

    public void incrementLength() {
        this.lengthCounter++;
    }

    public PunishmentType getLastType() {
        return this.lastType;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
}
