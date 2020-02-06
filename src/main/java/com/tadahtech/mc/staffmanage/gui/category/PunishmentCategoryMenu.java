package com.tadahtech.mc.staffmanage.gui.category;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.menu.Menu;
import com.tadahtech.mc.staffmanage.menu.buttons.MenuButton;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilder;
import com.tadahtech.mc.staffmanage.util.UtilUI;
import org.bukkit.entity.Player;

public class PunishmentCategoryMenu extends Menu {

    private final int size;
    private PunishmentCategory[] categories;
    private PunishmentBuilder builder;

    public PunishmentCategoryMenu(PunishmentBuilder builder) {
        super("Punishments");

        this.builder = builder;
        this.categories = StaffManager.getInstance().getPunishmentManager().getAll().toArray(new PunishmentCategory[0]);
        this.size = categories.length;
    }

    @Override
    protected MenuButton[] setUp(Player player) {
        int lines = (int) (2 + Math.ceil((double) this.size / 4.0));
        MenuButton[] buttons = new MenuButton[9 * lines];

        int[] slots = UtilUI.getIndicesFor(this.size, 1, 4, 0);

        for (int i = 0; i < this.size; i++) {
            buttons[slots[i]] = new PunishmentCategoryButton(this.categories[i], builder);
        }

        return buttons;
    }
}
