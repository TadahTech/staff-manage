package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PunishmentCategory {

    private String name;
    private Material icon;
    private Map<String, PunishmentData> punishments;
    private final ItemStack itemStack;

    public PunishmentCategory(String name, Material icon) {
        this.name = name;
        this.icon = icon;
        this.punishments = Maps.newHashMap();
        this.itemStack = new ItemBuilder(icon).setTitle(Colors.AQUA + name).build();
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public Map<String, PunishmentData> getPunishments() {
        return punishments;
    }

    public PunishmentData getDataFor(String subcategory) {
        return this.punishments.get(subcategory.toLowerCase());
    }

    public void add(PunishmentData data) {
        this.punishments.put(data.getName().toLowerCase(), data);
    }

    public ItemStack toItemStack() {
        return itemStack.clone();
    }
}
