package com.tadahtech.mc.staffmanage.util;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Messaging and text related utilities. These utility methods
 * are used to format text to a desirable format, in a range of
 * different ways.
 */
public class UtilText {

    private static final int CHAT_WIDTH = 320; // Max chat width (Default)
    private static final int CENTER_PX = CHAT_WIDTH / 2;
    private static final String VOWELS = "aeiouAEIOU";
    private static final String CONSONANTS = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";

    public static String splitLines(String string) {
        return splitLines(string, 30, null);
    }

    public static String splitLines(String string, int charCount, String formatting) {
        String[] split = string.split("\n");
        if (split.length != 1) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                if (i != 0) {
                    builder.append("\n");
                }

                builder.append(splitLines(split[i], charCount, formatting));
            }

            return builder.toString();
        }

        StringBuilder builder = new StringBuilder(formatting == null ? "" : formatting);

        char[] chars = string.toCharArray();
        int countSinceLastSplit = 0;
        boolean lastWasSlash = false;
        for (char ch : chars) {
            if (lastWasSlash && ch == 'n') {
                countSinceLastSplit = -1; // ++ yields 0
            }

            lastWasSlash = ch == '\\';

            boolean shouldSplitIfSpace = ++countSinceLastSplit >= charCount;
            if (shouldSplitIfSpace && ch == ' ') {
                countSinceLastSplit = 0;
                builder.append("\n").append(formatting == null ? org.bukkit.ChatColor.getLastColors(builder.toString()) : formatting);
                continue;
            }

            builder.append(ch);
        }

        return builder.toString();
    }


    public static String getIndefiniteArticle(String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("String is empty!");
        }

        char ch = string.charAt(0);
        boolean consonant = isConsonant(ch);
        boolean vowel = isVowel(ch);
        if (!consonant && !vowel) {
            throw new IllegalArgumentException("First character isn't letter!");
        }

        return consonant ? "a" : "an";
    }

    public static boolean isVowel(char ch) {
        return VOWELS.contains(ch + "");
    }

    public static boolean isConsonant(char ch) {
        return CONSONANTS.contains(ch + "");
    }

    public static String createNameSentence(Player player, Collection<String> collection) {
        return createNameSentence(player, collection.toArray(new String[collection.size()]));
    }

    public static String createNameSentence(Player player, String... names) {
        if (names == null || names.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder(names[0]);
        for (int i = 1; i < names.length; i++) {
            builder.append(", ");
            if ((i + 1) == names.length) {
                builder.append("and").append(" ");
            }

            builder.append(names[i]);
        }

        return builder.toString();
    }

    public static void sendFancyMessage(Player player, ChatColor headerColor, String... messages) {
        player.sendMessage(UtilText.createLine(headerColor));

        for (String string : messages) {
            player.sendMessage(UtilText.center(string));
        }

        player.sendMessage(UtilText.createLine(headerColor));
    }

    public static void sendFancyMessageWithTitle(Player player, String title, ChatColor headerColor, String... messages) {
        player.sendMessage(UtilText.createLineCenteredMessage(headerColor, title));

        for (String string : messages) {
            player.sendMessage(UtilText.center(string));
        }

        player.sendMessage(UtilText.createLine(headerColor));
    }

    public static void sendFancyMessageWithHoverClick(Player player, ChatColor headerColor, String[] hoverEvents, String command, String... messages) {
        String hoverText = String.join("\n", hoverEvents);
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText));
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);

        player.sendMessage(UtilText.createLine(headerColor));
        createBaseComponenent(player, hoverEvent, clickEvent, messages);

        player.sendMessage(UtilText.createLine(headerColor));
    }

    private static void createBaseComponenent(Player player, HoverEvent hoverEvent, ClickEvent clickEvent, String[] messages) {
        for (String string : messages) {
            if (string == null || string.isEmpty()) {
                player.sendMessage("");
                continue;
            }

            List<BaseComponent> components = Lists.newArrayList();

            String fill = createCenterFill(string, " ");
            Collections.addAll(components, TextComponent.fromLegacyText(fill));

            BaseComponent[] text = TextComponent.fromLegacyText(string);
            for (BaseComponent component : text) {
                component.setClickEvent(clickEvent);
                component.setHoverEvent(hoverEvent);
            }

            Collections.addAll(components, text);
            player.spigot().sendMessage(components.toArray(new BaseComponent[0]));
        }
    }

    public static void sendFancyMessageWithHoverClick(Player player, String title, ChatColor headerColor, String[] hoverEvents, String command, String... messages) {
        String hoverText = String.join("\n", hoverEvents);
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText));
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);

        player.sendMessage(UtilText.createLineCenteredMessage(headerColor, title));
        createBaseComponenent(player, hoverEvent, clickEvent, messages);

        player.sendMessage(UtilText.createLine(headerColor));
    }

    public static void sendFancyMessageWithHoverClick(Player player, ChatColor headerColor, HoverEvent hoverEvent, ClickEvent clickEvent, String... messages) {
        player.sendMessage(UtilText.createLine(headerColor));
        for (String string : messages) {
            List<BaseComponent> components = Lists.newArrayList();

            String fill = createCenterFill(string, " ");
            Collections.addAll(components, TextComponent.fromLegacyText(fill));

            BaseComponent[] text = TextComponent.fromLegacyText(string);
            for (BaseComponent component : text) {
                component.setClickEvent(clickEvent);
                component.setHoverEvent(hoverEvent);
            }

            Collections.addAll(components, text);
            player.spigot().sendMessage(components.toArray(new BaseComponent[0]));
        }

        player.sendMessage(UtilText.createLine(headerColor));
    }

    public static String toString(String... strings) {
        if (strings.length == 0) {
            return null;
        }

        return String.join(" ", strings);
    }

    public static String toString(String delimiter, String... strings) {
        if (strings.length == 0) {
            return null;
        }

        return String.join(delimiter, strings);
    }

    public static String toString(int from, String... strings) {
        if (strings.length <= from) {
            throw new IndexOutOfBoundsException();
        }

        StringBuilder builder = new StringBuilder();
        for (int i = from; i < strings.length; i++) {
            builder.append(" ").append(strings[i]);
        }

        return builder.substring(1);
    }

    public static String joinArgs(int from, String... strings) {
        return toString(from, strings);
    }

    public static String toString(int from, int to, String... strings) {
        if (strings.length <= from) {
            throw new IndexOutOfBoundsException();
        }

        StringBuilder builder = new StringBuilder();
        for (int i = from; i < strings.length && i < to; i++) {
            builder.append(" ").append(strings[i]);
        }

        return builder.substring(1);
    }

    public static String format(String name) {
        StringBuilder builder = new StringBuilder();

        if (name.contains("_")) {
            String[] str = name.split("_");
            int size = str.length;

            for (int i = 0; i < size; i++) {
                String s = str[i];

                builder.append(s, 0, 1).append(s.substring(1).toLowerCase());

                if ((i + 1) != size) {
                    builder.append(" ");
                }
            }
        } else {
            builder.append(name, 0, 1).append(name.substring(1).toLowerCase());
        }

        return builder.toString();
    }

    public static String split(String splitter, List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i));

            if ((i + 1) >= list.size()) {
                continue;
            }

            builder.append(splitter);
        }

        return builder.toString();
    }

    public static List<String> parse(String splitter, String string) {
        return Lists.newArrayList(string.split(splitter));
    }

    public static String center(String str) {
        return center(str, " ");
    }

    public static String matchLength(String toMatch, String matchWith) {
        int strLength = DefaultFontInfo.getPixelLength(toMatch);
        int iterations = Math.floorDiv(strLength, DefaultFontInfo.getPixelLength(matchWith));
        return StringUtils.repeat(matchWith, iterations);
    }

    public static String createEndingFill(String str, String fill) {
        int strLength = DefaultFontInfo.getPixelLength(str);
        int requiredFill = CHAT_WIDTH - strLength;

        int iterations = Math.floorDiv(requiredFill, DefaultFontInfo.getPixelLength(fill));
        return StringUtils.repeat(fill, iterations);
    }

    public static String createCenterFill(String str, String fill) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        fill = ChatColor.translateAlternateColorCodes('&', fill);

        StringBuilder sb = new StringBuilder();

        int fillPixelLength = DefaultFontInfo.getPixelLength(fill);
        if (fillPixelLength == 0) {
            throw new IllegalArgumentException("Fill can't have a length of 0!");
        }

        int strPixelLengthHalf = (int) Math.ceil(((double) DefaultFontInfo.getPixelLength(str)) / 2.0); // Why isn't #ceilDiv a thing?
        int toCompensate = CENTER_PX - strPixelLengthHalf;

        int iterations = Math.floorDiv(toCompensate, fillPixelLength);
        for (int i = 0; i < iterations; i++) {
            sb.append(fill);
        }

        return sb.toString();
    }

    public static String center(String str, String fill) {
        // Make sure it is translated
        str = ChatColor.translateAlternateColorCodes('&', str);
        fill = ChatColor.translateAlternateColorCodes('&', fill);

        StringBuilder sb = new StringBuilder();

        int fillPixelLength = DefaultFontInfo.getPixelLength(fill);
        if (fillPixelLength == 0) {
            throw new IllegalArgumentException("Fill can't have a length of 0!");
        }

        int strPixelLengthHalf = (int) Math.ceil(((double) DefaultFontInfo.getPixelLength(str)) / 2.0); // Why isn't #ceilDiv a thing?
        int toCompensate = CENTER_PX - strPixelLengthHalf;

        int iterations = Math.floorDiv(toCompensate, fillPixelLength);
        for (int i = 0; i < iterations; i++) {
            sb.append(fill);
        }

        String comp = sb.toString();
        return comp + ChatColor.RESET + str + ChatColor.RESET + (Objects.equals(fill, " ") ? "" : comp);
    }

    public static String createLine(ChatColor color) {
        return createLine(color.toString());
    }

    public static String createLine(String color) {
        return " " + color + ChatColor.STRIKETHROUGH + createEndingFill(" ", " ");
    }

    public static String createLineCenteredMessage(ChatColor lineColor, String center) {
        return createLineCenteredMessage(lineColor.toString(), center);
    }

    public static String createLineCenteredMessage(String lineColor, String center) {
        center = " " + center + " ";
        String left = " " + lineColor + ChatColor.STRIKETHROUGH + createCenterFill(center + " ", " ") + ChatColor.RESET;
        String right = lineColor + ChatColor.STRIKETHROUGH + createCenterFill(center, " ") + ChatColor.RESET;
        return left + center + right;
    }

}
