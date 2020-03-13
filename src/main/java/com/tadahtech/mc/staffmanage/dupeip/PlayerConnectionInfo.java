package com.tadahtech.mc.staffmanage.dupeip;

import com.tadahtech.mc.staffmanage.database.ColumnType;
import com.tadahtech.mc.staffmanage.database.Savable;
import com.tadahtech.mc.staffmanage.database.Saved;

import java.util.UUID;

public class PlayerConnectionInfo implements Savable {

    @Saved(primaryKey = true, columnType = ColumnType.UUID)
    private UUID uuid;

    @Saved
    private String name;

    @Saved(size = 32)
    private String ip;

    public PlayerConnectionInfo() {
    }

    public PlayerConnectionInfo(UUID uuid, String name, String ip) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
