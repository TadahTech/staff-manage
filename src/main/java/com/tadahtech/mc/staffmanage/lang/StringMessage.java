package com.tadahtech.mc.staffmanage.lang;

import org.bukkit.entity.Player;

import java.util.Locale;

public final class StringMessage implements Message {

    public static final StringMessage EMPTY = new StringMessage("");

    private static final long serialVersionUID = 1;

    private final String value;

    public StringMessage(String value) {
        this.value = value;
    }

    public static StringMessage wrap(String value) {
        if (value == null || value.isEmpty()) {
            return EMPTY;
        }

        return new StringMessage(value);
    }

    @Override
    public String toString(Player player) {
        return value;
    }

    @Override
    public String toString(Locale locale) {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
