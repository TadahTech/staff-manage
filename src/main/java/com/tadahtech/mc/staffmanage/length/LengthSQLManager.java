package com.tadahtech.mc.staffmanage.length;

import com.tadahtech.mc.staffmanage.database.Callback;
import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.database.SavedFieldValue;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;

import java.util.UUID;

public class LengthSQLManager extends GenericSQLManager<PlayerLengthData> {

    public LengthSQLManager() {
        super("player_punishment_lengths", PlayerLengthData.class);
    }

    public void getLengthData(UUID uuid, Callback<PlayerLengthData> callback) {
        UtilConcurrency.runAsync(() -> {
            PlayerLengthData lengthData = this.get(
              new SavedFieldValue<>(this.getField("uuid"), uuid)
            );
            UtilConcurrency.runSync(() -> callback.call(lengthData));
        });
    }

}
