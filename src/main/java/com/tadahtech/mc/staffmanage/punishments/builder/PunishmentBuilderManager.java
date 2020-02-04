package com.tadahtech.mc.staffmanage.punishments.builder;

import com.google.common.collect.Maps;

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

}
