package com.tadahtech.mc.staffmanage.lang.types;

import com.tadahtech.mc.staffmanage.lang.Argument;
import com.tadahtech.mc.staffmanage.lang.BaseMessage;
import com.tadahtech.mc.staffmanage.lang.ColorFormatting;
import com.tadahtech.mc.staffmanage.lang.Message;
import com.tadahtech.mc.staffmanage.lang.MessageKey;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilText;
import org.bukkit.entity.Player;

public class LoreMessage extends BaseMessage {

    private static final String DEFAULT_FORMAT = Colors.GRAY;

    public LoreMessage(MessageKey contents) {
        super(contents, DEFAULT_FORMAT);
    }

    @Override
    public LoreMessage withArg(Argument argument) {
        return (LoreMessage) super.withArg(argument);
    }

    @Override
    public LoreMessage withArg(String value, ColorFormatting formatting) {
        return (LoreMessage) super.withArg(value, formatting);
    }

    @Override
    public LoreMessage withArg(Message messageValue, ColorFormatting formatting) {
        return (LoreMessage) super.withArg(messageValue, formatting);
    }

    @Override
    public LoreMessage withArg(String value) {
        return (LoreMessage) super.withArg(value);
    }

    public String splitLines(Player player) {
        return UtilText.splitLines(this.toString(player));
    }
}
