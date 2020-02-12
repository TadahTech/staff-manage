package com.tadahtech.mc.staffmanage.length;

import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.database.SavedFieldValue;

import java.util.UUID;

public class LengthSQLManager extends GenericSQLManager<PlayerLengthData> {

    public LengthSQLManager() {
        super("player_punishment_lengths", PlayerLengthData.class);
    }

    public PlayerLengthData getLengthData(UUID uuid) {
        return this.get(new SavedFieldValue<>(this.getField("uuid"), uuid));

    }

}
