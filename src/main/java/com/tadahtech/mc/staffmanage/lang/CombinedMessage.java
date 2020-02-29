package com.tadahtech.mc.staffmanage.lang;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class CombinedMessage implements Message {

    private boolean separateWithSpace;
    private List<Message> messages;

    public CombinedMessage() {
        messages = new ArrayList<>();
    }

    public CombinedMessage(Message... messages) {
        this.messages = new ArrayList<>(Arrays.asList(messages));
    }

    public CombinedMessage(List<Message> messages) {
        this.messages = messages;
    }

    public CombinedMessage setSeparateWithSpaces(boolean separateWithSpaces) {
        separateWithSpace = separateWithSpaces;
        return this;
    }

    public CombinedMessage append(Message msg) {
        messages.add(msg);
        return this;
    }

    public CombinedMessage setMessages(List<Message> messages) {
        this.messages = new ArrayList<>(messages);
        return this;
    }

    @Override
    public String toString(Locale locale) {
        StringBuilder sb = new StringBuilder();
        boolean endsWithSpace = true;
        for (Message msg : messages) {
            String str = msg.toString(locale);

            if (separateWithSpace && !endsWithSpace && !str.startsWith(" ")) {
                sb.append(" ");
            }

            sb.append(str);

            endsWithSpace = str.endsWith(" ");
        }
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
