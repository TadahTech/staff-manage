package com.tadahtech.mc.staffmanage.lang.types;

import com.google.common.collect.Lists;
import com.tadahtech.mc.staffmanage.lang.Argument;
import com.tadahtech.mc.staffmanage.lang.Message;
import com.tadahtech.mc.staffmanage.lang.MessageKey;
import com.tadahtech.mc.staffmanage.lang.Messaging;
import com.tadahtech.mc.staffmanage.lang.MultiArgumentBase;
import com.tadahtech.mc.staffmanage.lang.MultiLanguageManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UnformattedMessage implements Message {

    private static final long serialVersionUID = 1;
    private MessageKey key;
    private List<Argument> arguments = new ArrayList<>();

    public UnformattedMessage(MessageKey key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        this.key = key;
    }

    public UnformattedMessage withArg(Argument argument) {
        arguments.add(argument);
        return this;
    }

    public UnformattedMessage withArg(Message messageValue) {
        withArg(new Argument(messageValue));
        return this;
    }

    public UnformattedMessage withArg(String value) {
        withArg(new Argument(value));
        return this;
    }

    public UnformattedMessage setArgs(List<Argument> args) {
        arguments = new ArrayList<>(args);
        return this;
    }

    @Override
    public String toString(Player player) {
        return toString(MultiLanguageManager.getPlayerLocale());
    }

    @Override
    public String toString() {
        return toString(MultiLanguageManager.DEFAULT);
    }

    @Override
    public String toString(Locale locale) {
        List<Object> args = Lists.newArrayList();
        for (Argument arg : this.arguments) {
            args.add(arg.toString(locale));
        }

        return Messaging.colorEachWord(new MultiArgumentBase(key, args).toString(locale));
    }
}
