package com.tadahtech.mc.staffmanage.gui.category;

import com.tadahtech.mc.staffmanage.gui.subCat.PunishmentSubTypeMenu;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PunishmentCategoryButton extends MenuButton {

    private PunishmentCategory category;
    private PunishmentBuilder builder;

    public PunishmentCategoryButton(PunishmentCategory category, PunishmentBuilder builder) {
        super(category.toItemStack());
        this.category = category;
        this.builder = builder;
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {
        builder.setCategory(this.category);

        new PunishmentSubTypeMenu(category, builder).open(player);
    }
}
