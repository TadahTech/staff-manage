package com.tadahtech.mc.staffmanage.record;

import com.tadahtech.mc.staffmanage.database.Callback;
import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.database.SavedFieldValue;
import com.tadahtech.mc.staffmanage.player.PlayerRecordData;

import java.util.UUID;

public class RecordSQLManager extends GenericSQLManager<RecordEntry> {

    public RecordSQLManager() {
        super("player_punishment_records", RecordEntry.class);
    }


    public void remove(RecordEntry entry) {
        entry.remove();
        this.saveEntry(entry);
    }

    public void saveEntry(RecordEntry entry) {
        this.save(entry);
    }

    public void getRecord(String accountName, boolean includeRemoved, Callback<PlayerRecordData> recordCallback) {
        if (!includeRemoved) {
            this.getAllAsync(list -> {
                PlayerRecordData record = new PlayerRecordData(accountName, list);
                recordCallback.call(record);
            }, this.createValue("accountName", accountName), this.createValue("removed", false));
            return;
        }

        this.getAllAsync(list -> {
            PlayerRecordData record = new PlayerRecordData(accountName, list);
            recordCallback.call(record);
        }, new SavedFieldValue<>(this.getField("accountName"), accountName));
    }

    public void getRecord(UUID accountId, boolean includeRemoved, Callback<PlayerRecordData> recordCallback) {
        if (!includeRemoved) {
            this.getAllAsync(list -> {
                PlayerRecordData record = new PlayerRecordData(accountId, list);
                recordCallback.call(record);
            }, this.createValue("accountId", accountId), this.createValue("removed", false));
            return;
        }

        this.getAllAsync(list -> {
            PlayerRecordData record = new PlayerRecordData(accountId, list);
            recordCallback.call(record);
        }, new SavedFieldValue<>(this.getField("accountId"), accountId));
    }
}
