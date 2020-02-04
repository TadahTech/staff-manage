package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.collect.Maps;

import java.util.Map;

public enum PunishmentType {

    BAN(null, true, "banned"),
    TEMP_BAN("tempban", true, "temporaily banned"),

    IP_BAN("ipban", true, "IP banned"),
    IP_MUTE("ipmute", true, "IP Muted"),

    MUTE(null, true, "muted"),
    TEMP_MUTE("tempmute", "temporarily muted"),

    KICK(null, false, "kicked"),
    WARNING(null, false, "warned");

    private static final Map<String, PunishmentType> TYPE_MAP = Maps.newHashMap();

    static {
        for (PunishmentType type : values()) {
            TYPE_MAP.put(type.getLabel(), type);
        }
    }

    private String label;
    private String messageVersion;
    private boolean broadcast;

    PunishmentType(String label, boolean broadcast, String messageVersion) {
        this.label = label;
        this.messageVersion = messageVersion;
        this.broadcast = broadcast;
    }

    PunishmentType(String label, String messageVersion) {
        this.label = label;
        this.broadcast = false;
        this.messageVersion = messageVersion;
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
}
