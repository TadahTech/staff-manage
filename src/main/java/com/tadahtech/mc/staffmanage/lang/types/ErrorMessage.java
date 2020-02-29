package com.tadahtech.mc.staffmanage.lang.types;

import com.tadahtech.mc.staffmanage.lang.ColorFormatting;
import com.tadahtech.mc.staffmanage.lang.Message;
import com.tadahtech.mc.staffmanage.lang.MessageKey;
import com.tadahtech.mc.staffmanage.util.Colors;

public class ErrorMessage extends PrefixedMessage {

    private static final String DEFAULT_FORMAT = Colors.RED;

    public ErrorMessage(String prefix, MessageKey contents) {
        super(prefix, contents, DEFAULT_FORMAT);
    }

    public ErrorMessage(Message prefix, MessageKey contents) {
        super(prefix, contents, DEFAULT_FORMAT);
    }

    public ErrorMessage(MessageKey prefix, MessageKey contents) {
        super(prefix, contents, DEFAULT_FORMAT);
    }

    public ErrorMessage(MessageKey contents) {
        super(contents, DEFAULT_FORMAT);
    }

    @Override
    protected String getPrefix(ColorFormatting formatting) {
        if (formatting == ColorFormatting.NAME || formatting == ColorFormatting.PLAYER_NAME) {
            return Colors.YELLOW;
        } else if (formatting == ColorFormatting.TIME) {
            return Colors.GOLD;
        }

        return formatting.getDefaultFormattingPrefix();
    }

}
