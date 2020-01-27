package com.tadahtech.mc.staffmanage.punishments.kicks;

import com.tadahtech.mc.staffmanage.punishments.Punishment;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class Kick implements Punishment {

    private String initiator;
    private UUID initiatorUUID;
    private Date timestamp;
    private String reason;

    public Kick(String initiator, UUID initiatorUUID, Date timestamp, String reason) {
        this.initiator = initiator;
        this.initiatorUUID = initiatorUUID;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    @Override
    public PunishmentType getType() {
        return PunishmentType.KICK;
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public Date getExpiry() {
        return null;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public String getInitiator() {
        return initiator;
    }

    @Override
    public UUID getInitiatorUUID() {
        return initiatorUUID;
    }

    @Override
    public Date getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean shouldPersist() {
        return false;
    }

    @Override
    public void onApply(Player player) {
        player.kickPlayer(this.getBaseMessage(player.getName()));
    }
}
