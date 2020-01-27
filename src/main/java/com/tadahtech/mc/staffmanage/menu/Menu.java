package com.tadahtech.mc.staffmanage.menu;

import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.util.SchedulerUtils;
import com.tadahtech.mc.staffmanage.util.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A class to manage dynamic creation of GUI's
 */
public abstract class Menu {

    protected static final Map<UUID, Menu> MENUS = new HashMap<>();

    protected static final int BACK_SLOT = 0;

    public static final ItemStack PANE = new ItemBuilder(Material.STAINED_GLASS_PANE).setTitle(" ").setPaneColor(DyeColor.GREEN).build();
    protected static final ItemStack BLACK_PANE = new ItemBuilder(Material.STAINED_GLASS_PANE).setTitle(" ").setData(DyeColor.BLACK.getWoolData()).build();
    protected static final ItemStack YELLOW_PANE = new ItemBuilder(Material.STAINED_GLASS_PANE).setTitle(" ").setData(DyeColor.YELLOW.getWoolData()).build();

    private String name;
    private boolean useClose = false;
    private MenuButton[] buttons;
    private Player player;
    private Inventory inventory;

    public Menu(String name) {
        this.name = name;
    }

    public static Menu get(UUID name) {
        return MENUS.get(name);
    }

    public static Menu remove(UUID uniqueId) {
        return MENUS.remove(uniqueId);
    }

    public String getName() {
        return name;
    }

    /**
     * Open and setup the inventory for the player to view
     * Store a reference to it inside a map for retrieving later
     *
     * @param player The player who we wish to showSimple the GUI to
     */
    public void open(Player player) {
        this.player = player;
        setButtons(setUp(player));

        Menu oldMenu = MENUS.remove(player.getUniqueId());
        if (oldMenu != this && oldMenu != null && oldMenu.isUseClose()) {
            oldMenu.onClose(player);
        }

        MENUS.put(player.getUniqueId(), this);

        int size = (buttons.length + 8) / 9 * 9;
        Inventory inventory = Bukkit.createInventory(player, size, name);
        populate(inventory);

        player.openInventory(inventory);
        this.setUseClose(true);
    }

    /**
     * Set up the GUI with buttons
     *
     * @return The setup button array
     */
    protected abstract MenuButton[] setUp(Player player);

    public MenuButton[] getButtons() {
        return buttons;
    }

    public void setButtons(MenuButton[] buttons) {
        this.buttons = buttons;
    }

    /**
     * Retrieve the button based off the slot
     *
     * @param slot The slot in the inventory
     * @return The button corresponding to that slot
     */
    public MenuButton getButton(int slot) {
        try {
            return buttons[slot];
        } catch (ArrayIndexOutOfBoundsException e) {
            //There isn't a button there, so no need to throw an error
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Replace a button, or create a new button dynamically
     * Update the players GUI
     *
     * @param slot   The slot to set the new button
     * @param button The reference to the button
     */
    public void setButton(int slot, MenuButton button) {
        try {
            buttons[slot] = button;
        } catch (ArrayIndexOutOfBoundsException ignored) {
            ignored.printStackTrace();
        }
        update();
    }

    /**
     * Refresh the players view, allows to change what the player sees, without opening and closing the GUI
     */
    public void update() {
        InventoryView view = player.getOpenInventory();

        Inventory inventory = view.getTopInventory();
        populate(inventory);
    }

    /**
     * Reset this players current menu's buttons and refresh the page
     */
    public void resetAndUpdate() {
        InventoryView view = player.getOpenInventory();
        Inventory inventory = view.getTopInventory();

        this.setButtons(setUp(player));

        if (inventory.getSize() != this.getButtons().length) {
            int size = (buttons.length + 8) / 9 * 9;
            inventory = Bukkit.createInventory(player, size, name);
            populate(inventory);

            this.setUseClose(false);
            player.openInventory(inventory);
            this.setUseClose(true);
            return;
        }

        populate(inventory);
    }

    private void populate(Inventory inventory) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == null) {
                inventory.setItem(i, new ItemStack(Material.AIR));
                continue;
            }

            buttons[i].update();

            ItemStack item = buttons[i].getItem();

            if (item == null) {
                continue;
            }

            inventory.setItem(i, item);
        }
        this.inventory = inventory;
    }

    protected MenuButton[] pane(MenuButton[] buttons) {
        return buttons;

        /*
        for (int i = 0; i < 9; i++) {
            if (buttons[i] == null) {
                buttons[i] = new IconButton(PANE);
            }

            if (buttons[i + buttons.length - 9] == null) {
                buttons[i + buttons.length - 9] = new IconButton(PANE);
            }

            if (i == 0 || i == 8) {
                for (int a = 0; a < buttons.length; a += 9) {
                    if (buttons[i + a] == null) {
                        buttons[i + a] = new IconButton(PANE);
                    }
                }
            }
        }
        return buttons;
        */
    }

    public void onClose(Player player) {
        ItemStack[] contents = this.inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            if (this.buttons[i] == null) {
                continue;
            }

            if (this.buttons[i].getItem().isSimilar(contents[i])) {
                continue;
            }

            player.setItemOnCursor(new ItemStack(Material.AIR));
            SchedulerUtils.runLater(5L, player::updateInventory);
            break;
        }

        remove(player.getUniqueId());
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isUseClose() {
        return useClose;
    }

    public void setUseClose(boolean useClose) {
        this.useClose = useClose;
    }

    public Inventory getInventory() {
        return inventory;
    }

    // Util

    protected int getLines(int elements, int perLine) {
        return (int) Math.ceil((double) elements / (double) perLine);
    }
}
