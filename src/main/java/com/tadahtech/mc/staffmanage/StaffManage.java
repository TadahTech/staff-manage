package com.tadahtech.mc.staffmanage;

import com.tadahtech.mc.staffmanage.database.SQLConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class StaffManage extends JavaPlugin {

    private static StaffManage instance;
    private SQLConfig sqlConfig;

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

    public SQLConfig getSqlConfig() {
        return sqlConfig;
    }
}
