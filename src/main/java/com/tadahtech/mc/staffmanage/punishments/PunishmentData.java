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
    private String guiName;
    private Material icon;
    private LinkedList<PunishmentType> types;
    private Map<PunishmentType, LinkedList<PunishmentLength>> lengths;

    public PunishmentData(String name, String guiName, Material icon, LinkedList<PunishmentType> types, Map<PunishmentType, LinkedList<PunishmentLength>> lengths) {
        this.name = name;
        this.guiName = guiName;
        this.types = types;
        this.icon = icon;
        this.lengths = lengths;
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public PunishmentType getTpeFor(int index) {
        return this.types.get(index);
    }

    public PunishmentType getNext(PunishmentType current) {
        int index = this.types.indexOf(current) + 1;
        return this.types.get(index);
    }

    public PunishmentLength getLengthFor(PunishmentType type, int index) {
        LinkedList<PunishmentLength> lengths = this.lengths.get(type);

        if (lengths == null || lengths.isEmpty()) {
            return null;
        }

        if (index >= lengths.size()) {
            return null;
        }

        return lengths.get(index);
    }

    public ItemStack toItemStack() {
        ItemBuilder builder = new ItemBuilder(getIcon()).setTitle(Colors.GOLD + getGuiName());

        List<String> lore = Lists.newArrayList();

        lore.add(" ");
        lore.add(Colors.GOLD + Colors.BOLD + Colors.UNDERLINE + "Punishments");
        lore.add(" ");

        for (PunishmentType type : this.types) {
            lore.add(Colors.DARK_GRAY + "- " + Colors.WHITE + type.getUiName());
        }

        lore.add(" ");

        builder.setLore(lore);

        return builder.build();
    }

    public String getGuiName() {
        return guiName;
    }
}
