package com.tadahtech.mc.staffmanage.lang.types;

import com.tadahtech.mc.staffmanage.lang.BaseMessage;
import com.tadahtech.mc.staffmanage.lang.Message;
import com.tadahtech.mc.staffmanage.lang.MessageKey;
import com.tadahtech.mc.staffmanage.lang.MultiArgumentBase;
import com.tadahtech.mc.staffmanage.lang.MultiLanguageManager;
import com.tadahtech.mc.staffmanage.lang.StringMessage;
import com.tadahtech.mc.staffmanage.util.Colors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

public class PrefixedMessage extends BaseMessage {

    private Message prefix;

    public PrefixedMessage(String prefix, MessageKey contents, String defaultFormatting) {
        this(contents, defaultFormatting);
        this.prefix = StringMessage.wrap(prefix);
    }

    public PrefixedMessage(Message prefix, MessageKey contents, String defaultFormatting) {
        this(contents, defaultFormatting);
        this.prefix = prefix;
    }

    public PrefixedMessage(MessageKey prefix, MessageKey contents, String defaultFormatting) {
        this(contents, defaultFormatting);
        this.prefix = new MultiArgumentBase(prefix);
    }

    public PrefixedMessage(MessageKey prefix, MessageKey contents) {
        this(contents, (String) null);
        this.prefix = new MultiArgumentBase(prefix);
    }

    public PrefixedMessage(MessageKey contents, String defaultFormatting) {
        super(contents, defaultFormatting);
    }

    public static String formatPrefix(Message prefix, Locale locale) {
        if (prefix == null) {
            return "";
        }
        return Colors.DARK_GRAY + "[" + ChatColor.translateAlternateColorCodes('&', prefix.toString(locale)) + Colors.DARK_GRAY + "] ";
    }

    public static String formatPrefix(Message prefix) {
        if (prefix == null) {
            return "";
        }
        return Colors.DARK_GRAY + "[" + ChatColor.translateAlternateColorCodes('&', prefix.toString()) + Colors.DARK_GRAY + "] ";
    }

    @Override
    public String toString(Locale locale) {
        StringBuilder sb = new StringBuilder();

        if (prefix != null) {
            sb.append(formatPrefix(prefix, locale));
        }

        sb.append(super.toString(locale));

        return sb.toString();
    }

    @Override
    public String toString(Player player) {
        return toString(MultiLanguageManager.getPlayerLocale());
    }

    @Override
    public String toString() {
        return toString(MultiLanguageManager.DEFAULT);
    }

}
