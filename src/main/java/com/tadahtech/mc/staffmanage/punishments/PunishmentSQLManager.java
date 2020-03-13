package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.database.Callback;
import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.database.SavedFieldValue;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//Google Cache is considered beta, but its fine to use.
@SuppressWarnings("UnstableApiUsage")
public class PunishmentSQLManager extends GenericSQLManager<PlayerPunishmentData> {

    private static final Cache<PunishmentType, Integer> COUNT_CACHE = CacheBuilder.newBuilder()
      .expireAfterWrite(1, TimeUnit.HOURS)
      .maximumSize(1000)
      .recordStats()
      .build();

    private static final Cache<UUID, CachedRecordCount> PLAYER_COUNT_CACHE = CacheBuilder.newBuilder()
      .expireAfterWrite(1, TimeUnit.HOURS)
      .maximumSize(1000)
      .recordStats()
      .build();

    public PunishmentSQLManager() {
        super("player_punishments", PlayerPunishmentData.class);
    }

    public Optional<PlayerPunishmentData> getPunishment(UUID uuid, PunishmentType type) {
        PlayerPunishmentData data = this.getByPrimary(uuid, type);

        if (data == null) {
            return Optional.empty();
        }

        return Optional.of(data);
    }

    public void getTotalForUser(UUID uuid, Callback<Map<PunishmentType, Integer>> callback) {
        CachedRecordCount cached = PLAYER_COUNT_CACHE.getIfPresent(uuid);

        if (cached != null) {
            callback.call(cached.getAll());
            return;
        }

        Map<PunishmentType, Integer> map = Maps.newHashMap();

        for (PunishmentType type : PunishmentType.values()) {
            int count = this.count(
              new SavedFieldValue<>(this.getField("type"), type),
              new SavedFieldValue<>(this.getField("uuid"), uuid)
            );
            PLAYER_COUNT_CACHE.put(uuid, new CachedRecordCount(type, count));
            map.put(type, count);
        }

        callback.call(map);
    }

    public void getTotalOfTypeForUser(UUID uuid, PunishmentType type, Callback<Integer> callback) {
        CachedRecordCount cached = PLAYER_COUNT_CACHE.getIfPresent(uuid);

        if (cached != null) {
            callback.call(cached.getAmount(type));
            return;
        }

        int count = this.count(
          new SavedFieldValue<>(this.getField("type"), type),
          new SavedFieldValue<>(this.getField("uuid"), uuid)
        );

        PLAYER_COUNT_CACHE.put(uuid, new CachedRecordCount(type, count));
        callback.call(count);
    }

    public void getTotalOfType(PunishmentType type, Callback<Integer> callback) {
        Integer amount = COUNT_CACHE.getIfPresent(type);

        if (amount != null) {
            callback.call(amount);
            return;
        }

        int count = this.count(new SavedFieldValue<>(this.getField("type"), type));
        COUNT_CACHE.put(type, count);
        callback.call(count);
    }

    public void getTotal(Callback<Map<PunishmentType, Integer>> callback) {
        Map<PunishmentType, Integer> map = Maps.newHashMap();
        for (PunishmentType type : PunishmentType.values()) {
            Integer amount = COUNT_CACHE.getIfPresent(type);

            if (amount != null) {
                map.put(type, amount);
                continue;
            }

            int count = this.count(new SavedFieldValue<>(this.getField("type"), type));
            map.put(type, count);
            COUNT_CACHE.put(type, count);
        }
        callback.call(map);
    }

    public void deletePunishment(UUID uuid, PunishmentType type) {
        this.deleteByPrimaryAsync(uuid, type);
    }

}
