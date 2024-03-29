package com.tadahtech.mc.staffmanage.util.item.nbt;

import org.bukkit.inventory.ItemStack;

/**
 * NBT copy of Bukkit {@link ItemStack}
 */
public class NBTItem {
    private ItemStack bukkititem;

    public NBTItem(ItemStack item) {
        bukkititem = item.clone();
    }

    public ItemStack getItem() {
        return bukkititem;
    }

    public void setString(String Key, String Text) {
        bukkititem = NBTReflectionUtils.setString(bukkititem, Key, Text);
    }

    public String getString(String Key) {
        return NBTReflectionUtils.getString(bukkititem, Key);
    }

    public void setInteger(String key, Integer Int) {
        bukkititem = NBTReflectionUtils.setInt(bukkititem, key, Int);
    }

    public Integer getInteger(String key) {
        return NBTReflectionUtils.getInt(bukkititem, key);
    }

    public void setDouble(String key, Double d) {
        bukkititem = NBTReflectionUtils.setDouble(bukkititem, key, d);
    }

    public Double getDouble(String key) {
        return NBTReflectionUtils.getDouble(bukkititem, key);
    }

    public void setBoolean(String key, Boolean b) {
        bukkititem = NBTReflectionUtils.setBoolean(bukkititem, key, b);
    }

    public Boolean getBoolean(String key) {
        return NBTReflectionUtils.getBoolean(bukkititem, key);
    }

    public Boolean hasKey(String key) {
        return NBTReflectionUtils.hasKey(bukkititem, key);
    }
}
