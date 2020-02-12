package com.tadahtech.mc.staffmanage.length;

import com.google.common.collect.Maps;
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
import java.util.UUID;

public class LengthManager implements PunishmentListener {

    private LengthSQLManager sqlManager;
    private PunishmentManager punishmentManager;
    private Map<UUID, PlayerLengthData> playerData;

    public LengthManager(PunishmentManager punishmentManager) {
        this.punishmentManager = punishmentManager;
        this.sqlManager = new LengthSQLManager();
        this.playerData = Maps.newHashMap();
        startListening();
    }

    public void addData(PlayerLengthData lengthData) {
        if (lengthData == null) {
            return;
        }

        this.playerData.put(lengthData.getUuid(), lengthData);
    }

    public PlayerLengthData getData(UUID uuid) {
        return this.playerData.get(uuid);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        PlayerLengthData data = this.sqlManager.getLengthData(uuid);

        if (data == null) {
            System.out.println("Data is null");
            return;
        }

        Date updated = data.getLastUpdated();
        Date future = new Date(updated.getTime() + this.punishmentManager.getRecordExpireTime());

        if (UtilTime.hasElapsed(updated.getTime(), future.getTime())) {
            System.out.println("Resetting data");
            data.reset();

            RecordEntry entry = new RecordEntry(data.getUuid(), data.getName(), RecordEntryType.RESET_LENGTH, "", "", "", null, new Date());
            this.punishmentManager.getRecordSQLManager().saveEntry(entry);
        }

        addData(data);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        PlayerLengthData data = getData(uuid);

        if (data == null) {
            return;
        }

        this.sqlManager.save(data);
    }

    public PunishmentLength getLength(PlayerPunishmentData data) {
        PunishmentCategory category = this.punishmentManager.getCategory(data.getCategory());
        PunishmentData punishmentData = category.getDataFor(data.getSubType());
        PlayerLengthData lengthData = getData(data.getUuid());

        int typeIndex = 0;
        int lengthIndex = 0;

        if (lengthData == null) {
            lengthData = new PlayerLengthData(data.getUuid(), data.getName());
            addData(lengthData);

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
        PlayerLengthData lengthData = getData(data.getUuid());

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
                data.setType(type);
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
        int lengthIndex = lengthData.getLengthCounter() - 1;

        PunishmentType type = data.getTypeFor(typeIndex);

        return data.getLengthFor(type, lengthIndex);
    }
}
