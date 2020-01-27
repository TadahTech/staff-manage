package com.tadahtech.mc.staffmanage.util;

import com.tadahtech.mc.staffmanage.StaffManage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UtilFreeze implements Listener {

    private static final List<UUID> PLAYERS = new ArrayList<>();

    public UtilFreeze() {
        StaffManage.getInstance().getServer().getPluginManager().registerEvents(this, StaffManage.getInstance());
    }

    public static void freeze(Player player, Location location) {
        player.setWalkSpeed(0f);
        if (location != null) {
            player.teleport(location);
        }

        PLAYERS.add(player.getUniqueId());
    }

    public static void unfreeze(Player player) {
        player.setWalkSpeed(0.25f);
        PLAYERS.remove(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        unfreeze(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!PLAYERS.contains(event.getPlayer().getUniqueId())
          || event.getFrom().getBlockY() - event.getTo().getBlockY() == 0.0
          || !this.isOnGround(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    private boolean isOnGround(Player player) {
        double y = player.getLocation().getY();
        return y == (int) y;
    }

    public static List<UUID> getFrozenPlayers() {
        return PLAYERS;
    }
}
