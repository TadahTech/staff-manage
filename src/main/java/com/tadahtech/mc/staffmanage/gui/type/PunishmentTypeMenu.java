package com.tadahtech.mc.staffmanage.gui.type;

import com.tadahtech.mc.staffmanage.menu.Menu;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import com.tadahtech.mc.staffmanage.util.UtilUI;
import org.bukkit.entity.Player;

public class PunishmentTypeMenu extends Menu {

    private PunishmentBuilder builder;

    public PunishmentTypeMenu(PunishmentBuilder data) {
        super(data.getData().getName());
        this.builder = data;
    }

    @Override
    protected MenuButton[] setUp(Player player) {
        MenuButton[] buttons = new MenuButton[27];
        PunishmentData data = builder.getData();

        int[] slots = UtilUI.getCenterIndices(data.getSlots());

        int slot = 0;

        buttons[slots[slot++]] = new PunishmentTypeButton(PunishmentType.KICK, this.builder);

        if (data.isAllowBan()) {
            buttons[slots[slot++]] = new PunishmentTypeButton(PunishmentType.BAN, this.builder);
        }

        buttons[slots[slot++]] = new PunishmentTypeButton(PunishmentType.TEMP_BAN, this.builder);

        if (data.isAllowIPBan()) {
            buttons[slots[slot++]] = new PunishmentTypeButton(PunishmentType.IP_BAN, this.builder);
        }

        if (data.isAllowMute()) {
            buttons[slots[slot++]] = new PunishmentTypeButton(PunishmentType.MUTE, this.builder);
        }

        buttons[slots[slot++]] = new PunishmentTypeButton(PunishmentType.TEMP_MUTE, this.builder);
        buttons[slots[slot]] = new PunishmentTypeButton(PunishmentType.WARNING, this.builder);


        return buttons;
    }
}
