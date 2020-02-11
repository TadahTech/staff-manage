package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.collect.Maps;

import java.util.Map;

public enum PunishmentType {

    BAN("ban", true, "Banned", "Ban"),
    TEMP_BAN("tempban", true, "Temporarily banned", "Temp Ban"),

    IP_BAN("ipban", true, "IP banned", "IP Ban"),
    IP_MUTE("ipmute", true, "IP Muted", "IP Mute"),

    MUTE("mute", true, "Muted", "Mute"),
    TEMP_MUTE("tempmute", false, "Temporarily Muted", "Temp Mute"),

    KICK("kick", false, "Kicked", "Kick"),
    WARNING("warning", false, "Warned", "Warn");

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

    PunishmentType(String label, boolean broadcast, String messageVersion, String uiName) {
        this.label = label == null ? name() : label;
        this.messageVersion = messageVersion;
        this.broadcast = broadcast;
        this.uiName = uiName;
    }

    public static PunishmentType getByName(String s) {
        return TYPE_MAP.get(s.toLowerCase());
    }

    public String getLabel() {
        return label;
    }

    public String getUiName() {
        return this.uiName;
    }

    public boolean shouldBroadcast() {
        return broadcast;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

}
