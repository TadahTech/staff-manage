package com.tadahtech.mc.staffmanage.util;

import com.tadahtech.mc.staffmanage.StaffManager;
import org.bukkit.Bukkit;

/**
 *
 */
public class UtilConcurrency {

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(StaffManager.getInstance(), runnable);
    }

    public static void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(StaffManager.getInstance(), runnable);
    }

}
