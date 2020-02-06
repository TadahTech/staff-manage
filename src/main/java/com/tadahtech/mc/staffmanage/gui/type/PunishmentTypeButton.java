package com.tadahtech.mc.staffmanage.gui.type;

import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PunishmentTypeButton extends MenuButton {

    private PunishmentBuilder builder;
    private PunishmentType type;

    public PunishmentTypeButton(PunishmentType type, PunishmentBuilder builder) {
        super(type.toItemStack());
        this.builder = builder;
        this.type = type;
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {
        builder.setType(type);
        builder.punish();

        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
        player.closeInventory();
    }
}
