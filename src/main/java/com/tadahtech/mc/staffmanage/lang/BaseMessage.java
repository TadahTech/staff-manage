package com.tadahtech.mc.staffmanage.lang;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BaseMessage implements Message {

    private static final long serialVersionUID = 1;
    private String defaultFormat;
    private MessageKey key;
    private List<Argument> arguments = new ArrayList<>();

    public BaseMessage(MessageKey key, String defaultFormatting) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        this.key = key;
        this.defaultFormat = defaultFormatting != null ? ChatColor.RESET + defaultFormatting : "";
    }

    public BaseMessage withArg(Argument argument) {
        arguments.add(argument);
        return this;
    }

    public BaseMessage withArg(MessageKey key, ColorFormatting formatting) {
        String message = MultiLanguageManager.getVersion(key, null);
        return this.withArg(message, formatting);
    }

    public BaseMessage withArg(MessageKey key) {
        String message = MultiLanguageManager.getVersion(key, null);
        return this.withArg(message);
    }

    public BaseMessage withArg(String value, ColorFormatting formatting) {
        withArg(new Argument(value, formatting));
        return this;
    }

    public BaseMessage withArg(Message messageValue, ColorFormatting formatting) {
        withArg(new Argument(messageValue, formatting));
        return this;
    }

    public BaseMessage withArg(String value) {
        withArg(new Argument(value));
        return this;
    }

    public BaseMessage setArgs(List<Argument> args) {
        arguments = new ArrayList<>(args);
        return this;
    }


    protected String getPrefix(ColorFormatting formatting) {
        if (formatting == null || formatting == ColorFormatting.NONE) {
            return "";
        }
        return formatting.getDefaultFormattingPrefix();
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
        List<Object> args = new ArrayList<>();
        for (Argument arg : arguments) {
            String argString = arg.toString(locale);
            ColorFormatting formatting = arg.getFormatting();

            if (formatting == null) {
                formatting = ColorFormatting.NONE;
            }

            argString = getPrefix(formatting) + argString + defaultFormat;

            args.add(argString);
        }

        return defaultFormat + ChatColor.translateAlternateColorCodes('&', new MultiArgumentBase(key, args).toString(locale));
    }

}
