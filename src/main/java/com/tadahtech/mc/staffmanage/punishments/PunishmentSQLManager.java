package com.tadahtech.mc.staffmanage.punishments;

import com.tadahtech.mc.staffmanage.database.Callback;
import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;

import java.util.Optional;
import java.util.UUID;

public class PunishmentSQLManager extends GenericSQLManager<PlayerPunishmentData> {

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

    public void deletePunishment(UUID uuid, PunishmentType type) {
        this.deleteByPrimaryAsync(uuid, type);
    }

    public <T extends PlayerPunishmentData> void getPunishmentByName(String name, PunishmentType type, Callback<Optional<PlayerPunishmentData>> callback) {
        UtilConcurrency.runAsync(() -> {
            Optional<PlayerPunishmentData> optional = this.getPunishmentByName(name, type);
            UtilConcurrency.runSync(() -> callback.call(optional));
        });
    }

    public Optional<PlayerPunishmentData> getPunishmentByName(String name, PunishmentType type) {
        PlayerPunishmentData data = this.getByPrimary(name, type);
        if (data == null) {
            return Optional.empty();
        }

        return Optional.of(data);
    }

}
