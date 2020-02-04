package com.tadahtech.mc.staffmanage.player;

import com.tadahtech.mc.staffmanage.record.RecordEntry;

import java.util.List;
import java.util.UUID;

public class PlayerRecordData {

    private UUID accountId;
    private String accountName;
    private List<RecordEntry> entries;

    public PlayerRecordData(UUID accountId, String accountName, List<RecordEntry> entries) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.entries = entries;
    }

    public PlayerRecordData(UUID accountId, List<RecordEntry> entries) {
        this.accountId = accountId;
        this.entries = entries;
    }

    public PlayerRecordData(String accountName, List<RecordEntry> entries) {
        this.accountName = accountName;
        this.entries = entries;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public List<RecordEntry> getEntries() {
        return entries;
    }

    public String getAccountName() {
        return accountName;
    }


}
