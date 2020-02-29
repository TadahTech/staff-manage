package com.tadahtech.mc.staffmanage.lang.types;

import com.tadahtech.mc.staffmanage.lang.Message;
import com.tadahtech.mc.staffmanage.lang.MessageKey;
import com.tadahtech.mc.staffmanage.util.Colors;

public class RegularMessage extends PrefixedMessage {

    public static final String DEFAULT_FORMAT = Colors.GRAY;

    public RegularMessage(String prefix, MessageKey contents) {
        super(prefix, contents, DEFAULT_FORMAT);
    }

    public RegularMessage(Message prefix, MessageKey contents) {
        super(prefix, contents, DEFAULT_FORMAT);
    }

    public RegularMessage(MessageKey prefix, MessageKey contents) {
        super(prefix, contents, DEFAULT_FORMAT);
    }

    public RegularMessage(MessageKey contents) {
        super(contents, DEFAULT_FORMAT);
    }


}
