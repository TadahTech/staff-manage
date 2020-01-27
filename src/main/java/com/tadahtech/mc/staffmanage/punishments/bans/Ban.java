package com.tadahtech.mc.staffmanage.punishments.bans;

import com.tadahtech.mc.staffmanage.punishments.Punishment;
import org.bukkit.entity.Player;

/**
 * Interface for bans, since these require a formatted message
 */
public interface Ban extends Punishment {

    /**
     * Creates a formatted message showing information about the specific players
     * ban. This message is used as the kick message for when the player attempts
     * to connect to the server, and fails, or when the ban is applied and the
     * player is kicked if online.
     *
     * @param player The {@link javafx.print.PageLayout}
     * @return The formatted message
     */
    String formatMessage(String player);

    @Override
    default boolean shouldPersist() {
        return true;
    }

    @Override
    default void onApply(Player player) {
        player.kickPlayer(this.formatMessage(player.getName()));
    }
}
