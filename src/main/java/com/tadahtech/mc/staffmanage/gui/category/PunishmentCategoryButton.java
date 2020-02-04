package com.tadahtech.mc.staffmanage.gui.category;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.gui.subCat.PunishmentSubTypeMenu;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import com.tadahtech.mc.staffmanage.util.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PunishmentCategoryButton extends MenuButton {

    private PunishmentCategory category;

    public PunishmentCategoryButton(PunishmentCategory category) {
        super(category.toItemStack());
        this.category = category;
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {
        PunishmentBuilder builder = StaffManager.getInstance().getPunishmentManager().getBuilderManager().getBuilder(player.getUniqueId());

        if (builder == null) {
            player.closeInventory();
            player.sendMessage(Colors.RED + "Failed to store builder state: Category");
            throw new IllegalArgumentException("Bad Builder State: Category");
        }

        builder.setCategory(this.category);

        new PunishmentSubTypeMenu(category).open(player);
    }
}
