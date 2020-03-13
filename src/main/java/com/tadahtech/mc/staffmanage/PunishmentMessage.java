package com.tadahtech.mc.staffmanage;

import com.tadahtech.mc.staffmanage.lang.MessageKey;

public enum PunishmentMessage implements MessageKey {

    PREFIX("&6&lHydra&f&l(Punishments) &e» &f"),
    GENERAL_LIST_PREVIOUS_PAGE("Previous Page"),
    GENERAL_LIST_CLICK_TO_PREV("Click to go to previous page"),
    GENERAL_LIST_NEXT_PAGE("Next Page"),
    GENERAL_LIST_CLICK_TO_NEXT("Click to go to next page"),
    PUNISHMENTS_RECORD_HEADER("{0}''s criminal record &7({1}&7)"),
    PUNISHMENTS_HISTORY_TIMESTAMP("&8({1}&8)&r &7{0}:"),
    PUNISHMENTS_HISTORY_TYPE("Type: {0}"),
    PUNISHMENTS_HISTORY_LENGTH("Length: {0}"),
    PUNISHMENTS_HISTORY_REASON("Reason: {0} - {1}"),
    PUNISHMENTS_HISTORY_INITIATOR("Initiator: {0}"),
    STAFF_DUPEIP_NO_DUPLICATES("No duplicate IPs were found!"),
    STAFF_DUPEIP_HEADER("&4&lDuplicate IPs"),
    STAFF_DUPEIP_NO_PLAYERS_ON_IP("No players found on IP {0}!"),
    ;

    private String english;

    PunishmentMessage(String english) {
        this.english = english;
    }

    @Override
    public String getKey() {
        return name().toLowerCase().replace("_", "-");
    }

    @Override
    public String getEnglishVersion() {
        return english;
    }
}
