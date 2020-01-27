package com.tadahtech.mc.staffmanage.util.item;

import com.tadahtech.mc.staffmanage.util.item.nbt.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class ItemTags implements Listener {
    public static final String TAG_KEY = "tag";

    public ItemTags(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static ItemStack applyTag(ItemStack itemStack, ItemTag... tags) {
        NBTItem item = new NBTItem(itemStack);
        item.setString(TAG_KEY, Arrays.toString(tags));
        return item.getItem();
    }

    public static boolean hasTag(ItemStack itemStack, ItemTag tag) {
        NBTItem item = new NBTItem(itemStack);
        if (!item.hasKey(TAG_KEY)) {
            return false;
        }

        String tags = item.getString(TAG_KEY);
        return tags.contains(tag.name());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        if (!hasTag(event.getCurrentItem(), ItemTag.NO_MOVE)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (event.getItem() == null) {
            return;
        }

        if (!hasTag(event.getItem(), ItemTag.NO_MOVE)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop() == null) {
            return;
        }

        if (!hasTag(event.getItemDrop().getItemStack(), ItemTag.NO_DROP)) {
            return;
        }

        event.setCancelled(true);
    }

    public enum ItemTag {
        NO_MOVE,
        NO_DROP
    }
}
