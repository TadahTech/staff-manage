package com.tadahtech.mc.staffmanage.length;

import com.tadahtech.mc.staffmanage.database.ColumnType;
import com.tadahtech.mc.staffmanage.database.Savable;
import com.tadahtech.mc.staffmanage.database.Saved;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;

import java.util.UUID;

public class PlayerLengthData implements Savable {

    @Saved(primaryKey = true, columnType = ColumnType.UUID)
    private UUID uuid;

    @Saved(primaryKey = true, columnType = ColumnType.ENUM)
    private PunishmentType type;
    
    @Saved
    private String name;

    @Saved(columnType = ColumnType.INTEGER)
    private int index;

    public PlayerLengthData() {
    }

    public PlayerLengthData(PlayerPunishmentData data) {
        this.uuid = data.getUuid();
        this.name = data.getName();
        this.type = data.getType();
        this.index = 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void increment() {
        this.index++;
    }

    public PunishmentType getType() {
        return type;
    }

    public void setType(PunishmentType type) {
        this.type = type;
    }
}
