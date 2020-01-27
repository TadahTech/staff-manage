package com.tadahtech.mc.staffmanage.punishments;

public enum PunishmentType {
    BAN("\u00a74"),
    KICK("\u00a7c"),
    MUTE("\u00a76"),
    WARNING("\u00a79"),
    REMOVE("\u00a7a");

    private String color;

    PunishmentType(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
