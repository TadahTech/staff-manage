package com.tadahtech.mc.staffmanage.util.item;

import org.bukkit.potion.PotionEffectType;

/**
 *
 */
public enum MinecraftPotion {

    REGENERATION("Potion of Regeneration", PotionEffectType.REGENERATION),
    SWIFTNESS("Potion of Swiftness", PotionEffectType.SPEED),
    FIRE_RESISTANCE("Potion of Fire Resistance", PotionEffectType.FIRE_RESISTANCE),
    POISON("Potion of Poison", PotionEffectType.POISON),
    HEALING("Potion of Healing", PotionEffectType.HEAL),
    NIGHT_VISION("Potion of Night Vision", PotionEffectType.NIGHT_VISION),
    WEAKNESS("Potion of Weakness", PotionEffectType.WEAKNESS),
    STRENGTH("Potion of Strength", PotionEffectType.INCREASE_DAMAGE),
    SLOWNESS("Potion of Slowness", PotionEffectType.SLOW),
    LEAPING("Potion of Leaping", PotionEffectType.JUMP),
    HARMING("Potion of Harming", PotionEffectType.HARM),
    WATER_BREATHING("Potion of Water Breathing", PotionEffectType.WATER_BREATHING),
    INVISIBILITY("Potion of Invisibility", PotionEffectType.INVISIBILITY),
    DAMAGE_RESISTANCE("Potion of Damage Resistance", PotionEffectType.DAMAGE_RESISTANCE),
    SATURATION("Potion of Saturation", PotionEffectType.SATURATION),
    FAST_DIGGING("Potion of Mining Strength", PotionEffectType.FAST_DIGGING),
    SLOW_DIGGING("Potion of Mining Fatigue", PotionEffectType.SLOW_DIGGING),
    ABSORPTION("Potion of Absorption", PotionEffectType.ABSORPTION),
    INCREASED_HEALTH("Potion of Increased Health", PotionEffectType.HEALTH_BOOST),
    CONFUSION("Potion of Confusion", PotionEffectType.CONFUSION),
    BLINDNESS("Potion of Blindness", PotionEffectType.BLINDNESS),
    HUNGER("Potion of Hunger", PotionEffectType.HUNGER),
    WITHER("Potion of Wither", PotionEffectType.WITHER);

    private String minecraftName;
    private PotionEffectType effectType;

    MinecraftPotion(String minecraftName, PotionEffectType effectType) {
        this.minecraftName = minecraftName;
        this.effectType = effectType;
    }

    public String getName() {
        return minecraftName;
    }

    public PotionEffectType getEffect() {
        return effectType;
    }

    public static MinecraftPotion getByName(String name) {
        try {
            return MinecraftPotion.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static MinecraftPotion getByEffect(PotionEffectType effectType) {
        for (MinecraftPotion potion : MinecraftPotion.values()) {
            if (potion.getEffect().equals(effectType)) {
                return potion;
            }
        }

        return null;
    }
}
