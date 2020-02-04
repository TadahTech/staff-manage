package com.tadahtech.mc.staffmanage.gui.type.buttons;

import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class TempMuteButton extends MenuButton {
    private PunishmentData data;

    public TempMuteButton(PunishmentData data) {
        super(new ItemBuilder(Material.BARRIER).setTitle(Colors.YELLOW + "Temp Mute").build());
        this.data = data;
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {

    }
}