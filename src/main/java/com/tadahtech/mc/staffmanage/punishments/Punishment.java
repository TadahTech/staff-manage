package com.tadahtech.mc.staffmanage.punishments;

import com.tadahtech.mc.staffmanage.StaffManager;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

/**
 * A negative sanction of some kind which may be called to punish a player
 * for a violation of the server rules.
 */
public interface Punishment {

    /**
     * @return The type of punishment
     */
    PunishmentType getType();

    /**
     * @return If this punishment is temporary, in other words has an expiration
     * date.
     */
    boolean isTemporary();

    /**
     * @return When the punishment expires. Null if permanent/no expiry.
     */
    Date getExpiry();

    /**
     * @return Punishment is expired
     * @throws UnsupportedOperationException If this punishment is not temporary
     */
    default boolean isExpired() {
        if (!this.isTemporary()) {
            throw new UnsupportedOperationException("This punishment isn't temporary!");
        }

        return System.currentTimeMillis() > this.getExpiry().getTime();
    }

    /**
     * @return The reason for this punishment
     */
    String getReason();

    /**
     * @return The account name of the person who initiated this punishment
     */
    String getInitiator();

    /**
     * @return The account UUID of the person who initiated this punishment
     */
    UUID getInitiatorUUID();

    /**
     * @return The time when this punishment was initiated
     */
    Date getTimestamp();

    /**
     * Called when the punishment is applied to the player
     *
     * @param player The {@link Player}
     */
    default void onApply(Player player) {
    }

    /**
     * Called when the punishment is removed from the player
     *
     * @param player The {@link Player}
     */
    default void onRemove(Player player) {
    }

    default String getBaseMessage(String player) {
        String base = StaffManager.getInstance().getMessagesSection().getString(getType().name().toLowerCase() + ".message");

        base = base.replace("%player%", player).replace("%reason%", getReason());
        return base;
    }

    /**
     * @return If the punishment should be saved or not
     */
    boolean shouldPersist();
}
