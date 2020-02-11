package com.tadahtech.mc.staffmanage.gui.subCat;

import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PunishmentSubCatButton extends MenuButton {

    private PunishmentData data;
    private PunishmentBuilder builder;

    public PunishmentSubCatButton(PunishmentData data, PunishmentBuilder builder) {
        super(data.toItemStack());
        this.data = data;
        this.builder = builder;
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {
        builder.setData(this.data);

        builder.punish();
        player.closeInventory();
    }
}
