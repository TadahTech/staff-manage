package com.tadahtech.mc.staffmanage.lang;

import org.bukkit.entity.Player;

import java.util.Locale;


public class Argument implements Message {

    private static final long serialVersionUID = 1;

    private Object value;
    private ColorFormatting formatting = ColorFormatting.NONE;

    public Argument() {
    }

    public Argument(Object value) {
        this.value = value;
    }

    public Argument(Object value, ColorFormatting colorFormatting) {
        this.value = value;
        if (colorFormatting != null) {
            formatting = colorFormatting;
        }
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ColorFormatting getFormatting() {
        return formatting;
    }

    public void setFormatting(ColorFormatting colorFormatting) {
        formatting = colorFormatting;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String toString(Player player) {
        if (value instanceof Message) {
            return ((Message) value).toString(player);
        }
        return toString();
    }

    @Override
    public String toString(Locale locale) {
        if (value instanceof Message) {
            return ((Message) value).toString(locale);
        }
        return toString();
    }

}
