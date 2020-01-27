package com.tadahtech.mc.staffmanage.util.item.cooldown;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilAction;
import com.tadahtech.mc.staffmanage.util.UtilTime;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

/**
 * The manager and listener for a set of item cooldowns.
 */
public abstract class ItemCooldownManager implements Listener {

    private static final String ITEM_COOLDOWN = Colors.RED + Colors.BOLD + "Item is on cooldown! " + Colors.RESET + Colors.GRAY +
      "You can't use this item for another " + Colors.ELEMENT + "%ss" + Colors.GRAY + "!";

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    public ItemCooldownManager() {
        StaffManager.getInstance().getServer().getPluginManager().registerEvents(this, StaffManager.getInstance());
    }

    public abstract ItemCooldownHolder getHolder(Player player);

    public abstract long getCooldown(MaterialData data);

    public abstract long getConsumeCooldown(MaterialData data);

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ItemStack item = event.getItem();

        MaterialData data = new MaterialData(item.getType(), (byte) item.getDurability());
        long cooldown = this.getConsumeCooldown(data);
        if (cooldown <= 0) {
            return; // No cooldown
        }

        Player player = event.getPlayer();
        ItemCooldownHolder holder = this.getHolder(player);
        if (holder == null) {
            return; // Guess this depends on your implementation
        }

        long lastUse = holder.getLastUse(data);
        if (UtilTime.hasElapsed(lastUse, cooldown)) {
            holder.use(data);
            return;
        }

        event.setCancelled(true);
        long timeLeft = (lastUse + cooldown) - System.currentTimeMillis();
        String timeStr = FORMAT.format((double) timeLeft / 1000.0);
        player.sendMessage(String.format(ITEM_COOLDOWN, timeStr));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.ENDER_PEARL && !UtilAction.isRightClick(event.getAction())) {
            return;
        }

        MaterialData data = new MaterialData(item.getType(), (byte) item.getDurability());
        long cooldown = this.getCooldown(data);
        if (cooldown <= 0) {
            return; // No cooldown
        }

        Player player = event.getPlayer();
        ItemCooldownHolder holder = this.getHolder(player);
        if (holder == null) {
            return; // Guess this depends on your implementation
        }

        long lastUse = holder.getLastUse(data);
        if (UtilTime.hasElapsed(lastUse, cooldown)) {
            holder.use(data);
            return;
        }

        event.setCancelled(true);
        long timeLeft = (lastUse + cooldown) - System.currentTimeMillis();
        String timeStr = FORMAT.format((double) timeLeft / 1000.0);
        player.sendMessage(String.format(ITEM_COOLDOWN, timeStr));
    }
}
