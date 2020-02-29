package com.tadahtech.mc.staffmanage.punishments.builder;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.length.PunishmentLength;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class PunishmentBuilder {

    private String initiatorName;
    private UUID initiatorUUID;
    private String playerName;
    private UUID playerUUID;
    private PunishmentLength currentLength;

    private PunishmentCategory category;
    private PunishmentData data;
    private PunishmentType type;

    public PunishmentBuilder(Player initiator, Player player) {
        this.initiatorName = initiator.getName();
        this.initiatorUUID = initiator.getUniqueId();
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
    }

    public PunishmentBuilder(Player initiator, UUID targetUUID, String targetName) {
        this.initiatorName = initiator.getName();
        this.initiatorUUID = initiator.getUniqueId();
        this.playerUUID = targetUUID;
        this.playerName = targetName;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public UUID getInitiatorUUID() {
        return initiatorUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public PunishmentCategory getCategory() {
        return category;
    }

    public void setCategory(PunishmentCategory category) {
        this.category = category;
    }

    public PunishmentData getData() {
        return data;
    }

    public PunishmentBuilder setData(PunishmentData data) {
        this.data = data;
        return this;
    }

    public PunishmentType getType() {
        return type;
    }

    public PunishmentBuilder setType(PunishmentType type) {
        this.type = type;
        return this;
    }

    public void punish() {
        PlayerPunishmentData data = new PlayerPunishmentData();

        data.setInitiatorName(this.initiatorName);
        data.setInitiatorUUID(this.initiatorUUID);

        data.setUuid(this.playerUUID);
        data.setName(this.playerName);

        data.setTimestamp(new Date());

        data.setCategory(this.category.getName());
        data.setSubType(this.data.getName());
        data.setSubTypePretty(ChatColor.stripColor(this.data.getGuiName()));

        StaffManager.getInstance().getPunishmentManager().punish(data);
    }

    public PunishmentLength getCurrentLength() {
        return currentLength;
    }

    public PunishmentBuilder setCurrentLength(PunishmentLength currentLength) {
        this.currentLength = currentLength;
        return this;
    }
}
