package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.record.RecordEntryType;

import java.util.Map;

public enum PunishmentType {

    BAN("ban", true, "Banned", "Ban", RecordEntryType.BAN),
    TEMP_BAN("tempban", true, "Temporarily banned", "Temp Ban", RecordEntryType.BAN),

    IP_BAN("ipban", true, "IP banned", "IP Ban", RecordEntryType.BAN),
    IP_MUTE("ipmute", true, "IP Muted", "IP Mute", RecordEntryType.MUTE),

    MUTE("mute", true, "Muted", "Mute", RecordEntryType.MUTE),
    TEMP_MUTE("tempmute", false, "Temporarily Muted", "Temp Mute", RecordEntryType.MUTE),

    KICK("kick", false, "Kicked", "Kick", RecordEntryType.KICK),
    WARNING("warning", false, "Warned", "Warn", RecordEntryType.WARNING);

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
    private RecordEntryType record;

    PunishmentType(String label, boolean broadcast, String messageVersion, String uiName, RecordEntryType record) {
        this.label = label == null ? name() : label;
        this.messageVersion = messageVersion;
        this.broadcast = broadcast;
        this.uiName = uiName;
        this.record = record;
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

    public boolean isMute() {
        return this == MUTE || this == TEMP_MUTE || this == IP_MUTE;
    }

    public RecordEntryType getRecord() {
        return this.record;
    }
}
