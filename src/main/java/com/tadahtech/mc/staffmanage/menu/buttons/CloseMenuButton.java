package com.tadahtech.mc.staffmanage.menu.buttons;

import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class CloseMenuButton extends MenuButton {

    public CloseMenuButton() {
        super(new ItemBuilder(Material.BARRIER)
          .setTitle(Colors.MENU_ITEM + "Close Menu")
          .setLore("", Colors.LORE + "Click to close menu!")
          .build());
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {
        player.closeInventory();
    }
}
