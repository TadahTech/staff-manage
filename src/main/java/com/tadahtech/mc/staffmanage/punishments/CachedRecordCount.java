package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.collect.Maps;

import java.util.Map;

public class CachedRecordCount {

    private Map<PunishmentType, Integer> counts;

    public CachedRecordCount(PunishmentType type, int amount) {
        this.counts = Maps.newHashMap();
        this.counts.put(type, amount);
    }

    public int getAmount(PunishmentType type) {
        return this.counts.getOrDefault(type, 0);
    }

    public void add(PunishmentType type, int amount) {
        int curr = getAmount(type);
        curr += amount;
        this.counts.put(type, curr);
    }

    public Map<PunishmentType, Integer> getAll() {
        return this.counts;
    }
}
