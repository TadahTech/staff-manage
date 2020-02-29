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

    public PunishmentType getTypeFor(int index) {
        return this.types.get(index);
    }

    public PunishmentType getNext(PunishmentType current) {
        if (current == null) {
            return null;
        }

        int index = this.types.indexOf(current) + 1;

        if (index == this.types.size()) {
            return null;
        }

        return this.types.get(index);
    }

    public PunishmentLength getLengthFor(PunishmentType type, int index) {
        if (this.lengths == null || this.lengths.isEmpty()) {
            return null;
        }

        LinkedList<PunishmentLength> lengths = this.lengths.get(type);

        if (lengths == null || lengths.isEmpty()) {
            return null;
        }

        if (index >= lengths.size()) {
            return null;
        }

        return lengths.get(index);
    }

    public ItemStack toItemStack(PunishmentLength currentLength, PunishmentType currentType) {
        ItemBuilder builder = new ItemBuilder(getIcon()).setTitle(Colors.GOLD + getGuiName());

        List<String> lore = Lists.newArrayList();

        lore.add(" ");
        lore.add(Colors.GOLD + Colors.BOLD + Colors.UNDERLINE + "Punishments");
        lore.add(" ");

        PunishmentType nextType = getNext(currentType);
        boolean onNext = false;

        for (int t = 0; t < this.types.size(); t++) {
            PunishmentType type = this.types.get(t);

            if (nextType == null) {
                if (t == 0) {
                    lore.add(Colors.DARK_GRAY + "- " + Colors.GOLD + type.getUiName());
                } else {
                    lore.add(Colors.DARK_GRAY + "- " + Colors.WHITE + type.getUiName());
                }
            } else {
                if (nextType == type) {
                    lore.add(Colors.DARK_GRAY + "- " + Colors.GOLD + type.getUiName());
                    onNext = true;
                } else {
                    onNext = false;
                    if (type == currentType) {
                        lore.add(Colors.DARK_GRAY + "- " + Colors.GREEN + type.getUiName());
                    } else {
                        lore.add(Colors.DARK_GRAY + "- " + Colors.DARK_GRAY + type.getUiName());
                    }
                }
            }

            if (type == PunishmentType.TEMP_MUTE || type == PunishmentType.TEMP_BAN) {
                LinkedList<PunishmentLength> lengths = this.lengths.get(type);

                if (lengths == null) {
                    continue;
                }

                boolean doneNext = false;
                for (int i = 0; i < lengths.size(); i++) {
                    PunishmentLength length = lengths.get(i);

                    if (currentLength == null) {
                        if (onNext && !doneNext) {
                            doneNext = true;
                            lore.add(Colors.DARK_GRAY + "  - " + Colors.GREEN + length.toSentence());
                            continue;
                        }
                        lore.add(Colors.DARK_GRAY + "  - " + Colors.DARK_GRAY + length.toSentence());
                        continue;
                    }

                    if (length.getMillis() < currentLength.getMillis()) {
                        lore.add(Colors.DARK_GRAY + "  - " + Colors.RED + length.toSentence());
                        continue;
                    }

                    if (length.getMillis() == currentLength.getMillis()) {
                        lore.add(Colors.DARK_GRAY + "  - " + Colors.GREEN + length.toSentence());

                        if ((i + 1) != lengths.size()) {
                            PunishmentLength next = lengths.get(i + 1);
                            lore.add(Colors.DARK_GRAY + "  - " + Colors.GOLD + next.toSentence());
                            i++;
                        }

                        continue;
                    }

                    lore.add(Colors.DARK_GRAY + "  - " + Colors.WHITE + length.toSentence());
                }
            }
            lore.add(" ");
        }

        lore.add(" ");

        builder.setLore(lore);

        return builder.build();
    }

    public String getGuiName() {
        return guiName;
    }

    public void setLengths(Map<PunishmentType, LinkedList<PunishmentLength>> lengths) {
        this.lengths = lengths;
    }
}
