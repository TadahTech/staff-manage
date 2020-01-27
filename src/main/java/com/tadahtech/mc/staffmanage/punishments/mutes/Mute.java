package com.tadahtech.mc.staffmanage.punishments.mutes;

import com.tadahtech.mc.staffmanage.punishments.Punishment;

public interface Mute extends Punishment {

    @Override
    default boolean shouldPersist() {
        return true;
    }

}
