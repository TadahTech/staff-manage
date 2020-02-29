package com.tadahtech.mc.staffmanage.lang;

import com.tadahtech.mc.staffmanage.util.Colors;

import java.io.Serializable;

public class ColorFormatting implements Serializable {

    public static final ColorFormatting NONE = new ColorFormatting("");
    public static final ColorFormatting NAME = new ColorFormatting(Colors.GOLD);
    public static final ColorFormatting ELEMENT = new ColorFormatting(Colors.GOLD);
    public static final ColorFormatting PLAYER_NAME = new ColorFormatting(Colors.PLAYER_NAME);
    public static final ColorFormatting COUNT = new ColorFormatting(Colors.COUNT);
    public static final ColorFormatting TIME = new ColorFormatting(Colors.TIME);

    private static final long serialVersionUID = 1L;
    private String formatting = "";

    public ColorFormatting(String formatting) {
        if (formatting != null) {
            this.formatting = formatting;
        }
    }

    public String getDefaultFormattingPrefix() {
        return formatting;
    }

}