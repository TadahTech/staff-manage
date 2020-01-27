package com.tadahtech.mc.staffmanage.util.item.cooldown;

import org.bukkit.entity.Player;

/**
 * A holder for an item cooldown. This doesn't have to be a
 * {@link Player player}, it can be a group for example if
 * you intend to have a group only be able to use an item
 * within a certain timespace.
 * <p>
 * Usually this will only be a player. You'll need a manager
 * that loads this on join, and removes it on quit.
 */
public interface ItemCooldownHolder {

    /**
     * Called when a holder uses an item
     *
     * @param data The data of the item
     */
    void use(MaterialData data);

    /**
     * Checks when an item was last used
     *
     * @param data The data of the item
     * @return The last time it was used
     * @see System#currentTimeMillis()
     */
    long getLastUse(MaterialData data);
}
