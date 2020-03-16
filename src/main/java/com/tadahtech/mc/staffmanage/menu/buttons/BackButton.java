package com.tadahtech.mc.staffmanage.menu.buttons;

import com.tadahtech.mc.staffmanage.PunishmentMessage;
import com.tadahtech.mc.staffmanage.lang.types.RegularMessage;
import com.tadahtech.mc.staffmanage.menu.Menu;
import com.tadahtech.mc.staffmanage.util.item.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Sends a player back to the main page
 */
public class BackButton extends MenuButton {

    private Menu menu;

    public BackButton(Player player, Menu menu) {
        super(new ItemBuilder(Material.BED)
          .setTitle(ChatColor.GRAY + "\u2190 " + new RegularMessage(PunishmentMessage.GO_BACK).toString(player))
          .build());
        this.menu = menu;
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {
        player.closeInventory();
        this.menu.open(player);
    }
}