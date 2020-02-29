package com.tadahtech.mc.staffmanage.gui.subCat;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.length.LengthManager;
import com.tadahtech.mc.staffmanage.length.PlayerLengthData;
import com.tadahtech.mc.staffmanage.length.PunishmentLength;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PunishmentSubCatButton extends MenuButton {

    private PunishmentData data;
    private PunishmentBuilder builder;

    public PunishmentSubCatButton(PunishmentData data, PunishmentBuilder builder) {
        super(null);
        this.data = data;
        this.builder = builder;

        LengthManager lengthManager = StaffManager.getInstance().getPunishmentManager().getLengthManager();
        PlayerLengthData lengthData = lengthManager.getData(builder.getPlayerUUID(), data.getName());

        if (lengthData == null) {
            builder.setCurrentLength(null);
            setItem(data.toItemStack(null, null));
            return;
        }

        PunishmentLength length = lengthManager.getLength(data, lengthData);

        this.itemStack = data.toItemStack(length, lengthData.getLastType());
    }

    @Override
    public void onClick(Player player, ClickType clickType, int slot) {
        builder.setData(this.data);

        builder.punish();
        player.closeInventory();
    }
}
