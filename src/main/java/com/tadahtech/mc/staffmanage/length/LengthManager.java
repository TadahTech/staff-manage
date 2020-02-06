package com.tadahtech.mc.staffmanage.length;

import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.PunishmentManager;
import com.tadahtech.mc.staffmanage.listener.PunishmentListener;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        this.sqlManager.getLengthData(uuid, playerLengthData -> this.playerData.put(uuid, playerLengthData));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        PlayerLengthData data = this.playerData.remove(uuid);

        this.sqlManager.save(data);
    }

    public PunishmentLength getLength(PlayerPunishmentData data) {
        PlayerLengthData lengthData = this.playerData.get(data.getUuid());

        if (lengthData == null) {
            lengthData = new PlayerLengthData(data);
        }

        int currentIndex = lengthData.getIndexFor(data.getType());
        PunishmentData punishmentData = this.punishmentManager.getCategory(data.getCategory()).getDataFor(data.getSubType());

        this.playerData.put(data.getUuid(), lengthData);

        return punishmentData.getLengthFor(data.getType(), currentIndex);
    }

    public void incrementLength(PlayerPunishmentData data) {
        PlayerLengthData lengthData = this.playerData.get(data.getUuid());

        lengthData.setIndex(data.getType(), lengthData.getIndexFor(data.getType()) + 1);

        this.playerData.put(data.getUuid(), lengthData);
    }

}
