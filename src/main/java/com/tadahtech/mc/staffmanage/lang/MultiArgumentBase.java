package com.tadahtech.mc.staffmanage.lang;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.text.Format;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

public class MultiArgumentBase implements Message {

    private MessageKey key;
    private List<Object> args;

    public MultiArgumentBase(MessageKey key) {
        this.key = key;
        this.args = Lists.newArrayList();
    }

    public MultiArgumentBase(MessageKey key, List<Object> arguments) {
        if (key == null) {
            throw new NullPointerException();
        }
        this.key = key;
        this.args = Lists.newArrayList(arguments);
    }

    public MessageKey getKey() {
        return key;
    }

    @Override
    public String toString(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        String formatString = MultiLanguageManager.getVersion(key, locale);

        if (args.isEmpty()) { // no arguments to deal with
            return formatString;
        }

        MessageFormat format = new MessageFormat(formatString, locale);

        Format[] formats = format.getFormatsByArgumentIndex();
        Object[] argArray = new Object[args.size()];
        for (int i = 0; i < formats.length && i < argArray.length; i++) {
            argArray[i] = args.get(i);
            if (argArray[i] instanceof Message) {
                argArray[i] = ((Message) argArray[i]).toString(locale);
            }

            if (formats[i] != null) {
                argArray[i] = formats[i].format(argArray[i]);
                format.setFormatByArgumentIndex(i, null);
            }
        }

        return format.format(argArray, new StringBuffer(), null).toString();
    }

    @Override
    public String toString(Player player) {
        return toString();
    }

    @Override
    public String toString() {
        return toString(MultiLanguageManager.DEFAULT);
    }

    public MultiArgumentBase addArg(Object nextArg) {
        this.args.add(nextArg);
        return this;
    }

    public MultiArgumentBase setArgs(List<Object> args) {
        this.args = Lists.newArrayList(args);
        return this;
    }

}
