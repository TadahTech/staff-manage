package com.tadahtech.mc.staffmanage.util.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class SplashPotion {

    private PotionType type;
    private int amplifier;
    private int duration;

    public SplashPotion(PotionType type, int amplifier, int duration) {
        this.type = type;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    public SplashPotion(PotionType type) {
        this(type, 1, 20);
    }

    public SplashPotion(PotionType type, int amplifier) {
        this(type, amplifier, 20);
    }

    public ItemStack getItem(int amount) {
        Potion potion = new Potion(this.type);
        potion.setSplash(true);
        potion.setLevel(this.amplifier);
        return potion.toItemStack(amount);
    }

    public ItemStack getItem() {
        return getItem(1);
    }
}
