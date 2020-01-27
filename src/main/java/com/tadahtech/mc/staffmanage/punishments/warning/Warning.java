package com.tadahtech.mc.staffmanage.punishments.warning;

import com.google.common.collect.Lists;
import com.tadahtech.mc.staffmanage.punishments.Punishment;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilText;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Warning implements Punishment {

    private String initiator;
    private UUID initiatorUUID;
    private Date timestamp;
    private String reason;

    public Warning(String initiator, UUID initiatorUUID, Date timestamp, String reason) {
        this.initiator = initiator;
        this.initiatorUUID = initiatorUUID;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    @Override
    public PunishmentType getType() {
        return PunishmentType.WARNING;
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
        return reason;
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
        return timestamp;
    }

    @Override
    public boolean shouldPersist() {
        return false;
    }

    @Override
    public void onApply(Player player) {
        List<String> lines = Lists.newArrayList();
        lines.add(UtilText.createLineCenteredMessage(ChatColor.RED, Colors.DARK_RED + Colors.BOLD + "Warning"));
        lines.add("");
        lines.add(UtilText.center(getBaseMessage(player.getName())));
        lines.add("");
        lines.add(UtilText.createLine(ChatColor.RED));

        player.sendMessage(lines.toArray(new String[0]));
    }
}
