package com.tadahtech.mc.staffmanage.length;

import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.listener.PunishmentListener;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class LengthManager implements PunishmentListener {

    private LengthSQLManager sqlManager;
    private PunishmentManager punishmentManager;
    private Map<UUID, Map<PunishmentType, PlayerLengthData>> playerData;

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

        Map<PunishmentType, PlayerLengthData> lengthDataMap = this.playerData.get(lengthData.getUuid());

        if (lengthDataMap == null) {
            lengthDataMap = Maps.newHashMap();
        }

        lengthDataMap.put(lengthData.getType(), lengthData);

        this.playerData.put(lengthData.getUuid(), lengthDataMap);
    }

    public PlayerLengthData getData(UUID uuid, PunishmentType type) {
        Map<PunishmentType, PlayerLengthData> lengthDataMap = this.playerData.get(uuid);

        if (lengthDataMap == null) {
            return null;
        }

        return lengthDataMap.get(type);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        for (PunishmentType type : PunishmentType.values()) {
            if (!type.isTemporary()) {
                continue;
            }
            this.sqlManager.getLengthData(uuid, type, this::addData);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        for (PunishmentType type : PunishmentType.values()) {
            if (!type.isTemporary()) {
                continue;
            }
            PlayerLengthData data = getData(uuid, type);

            if (data == null) {
                continue;
            }

            this.sqlManager.save(data);
        }

    }

    public PunishmentLength getLength(PlayerPunishmentData data) {
        PlayerLengthData lengthData = getData(data.getUuid(), data.getType());

        if (lengthData == null) {
            lengthData = new PlayerLengthData(data);
        }

        int currentIndex = lengthData.getIndex();

        PunishmentCategory category = this.punishmentManager.getCategory(data.getCategory());
        PunishmentData punishmentData = category.getDataFor(data.getSubType());

        addData(lengthData);

        return punishmentData.getLengthFor(data.getType(), currentIndex);
    }

    public void incrementLength(PlayerPunishmentData data) {
        System.out.println("Getting lengthData");
        PlayerLengthData lengthData = getData(data.getUuid(), data.getType());

        int index = lengthData.getIndex();

        System.out.println("Current index: " + data.getType() + " ::  " + index);
        lengthData.increment();

        System.out.println("New index: " + data.getType() + " ::  " + lengthData.getIndex());
        addData(lengthData);
    }

}
