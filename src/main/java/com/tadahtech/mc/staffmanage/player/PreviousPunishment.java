package com.tadahtech.mc.staffmanage.player;

import com.tadahtech.mc.staffmanage.length.PunishmentLength;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;

public class PreviousPunishment {

    private PunishmentType type;
    private PunishmentLength length;

    public PreviousPunishment(PunishmentType type, PunishmentLength length) {
        this.type = type;
        this.length = length;
    }

    public PunishmentType getType() {
        return type;
    }

    public PunishmentLength getLength() {
        return length;
    }
}
