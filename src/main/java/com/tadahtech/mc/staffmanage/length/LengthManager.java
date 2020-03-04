package com.tadahtech.mc.staffmanage.length;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.listener.PunishmentListener;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.record.RecordEntry;
import com.tadahtech.mc.staffmanage.record.RecordEntryType;
import com.tadahtech.mc.staffmanage.util.UtilTime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LengthManager implements PunishmentListener {

    private LengthSQLManager sqlManager;
    private PunishmentManager punishmentManager;
    private Map<UUID, Map<String, PlayerLengthData>> playerData;

    public LengthManager(PunishmentManager punishmentManager) {
        this.punishmentManager = punishmentManager;
        this.sqlManager = new LengthSQLManager();
        this.playerData = Maps.newHashMap();
        startListening();
    }

    public void addData(PlayerLengthData lengthData, PunishmentData data) {
        if (lengthData == null) {
            return;
        }

        lengthData.setSubCategory(data.getName());
        Map<String, PlayerLengthData> dataMap = Maps.newHashMap();
        dataMap.put(data.getName(), lengthData);

        this.playerData.put(lengthData.getUuid(), dataMap);
    }

    public Set<PlayerLengthData> getAll(UUID uuid) {
        if (this.playerData.get(uuid) == null) {
            return null;
        }

        return Sets.newHashSet(this.playerData.get(uuid).values());
    }

    public PlayerLengthData getData(UUID uuid, String name) {
        if (this.playerData.get(uuid) == null) {
            return null;
        }

        return this.playerData.get(uuid).get(name);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        this.punishmentManager.getAll().forEach(category -> {
            category.getPunishments().values().forEach(punishmentData -> {
                PlayerLengthData data = this.sqlManager.getLengthData(uuid, punishmentData);

                if (data == null) {
                    return;
                }

                Date updated = data.getLastUpdated();
                Date future = new Date(updated.getTime() + this.punishmentManager.getRecordExpireTime());

                if (UtilTime.hasElapsed(updated.getTime(), future.getTime())) {
                    data.reset();

                    RecordEntry entry = new RecordEntry(data.getUuid(), data.getName(), RecordEntryType.RESET_LENGTH, "", "", "", null, new Date(), null);
                    this.punishmentManager.getRecordSQLManager().saveEntry(entry);
                }

                addData(data, punishmentData);
            });
        });

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        Set<PlayerLengthData> data = getAll(uuid);

        if (data == null || data.isEmpty()) {
            return;
        }

        data.forEach(lengthData -> this.sqlManager.save(lengthData));
    }

    public PunishmentLength getLength(PlayerPunishmentData data) {
        PunishmentCategory category = this.punishmentManager.getCategory(data.getCategory());
        PunishmentData punishmentData = category.getDataFor(data.getSubType());
        PlayerLengthData lengthData = getData(data.getUuid(), punishmentData.getName());

        int typeIndex = 0;
        int lengthIndex = 0;

        if (lengthData == null) {
            lengthData = new PlayerLengthData(data.getUuid(), data.getName());
            addData(lengthData, punishmentData);

            PunishmentType type = punishmentData.getTypeFor(typeIndex);
            lengthData.setLastType(type);
            data.setType(type);
            lengthData.setLastUpdated();

            return punishmentData.getLengthFor(type, lengthIndex);
        } else {
            typeIndex = lengthData.getTypeCounter();
            lengthIndex = lengthData.getLengthCounter();
        }

        PunishmentType type = punishmentData.getTypeFor(typeIndex);
        PunishmentLength length = punishmentData.getLengthFor(type, lengthIndex);

        if (length == null) {
            PunishmentType next = punishmentData.getNext(type);

            if (next == null) {
                data.setType(type);
                return null;
            }

            type = next;

            lengthData.addPunishment(next);
            length = punishmentData.getLengthFor(next, 0);

            if (length != null) {
                lengthData.incrementLength();
            }

        } else {
            lengthData.addPunishment(type);
        }

        data.setType(type);
        return length;
    }

    public String getNext(PlayerPunishmentData data) {
        PunishmentCategory category = this.punishmentManager.getCategory(data.getCategory());
        PunishmentData punishmentData = category.getDataFor(data.getSubType());
        PlayerLengthData lengthData = getData(data.getUuid(), punishmentData.getName());

        if (lengthData == null) {
            return null;
        }

        int typeIndex = lengthData.getTypeCounter();
        int lengthIndex = lengthData.getLengthCounter();

        PunishmentType type = punishmentData.getTypeFor(typeIndex);
        PunishmentLength length = punishmentData.getLengthFor(type, lengthIndex);

        if (length == null) {
            PunishmentType next = punishmentData.getNext(type);

            if (next == null) {
                return null;
            }

            length = punishmentData.getLengthFor(next, 0);

            if (length == null) {
                return next.getMessageVersion();
            }

            return length.toSentence();
        } else {
            return length.toSentence();
        }
    }

    public PunishmentLength getLength(PunishmentData data, PlayerLengthData lengthData) {
        int typeIndex = lengthData.getTypeCounter();
        int lengthIndex = (lengthData.getLengthCounter() <= 0) ? 0 : (lengthData.getLengthCounter() - 1);

        PunishmentType type = data.getTypeFor(typeIndex);

        return data.getLengthFor(type, lengthIndex);
    }
}
