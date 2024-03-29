package com.tadahtech.mc.staffmanage.punishments.builder;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class PunishmentBuilderManager {

    private Map<UUID, PunishmentBuilder> builderMap;

    public PunishmentBuilderManager() {
        this.builderMap = Maps.newHashMap();
    }

    public PunishmentBuilder getBuilder(UUID player) {
        return this.builderMap.get(player);
    }

    public PunishmentBuilder cleanup(UUID player) {
        return this.builderMap.remove(player);
    }

    public boolean isInBuilder(UUID player) {
        return getBuilder(player) != null;
    }

    public PunishmentBuilder makeBuilder(Player player, Player target) {
        PunishmentBuilder builder = new PunishmentBuilder(player, target);
        this.builderMap.put(player.getUniqueId(), builder);
        return builder;
    }

    public PunishmentBuilder makeBuilder(Player player, UUID targetUUID, String targetName) {
        PunishmentBuilder builder = new PunishmentBuilder(player, targetUUID, targetName);
        this.builderMap.put(player.getUniqueId(), builder);
        return builder;
    }
}
