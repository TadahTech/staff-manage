package com.tadahtech.mc.staffmanage.punishments;

import com.tadahtech.mc.staffmanage.util.UtilTime;

import java.util.Date;

public class PunishmentLength {

    private long millis;

    public PunishmentLength(String simple) {
        this(UtilTime.toMillis(simple));
    }

    public PunishmentLength(long millis) {
        this.millis = millis;
    }

    public String toSentence() {
        return UtilTime.toSentence(this.millis);
    }

    public long getMillis() {
        return millis;
    }

    public Date toDate() {
        return new Date(System.currentTimeMillis() + this.getMillis());
    }
}
