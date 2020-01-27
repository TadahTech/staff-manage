package com.tadahtech.mc.staffmanage.util.item;

import org.bukkit.enchantments.Enchantment;

/**
 * @author Timothy Andis (TadahTech) on 4/26/2016.
 */
public class WrappedEnchantment {

    private int level;
    private String enchantment;
    private boolean override;

    public WrappedEnchantment(Enchantment enchantment, int level, boolean override) {
        this.level = level;
        this.enchantment = enchantment.getName();
        this.override = override;
    }

    public WrappedEnchantment(Enchantment enchantment) {
        this(enchantment, 1, true);
    }

    public WrappedEnchantment(Enchantment enchantment, int level) {
        this(enchantment, level, true);
    }

    public int getLevel() {
        return level;
    }

    public Enchantment getEnchantment() {
        return Enchantment.getByName(this.enchantment);
    }

    public boolean isOverride() {
        return override;
    }
}
