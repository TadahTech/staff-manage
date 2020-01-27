package com.tadahtech.mc.staffmanage.punishments.persist;

import com.tadahtech.mc.staffmanage.database.Callback;
import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.database.SavedFieldValue;
import com.tadahtech.mc.staffmanage.punishments.Punishment;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PunishmentSQLManager extends GenericSQLManager<WrappedSavablePunishment> {

    public PunishmentSQLManager() {
        super("player_punishments", WrappedSavablePunishment.class);
    }

    public void getPunishments(UUID accountId, Callback<List<Punishment>> callback) {
        UtilConcurrency.runAsync(() -> {
            List<Punishment> punishments = this.getPunishments(accountId);
            UtilConcurrency.runSync(() -> callback.call(punishments));
        });
    }

    public List<Punishment> getPunishments(UUID accountId) {
        return this.getAll(new SavedFieldValue<>(this.getField("uuid"), accountId)).stream()
          .map(WrappedSavablePunishment::toPunishment)
          .collect(Collectors.toList());
    }

    public <T extends Punishment> void getPunishment(UUID accountId, PunishmentType type, Callback<Optional<T>> callback) {
        UtilConcurrency.runAsync(() -> {
            Optional<T> optional = this.getPunishment(accountId, type);
            UtilConcurrency.runSync(() -> callback.call(optional));
        });
    }

    public <T extends Punishment> Optional<T> getPunishment(UUID accountId, PunishmentType type) {
        WrappedSavablePunishment wrappedPunishment = this.getByPrimary(accountId, type);
        if (wrappedPunishment == null) {
            return Optional.empty();
        }

        return Optional.of((T) wrappedPunishment.toPunishment());
    }

    public void savePunishment(UUID accountId, Punishment punishment) {
        this.save(new WrappedSavablePunishment(accountId, punishment));
    }

    public void deletePunishment(UUID accountId, PunishmentType type) {
        this.deleteByPrimaryAsync(accountId, type);
    }
}
