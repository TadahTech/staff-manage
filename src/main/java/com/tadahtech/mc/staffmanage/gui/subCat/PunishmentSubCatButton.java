package com.tadahtech.mc.staffmanage.gui.subCat;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.gui.type.PunishmentTypeMenu;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import com.tadahtech.mc.staffmanage.util.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PunishmentSubCatButton extends MenuButton {

    private PunishmentData data;

    public PunishmentSubCatButton(PunishmentData data) {
        super(data.toItemStack());
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {
        PunishmentBuilder builder = StaffManager.getInstance().getPunishmentManager().getBuilderManager().getBuilder(player.getUniqueId());

        if (builder == null) {
            player.closeInventory();
            player.sendMessage(Colors.RED + "Failed to store builder state: SubCat");
            throw new IllegalArgumentException("Bad Builder State: SubCat");
        }

        builder.setData(this.data);

        new PunishmentTypeMenu(data).open(player);
    }
}
