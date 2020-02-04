package com.tadahtech.mc.staffmanage.gui.type.buttons;

import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class KickButton extends MenuButton {
    private PunishmentData data;

    public KickButton(PunishmentData data) {
        super(new ItemBuilder(Material.BARRIER).setTitle(Colors.AQUA + "Kick").build());
        this.data = data;
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {

    }
}
