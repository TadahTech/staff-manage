package com.tadahtech.mc.staffmanage.menu.listeners;

import com.tadahtech.mc.staffmanage.StaffManage;
import com.tadahtech.mc.staffmanage.menu.Menu;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

/**
 * Listener for the Menu system
 */
public class MenuListener implements Listener {

    public MenuListener() {
        StaffManage.getInstance().getServer().getPluginManager().registerEvents(this, StaffManage.getInstance());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu gui = Menu.get(player.getUniqueId());

        if (gui == null) {
            return;
        }

        if (event.getClick() == ClickType.DOUBLE_CLICK || event.isShiftClick()) {
            event.setCancelled(true);
        }

        if (!event.getInventory().getTitle().equals(gui.getName())) {
            return;
        }

        MenuButton button = gui.getButton(event.getRawSlot());
        InventoryView view = event.getView();

        if (event.getClickedInventory() == view.getTopInventory()) {
            event.setCancelled(true);
        }

        if (button == null) {
            return;
        }

        button.onClick(player, event.getClick(), event.getSlot());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu gui = Menu.get(player.getUniqueId());

        if (gui == null) {
            return;
        }

        if (gui.isUseClose()) {
            gui.onClose(player);
            gui.setUseClose(false);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Menu.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent event) {
        Inventory source = event.getSource();
        if (!(source instanceof PlayerInventory)) {
            return;
        }

        Player player = (Player) source.getHolder();
        Menu gui = Menu.get(player.getUniqueId());
        if (gui == null) {
            return;
        }

        // Don't transfer items between inventories
        if (event.getSource() == event.getDestination()) {
            return;
        }

        // Don't shift click shit into the menu, players are idiots
        event.setCancelled(true);
    }
}
