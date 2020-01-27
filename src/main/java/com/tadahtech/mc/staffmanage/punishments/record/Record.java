package com.tadahtech.mc.staffmanage.punishments.record;

import java.util.List;
import java.util.UUID;

public class Record {

    private UUID accountId;
    private List<RecordEntry> entries;

    public Record(UUID accountId, List<RecordEntry> entries) {
        this.accountId = accountId;
        this.entries = entries;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public List<RecordEntry> getEntries() {
        return entries;
    }
}
