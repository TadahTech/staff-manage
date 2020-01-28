package com.tadahtech.mc.staffmanage.punishments.record;

import com.tadahtech.mc.staffmanage.database.Callback;
import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.database.SavedFieldValue;

import java.util.UUID;

public class RecordSQLManager extends GenericSQLManager<RecordEntry> {

    public RecordSQLManager() {
        super("player_records", RecordEntry.class);
    }

    public void remove(RecordEntry entry) {
        entry.remove();
        this.saveEntry(entry);
    }

    public void saveEntry(RecordEntry entry) {
        this.save(entry);
    }

    public void getRecord(String accountName, boolean includeRemoved, Callback<Record> recordCallback) {
        if (!includeRemoved) {
            this.getAllAsync(list -> {
                Record record = new Record(accountName, list);
                recordCallback.call(record);
            }, this.createValue("accountName", accountName), this.createValue("removed", false));
            return;
        }

        this.getAllAsync(list -> {
            Record record = new Record(accountName, list);
            recordCallback.call(record);
        }, new SavedFieldValue<>(this.getField("accountName"), accountName));
    }

    public void getRecord(UUID accountId, boolean includeRemoved, Callback<Record> recordCallback) {
        if (!includeRemoved) {
            this.getAllAsync(list -> {
                Record record = new Record(accountId, list);
                recordCallback.call(record);
            }, this.createValue("accountId", accountId), this.createValue("removed", false));
            return;
        }

        this.getAllAsync(list -> {
            Record record = new Record(accountId, list);
            recordCallback.call(record);
        }, new SavedFieldValue<>(this.getField("accountId"), accountId));
    }
}
