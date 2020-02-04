package com.tadahtech.mc.staffmanage.gui.subCat;

import com.tadahtech.mc.staffmanage.menu.Menu;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.util.UtilUI;
import org.bukkit.entity.Player;

public class PunishmentSubTypeMenu extends Menu {

    private final int size;
    private PunishmentData[] punishmentData;

    public PunishmentSubTypeMenu(PunishmentCategory category) {
        super(category.getName());
        this.punishmentData = category.getPunishments().toArray(new PunishmentData[0]);
        this.size = this.punishmentData.length;
    }

    @Override
    protected MenuButton[] setUp(Player player) {
        int lines = (int) (2 + Math.ceil((double) this.size / 4.0));
        MenuButton[] buttons = new MenuButton[9 * lines];

        int[] slots = UtilUI.getIndicesFor(this.size, 1, 4, 0);

        for (int i = 0; i < this.size; i++) {
            buttons[slots[i]] = new PunishmentSubCatButton(this.punishmentData[i]);
        }

        return buttons;
    }
}
