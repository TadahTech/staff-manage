package com.tadahtech.mc.staffmanage.gui.subCat;

import com.tadahtech.mc.staffmanage.gui.category.PunishmentCategoryMenu;
import com.tadahtech.mc.staffmanage.menu.Menu;
import com.tadahtech.mc.staffmanage.menu.buttons.BackButton;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import com.tadahtech.mc.staffmanage.util.UtilUI;
import org.bukkit.entity.Player;

public class PunishmentSubTypeMenu extends Menu {

    private PunishmentCategoryMenu menu;
    private final int size;
    private PunishmentData[] punishmentData;
    private PunishmentBuilder builder;

    public PunishmentSubTypeMenu(PunishmentCategoryMenu menu, PunishmentCategory category, PunishmentBuilder builder) {
        super(category.getName());
        this.menu = menu;
        this.builder = builder;
        this.punishmentData = category.getPunishments().values().toArray(new PunishmentData[0]);
        this.size = this.punishmentData.length;
    }

    @Override
    protected MenuButton[] setUp(Player player) {
        int lines = (int) (2 + Math.ceil((double) this.size / 4.0));
        MenuButton[] buttons = new MenuButton[Math.min((9 * lines), 54)];

        buttons[0] = new BackButton(player, menu);

        int[] slots = UtilUI.getIndicesFor(this.size, 1, 4, 0);

        for (int i = 0; i < this.size; i++) {
            buttons[slots[i]] = new PunishmentSubCatButton(this.punishmentData[i], builder);
        }

        return buttons;
    }
}
