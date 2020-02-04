package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.collect.Lists;
import com.tadahtech.mc.staffmanage.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PunishmentCategory {

    private String name;
    private Material icon;
    private List<PunishmentData> punishments;
    private final ItemStack itemStack;

    public PunishmentCategory(String name, Material icon) {
        this.name = name;
        this.icon = icon;
        this.punishments = Lists.newArrayList();
        this.itemStack = new ItemBuilder(icon).setTitle(name).build();
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public List<PunishmentData> getPunishments() {
        return punishments;
    }

    public void setPunishments(List<PunishmentData> punishments) {
        this.punishments = punishments;
    }

    public ItemStack toItemStack() {
        return itemStack.clone();
    }
}
