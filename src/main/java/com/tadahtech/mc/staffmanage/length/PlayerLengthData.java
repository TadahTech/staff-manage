package com.tadahtech.mc.staffmanage.length;

import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.database.ColumnType;
import com.tadahtech.mc.staffmanage.database.Savable;
import com.tadahtech.mc.staffmanage.database.Saved;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;

import java.util.Map;
import java.util.UUID;

public class PlayerLengthData implements Savable {

    @Saved(primaryKey = true, columnType = ColumnType.UUID)
    private UUID uuid;

    @Saved
    private String name;

    @Saved(columnType = ColumnType.GSON)
    private Map<PunishmentType, Integer> lengthIndexes;

    public PlayerLengthData() {
    }

    public PlayerLengthData(PlayerPunishmentData data) {
        this.uuid = data.getUuid();
        this.name = data.getName();
        this.lengthIndexes = Maps.newHashMap();
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

    public Map<PunishmentType, Integer> getLengthIndexes() {
        return lengthIndexes;
    }

    public int getIndexFor(PunishmentType punishmentType) {
        Integer index = this.lengthIndexes.get(punishmentType);
        return index == null ? 0 : index;
    }

    public void setIndex(PunishmentType punishmentType, int index) {
        this.lengthIndexes.put(punishmentType, index);
    }

    public void setLengthIndexes(Map<PunishmentType, Integer> lengthIndexes) {
        this.lengthIndexes = lengthIndexes;
    }
}
