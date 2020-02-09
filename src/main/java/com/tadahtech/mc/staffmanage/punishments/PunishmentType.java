package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public enum PunishmentType {

    BAN("ban", true, "Banned", "Ban", Material.BARRIER),
    TEMP_BAN("tempban", true, "Temporarily banned", "Temp Ban", Material.REDSTONE),

    IP_BAN("ipban", true, "IP banned", "IP Ban", Material.ANVIL),
    IP_MUTE("ipmute", true, "IP Muted", "IP Mute", Material.ENCHANTED_BOOK),

    MUTE("mute", true, "Muted", "Mute", Material.BOOK),
    TEMP_MUTE("tempmute", false, "Temporarily Muted", "Temp Mute", Material.GHAST_TEAR),

    KICK("kick", false, "Kicked", "Kick", Material.GOLD_BOOTS),
    WARNING("warn", false, "Warned", "Warn", Material.PAPER);

    private static final Map<String, PunishmentType> TYPE_MAP = Maps.newHashMap();

    static {
        for (PunishmentType type : values()) {
            TYPE_MAP.put(type.getLabel(), type);
        }
    }

    private String label;
    private String messageVersion;
    private String uiName;
    private boolean broadcast;
    private Material material;

    PunishmentType(String label, boolean broadcast, String messageVersion, String uiName, Material material) {
        this.label = label == null ? name() : label;
        this.messageVersion = messageVersion;
        this.broadcast = broadcast;
        this.uiName = uiName;
        this.material = material;
    }

    public static PunishmentType getByName(String s) {
        return TYPE_MAP.get(s.toLowerCase());
    }

    public String getLabel() {
        return label;
    }

    public boolean shouldBroadcast() {
        return broadcast;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public ItemStack toItemStack() {
        return new ItemBuilder(this.material).setTitle(Colors.GOLD + this.uiName).build();
    }

    public boolean isTemporary() {
        return this == TEMP_BAN || this == TEMP_MUTE;
    }
}
