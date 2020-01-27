package com.tadahtech.mc.staffmanage.util;

import com.tadahtech.mc.staffmanage.StaffManage;
import org.bukkit.Bukkit;

/**
 *
 */
public class UtilConcurrency {

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(StaffManage.getInstance(), runnable);
    }

    public static void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(StaffManage.getInstance(), runnable);
    }

}
