package com.tadahtech.mc.staffmanage.gui.type;

import com.tadahtech.mc.staffmanage.gui.type.buttons.BanButton;
import com.tadahtech.mc.staffmanage.gui.type.buttons.IPBanButton;
import com.tadahtech.mc.staffmanage.gui.type.buttons.KickButton;
import com.tadahtech.mc.staffmanage.gui.type.buttons.MuteButton;
import com.tadahtech.mc.staffmanage.gui.type.buttons.TempBanButton;
import com.tadahtech.mc.staffmanage.gui.type.buttons.TempMuteButton;
import com.tadahtech.mc.staffmanage.gui.type.buttons.WarnButton;
import com.tadahtech.mc.staffmanage.menu.Menu;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.util.UtilUI;
import org.bukkit.entity.Player;

public class PunishmentTypeMenu extends Menu {

    private PunishmentData data;

    public PunishmentTypeMenu(PunishmentData data) {
        super(data.getName());
        this.data = data;
    }

    @Override
    protected MenuButton[] setUp(Player player) {
        MenuButton[] buttons = new MenuButton[27];

        int[] slots = UtilUI.getCenterIndices(data.getSlots());

        int slot = 0;

        buttons[slots[slot++]] = new KickButton(this.data);

        if (data.isAllowBan()) {
            buttons[slots[slot++]] = new BanButton(this.data);
        }

        buttons[slots[slot++]] = new TempBanButton(this.data);

        if (data.isAllowIPBan()) {
            buttons[slots[slot++]] = new IPBanButton(this.data);
        }

        if (data.isAllowMute()) {
            buttons[slots[slot++]] = new MuteButton(this.data);
        }

        buttons[slots[slot++]] = new TempMuteButton(this.data);
        buttons[slots[slot]] = new WarnButton(this.data);


        return buttons;
    }
}
