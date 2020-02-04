package com.tadahtech.mc.staffmanage.punishments;

import com.tadahtech.mc.staffmanage.database.Callback;
import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.database.SavedFieldValue;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PunishmentSQLManager extends GenericSQLManager<PlayerPunishmentData> {

    public PunishmentSQLManager() {
        super("player_punishments", PlayerPunishmentData.class);
    }

    public void getPunishments(UUID uuid, Callback<List<PlayerPunishmentData>> callback) {
        UtilConcurrency.runAsync(() -> {
            List<PlayerPunishmentData> punishments = this.getPunishments(uuid);
            UtilConcurrency.runSync(() -> callback.call(punishments));
        });
    }

    public List<PlayerPunishmentData> getPunishments(UUID uuid) {
        return new ArrayList<>(this.getAll(new SavedFieldValue<>(this.getField("uuid"), uuid)));
    }

    public <T extends PlayerPunishmentData> void getPunishment(UUID uuid, PunishmentType type, Callback<Optional<PlayerPunishmentData>> callback) {
        UtilConcurrency.runAsync(() -> {
            Optional<PlayerPunishmentData> optional = this.getPunishment(uuid, type);
            UtilConcurrency.runSync(() -> callback.call(optional));
        });
    }

    public Optional<PlayerPunishmentData> getPunishment(UUID uuid, PunishmentType type) {
        PlayerPunishmentData data = this.getByPrimary(uuid, type);
        if (data == null) {
            return Optional.empty();
        }

        return Optional.of(data);
    }

    public void deletePunishment(UUID uuid, String name, PunishmentType type) {
        this.deleteByPrimaryAsync(uuid, name, type);
    }

    public void getPunishmentsByName(String name, Callback<List<PlayerPunishmentData>> callback) {
        UtilConcurrency.runAsync(() -> {
            List<PlayerPunishmentData> punishments = this.getPunishmentsByName(name);
            UtilConcurrency.runSync(() -> callback.call(punishments));
        });
    }

    public List<PlayerPunishmentData> getPunishmentsByName(String name) {
        return new ArrayList<>(this.getAll(new SavedFieldValue<>(this.getField("String"), name)));
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
