package com.tadahtech.mc.staffmanage.listener;

import com.tadahtech.mc.staffmanage.StaffManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface PunishmentListener extends Listener {

    default void startListening() {
        Bukkit.getPluginManager().registerEvents(this, StaffManager.getInstance());
    }

    default void stopListening() {
        HandlerList.unregisterAll(this);
    }

}
