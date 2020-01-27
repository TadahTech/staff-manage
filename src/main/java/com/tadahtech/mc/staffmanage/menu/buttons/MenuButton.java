package com.tadahtech.mc.staffmanage.menu.buttons;
import com.tadahtech.mc.staffmanage.util.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 *
 */
public abstract class MenuButton {

    protected ItemStack itemStack;

    public MenuButton(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    protected static String itemize(String label, String value) {
        return Colors.GRAY + label + ": " + Colors.ELEMENT + value;
    }

    protected static String itemize(String label, boolean value) {
        return Colors.GRAY + label + ": " + (value ? (Colors.GREEN + "Yes") : (Colors.RED + "No"));
    }

    protected static String itemize(String label, int value) {
        return Colors.GRAY + label + ": " + Colors.ELEMENT + value;
    }

    protected static String lore(String str) {
        return Colors.LORE + str;
    }

    /**
     * Called when clicking on a specific item is needed, rather than just the slot
     * Empty by default
     *
     * @param player    The player who clicked
     * @param clickType Tge type of click
     * @param slot      The slot clicked
     */
    public abstract void onClick(Player player, ClickType clickType, int slot);

    public void onClick(Player player, int slot) {
        onClick(player, ClickType.RIGHT, slot);
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public void setItem(ItemStack item) {
        this.itemStack = item;
    }

    /**
     * Refreshes the ItemStack
     */
    public void update() {
    }
}
