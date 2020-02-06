package com.tadahtech.mc.staffmanage.punishments;

import com.google.common.collect.Lists;
import com.tadahtech.mc.staffmanage.length.PunishmentLength;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PunishmentData {

    private String name;
    private Material icon;
    private boolean allowBan;
    private boolean allowMute;
    private boolean allowIPBan;
    private Map<PunishmentType, LinkedList<PunishmentLength>> lengths;
    private int slots;

    public PunishmentData(String name, Material icon, boolean allowBan, boolean allowMute, boolean allowIPBan, Map<PunishmentType, LinkedList<PunishmentLength>> lengths) {
        this.name = name;
        this.icon = icon;
        this.allowBan = allowBan;
        this.allowMute = allowMute;
        this.allowIPBan = allowIPBan;
        this.lengths = lengths;
        this.slots = 4;

        if (allowBan) {
            slots++;
        }

        if (allowMute) {
            slots++;
        }

        if (allowIPBan) {
            slots++;
        }
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public boolean isAllowBan() {
        return allowBan;
    }

    public boolean isAllowMute() {
        return allowMute;
    }

    public boolean isAllowIPBan() {
        return allowIPBan;
    }

    public Map<PunishmentType, LinkedList<PunishmentLength>> getLengths() {
        return lengths;
    }

    public PunishmentLength getLengthFor(PunishmentType type, int index) {
        LinkedList<PunishmentLength> lengths = this.lengths.get(type);

        if (lengths == null) {
            throw new IllegalArgumentException("no lengths found for type " + type.name());
        }

        return lengths.get(index);
    }

    public int getSlots() {
        return slots;
    }

    public ItemStack toItemStack() {
        ItemBuilder builder = new ItemBuilder(icon).setTitle(name);

        List<String> lore = Lists.newArrayList();

        lore.add(itemize("Allow Perm Ban", this.allowBan));
        lore.add(itemize("Allow Perm Mute", this.allowMute));
        lore.add(itemize("Allow IP Ban", this.allowIPBan));

        builder.setLore(lore);

        return builder.build();
    }

    protected static String itemize(String label, boolean value) {
        return Colors.GOLD + label + ": " + (value ? (Colors.GREEN + "True") : (Colors.RED + "False"));
    }
}
