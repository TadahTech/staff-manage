package com.tadahtech.mc.staffmanage.mute;

import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class MuteManager {

    private Map<UUID, PlayerPunishmentData> mutes;

    public MuteManager() {
        this.mutes = Maps.newHashMap();
    }

    public void mute(PlayerPunishmentData data) {
        this.mutes.put(data.getUuid(), data);
    }

    public boolean isMuted(Player player) {
        return this.mutes.containsKey(player.getUniqueId());
    }

    public PlayerPunishmentData getMute(Player player) {
        return this.mutes.get(player.getUniqueId());
    }

    public void unmute(Player player) {
        this.mutes.remove(player.getUniqueId());
    }
}
