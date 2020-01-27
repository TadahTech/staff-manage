package com.tadahtech.mc.staffmanage;

import org.bukkit.plugin.java.JavaPlugin;

public final class StaffManage extends JavaPlugin {

    private static StaffManage instance;

    public static StaffManage getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
    }
}
