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
        if (isMuted(data.getUuid())) {
            PlayerPunishmentData mute = getMute(data.getUuid());

            if (data.isTemporary() && !mute.isTemporary()) {
                return;
            }

            if (mute.isTemporary() && data.isTemporary()) {
                if (data.getExpiry().getTime() < mute.getExpiry().getTime()) {
                    return;
                }
            }
        }
        this.mutes.put(data.getUuid(), data);
    }

    private PlayerPunishmentData getMute(UUID uuid) {
        return this.mutes.get(uuid);
    }

    private boolean isMuted(UUID uuid) {
        return this.mutes.containsKey(uuid);
    }

    public boolean isMuted(Player player) {
        return isMuted(player.getUniqueId());
    }

    public PlayerPunishmentData getMute(Player player) {
        return this.mutes.get(player.getUniqueId());
    }

    public void unmute(Player player) {
        this.mutes.remove(player.getUniqueId());
    }
}
