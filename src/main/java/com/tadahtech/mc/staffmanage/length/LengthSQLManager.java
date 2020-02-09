package com.tadahtech.mc.staffmanage.length;

import com.tadahtech.mc.staffmanage.database.Callback;
import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.database.SavedFieldValue;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;

import java.util.UUID;

public class LengthSQLManager extends GenericSQLManager<PlayerLengthData> {

    public LengthSQLManager() {
        super("player_punishment_lengths", PlayerLengthData.class);
    }

    public void getLengthData(UUID uuid, PunishmentType type, Callback<PlayerLengthData> callback) {
        UtilConcurrency.runAsync(() -> {
            PlayerLengthData punishments = this.get(
              new SavedFieldValue<>(this.getField("uuid"), uuid),
              new SavedFieldValue<>(this.getField("type"), type)
            );
            UtilConcurrency.runSync(() -> callback.call(punishments));
        });
    }

}
