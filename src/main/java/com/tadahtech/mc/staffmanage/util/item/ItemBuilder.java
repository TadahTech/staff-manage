package com.tadahtech.mc.staffmanage.util.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilMath;
import com.tadahtech.mc.staffmanage.util.UtilText;
import com.tadahtech.mc.staffmanage.util.UtilTime;
import com.tadahtech.mc.staffmanage.util.item.cooldown.MaterialData;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class ItemBuilder {

    private static final String LINE_SEPARATOR = "\n";

    private Map<Enchantment, Integer> enchants = Maps.newHashMap();
    private int amount;
    private short data;
    private List<String> lore = Lists.newArrayList();
    private Material mat;
    private String title;
    private boolean glow;
    private String skullOwner;
    private String skullCustomTexture;
    private boolean unbreakable;
    private Color color;
    private List<PotionEffect> potionEffects;

    @Deprecated
    public ItemBuilder() {
        // We have this for GSON support.
    }

    public ItemBuilder(ItemBuilder builder) {
        this.enchants = Maps.newHashMap(builder.enchants);
        this.amount = builder.amount;
        this.data = builder.data;
        this.lore = Lists.newArrayList(builder.lore);
        this.mat = builder.mat;
        this.title = builder.title;
        this.glow = builder.glow;
        this.skullOwner = builder.skullOwner;
        this.skullCustomTexture = builder.skullCustomTexture;
        this.unbreakable = builder.unbreakable;
        this.color = builder.color;
    }

    public ItemBuilder(ItemStack stack) {
        this.mat = stack.getType();
        this.amount = stack.getAmount();
        this.enchants.putAll(stack.getEnchantments());
        this.data = stack.getDurability();
        this.potionEffects = Lists.newArrayList();

        if (!stack.hasItemMeta()) {
            return;
        }

        this.title = stack.getItemMeta().getDisplayName();

        if (stack.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) stack.getItemMeta();
            this.potionEffects = Lists.newArrayList(meta.getCustomEffects());
        }

        if (stack.getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            this.skullCustomTexture = SkullTexture.getTexture(meta);
            if (this.skullCustomTexture == null) {
                this.skullOwner = meta.getOwner();
            }
        }

        if (stack.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
            this.color = meta.getColor();
        }

        if (!stack.getItemMeta().hasLore()) {
            return;
        }

        this.lore = stack.getItemMeta().getLore();
    }

    public ItemBuilder(Material mat) {
        this(mat, 1, (short) 0);
    }

    public ItemBuilder(MaterialData data) {
        this(data.getMaterial(), 1, data.getData());
    }

    public ItemBuilder(Material mat, int amount, short data) {
        this.mat = mat;
        this.amount = amount;
        this.data = data;
        this.potionEffects = Lists.newArrayList();
    }

    public ItemBuilder setType(Material mat) {
        this.mat = mat;
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        if (lore.length == 0) {
            this.lore = null;
            return this;
        }

        this.lore.clear();
        List<String> loreList = Lists.newArrayList();
        toLore(loreList, lore);

        this.lore.addAll(loreList);
        return this;
    }

    private void toLore(List<String> loreList, String[] lore) {
        for (String loreLine : lore) {
            String[] lines = loreLine.split(LINE_SEPARATOR);
            if (lines.length == 1) {
                loreList.add(lines[0]);
                continue;
            }

            String formatting = ChatColor.getLastColors(lines[0]);
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) {
                    loreList.add(lines[i]);
                    continue;
                }

                loreList.add(formatting + lines[i]);
            }
        }
    }

    public ItemBuilder apply(Consumer<ItemBuilder> builderConsumer) {
        builderConsumer.accept(this);
        return this;
    }

    public ItemBuilder addLoreLines(String... strings) {
        return this.addLoreLines(Lists.newArrayList(strings));
    }

    public ItemBuilder addColoredLoreLines(String color, String... strings) {
        return this.addLoreLines(color, Lists.newArrayList(strings));
    }

    public ItemBuilder addLoreLines(String color, List<String> list) {
        if (list == null) {
            return this;
        }

        List<String> loreList = Lists.newArrayList();
        for (String loreLine : list) {
            String[] lines = loreLine.split(LINE_SEPARATOR);
            if (lines.length == 1) {
                loreList.add(color == null ? lines[0] : color + lines[0]);
                continue;
            }

            String formatting = color == null ? ChatColor.getLastColors(lines[0]) : color;
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) {
                    loreList.add(color == null ? lines[i] : color + lines[i]);
                    continue;
                }

                loreList.add(formatting + lines[i]);
            }
        }

        this.lore.addAll(loreList);
        return this;
    }

    public ItemBuilder addLoreLines(List<String> list) {
        return this.addLoreLines(null, list);
    }

    public ItemBuilder addTopLore(List<String> list) {
        if (list == null) {
            return this;
        }

        if (this.lore == null) {
            this.lore = list;
            return this;
        }

        list.addAll(this.lore);
        this.lore = list;
        return this;
    }

    public ItemBuilder addPotionEffect(PotionEffect effect) {
        this.potionEffects.add(effect);

        if (this.title == null) {
            this.title = ChatColor.GOLD + MinecraftPotion.getByEffect(effect.getType()).getName();
        }

        String loreEntry = Colors.GRAY + UtilText.format(effect.getType().getName())
          + " " + UtilMath.toRoman(effect.getAmplifier()) + Colors.GRAY + " (" + (effect.getDuration() < 20 ? Colors.GOLD
          + "Instant" : Colors.GRAY + Colors.LIGHT_PURPLE + UtilTime.toTimerSecond(effect.getDuration() * 50)) + Colors.GRAY + ")";

        this.addTopLore(Lists.newArrayList(loreEntry));
        return this;
    }

    public ItemBuilder setWoolColor(DyeColor color) {
        return this.setData(color.getWoolData());
    }

    public ItemBuilder setPaneColor(DyeColor color) {
        return this.setData(color.getWoolData());
    }

    public ItemBuilder setClayColor(DyeColor color) {
        return this.setData(color.getWoolData());
    }

    public ItemBuilder setNMSName(String prefix) {
        if (this.title != null) {
            return this.setTitle(prefix + this.title);
        }

        ItemInfo info = ItemNames.itemByType(this.mat, this.data);

        if (info == null) {
            return this.setTitle(prefix + getNMSName(new ItemStack(this.mat, this.amount, this.data)));
        }

        return this.setTitle(prefix + info.getName());
    }

    public MaterialData asMaterialData() {
        return new MaterialData(this.mat, (byte) this.data);
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(this.mat, this.amount, this.data);
        if (this.skullCustomTexture != null && this.mat == Material.SKULL_ITEM) {
            item = SkullTexture.createSkull(this.skullCustomTexture);
            item.setAmount(this.amount);
        }

        ItemMeta meta = item.getItemMeta();

        if(meta == null) {
            throw new IllegalArgumentException("Null meta!");
        }

        if (meta instanceof PotionMeta && this.potionEffects != null && !this.potionEffects.isEmpty()) {
            PotionMeta potionMeta = (PotionMeta) meta;
            for (PotionEffect effect : this.potionEffects) {
                potionMeta.addCustomEffect(effect, false);
            }
        }

        if (this.title != null) {
            meta.setDisplayName(this.title);
        }

        if (this.lore != null && !this.lore.isEmpty()) {
            meta.setLore(this.lore);
        }

        if (this.skullOwner != null && meta instanceof SkullMeta) {
            ((SkullMeta) meta).setOwner(this.skullOwner);
        }

        if (color != null && meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(this.color);
        }

        if (meta instanceof EnchantmentStorageMeta) {
            for (Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
                ((EnchantmentStorageMeta) meta).addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }

            this.enchants.clear();
        }

        meta.spigot().setUnbreakable(this.unbreakable);
        item.setItemMeta(meta);
        item.addUnsafeEnchantments(this.enchants);
        return item;
    }

    public Material getType() {
        return this.mat;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        return this.addEnchant(new WrappedEnchantment(enchantment, level));
    }

    public ItemBuilder addEnchant(WrappedEnchantment enchantment) {
        this.enchants.put(enchantment.getEnchantment(), enchantment.getLevel());
        return this;
    }

    public ItemBuilder addEnchants(List<WrappedEnchantment> enchantments) {
        enchantments.forEach(this::addEnchant);
        return this;
    }

    public ItemBuilder addLore(String string) {
        this.addLoreLines(Lists.newArrayList(string));
        return this;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    public ItemBuilder setEnchants(List<WrappedEnchantment> enchantments) {
        for (WrappedEnchantment enchantment : enchantments) {
            this.enchants.put(enchantment.getEnchantment(), enchantment.getLevel());
        }

        return this;
    }

    public int getAmount() {
        return amount;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public short getData() {
        return data;
    }

    public ItemBuilder setData(int newData) {
        return this.setData((short) newData);
    }

    public ItemBuilder setData(short newData) {
        this.data = newData;
        return this;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (lore == null) {
            return this;
        }

        List<String> loreList = Lists.newArrayList();
        toLore(loreList, lore.toArray(new String[0]));

        this.lore = loreList;
        return this;
    }

    public Material getMat() {
        return mat;
    }

    public String getTitle() {
        return title;
    }

    public ItemBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean isGlow() {
        return glow;
    }

    public ItemBuilder setGlow(boolean glow) {
        this.glow = glow;
        return this;
    }

    public String getSkullOwner() {
        return skullOwner;
    }

    public ItemBuilder setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    public String getSkullCustomTexture() {
        return skullCustomTexture;
    }

    public ItemBuilder setSkullCustomTexture(String skullCustomTexture) {
        this.skullCustomTexture = skullCustomTexture;
        return this;
    }

    public ItemBuilder setEntityType(EntityType entityType) {
        return this.setData(entityType.getTypeId());
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public ItemBuilder setUnbreakable(boolean value) {
        this.unbreakable = value;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public ItemBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public ItemBuilder setPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ItemBuilder)) return false;

        ItemBuilder builder = (ItemBuilder) o;

        return new EqualsBuilder()
          .append(amount, builder.amount)
          .append(data, builder.data)
          .append(glow, builder.glow)
          .append(unbreakable, builder.unbreakable)
          .append(enchants, builder.enchants)
          .append(lore, builder.lore)
          .append(mat, builder.mat)
          .append(title, builder.title)
          .append(skullOwner, builder.skullOwner)
          .append(skullCustomTexture, builder.skullCustomTexture)
          .append(color, builder.color)
          .append(potionEffects, builder.potionEffects)
          .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
          .append(enchants)
          .append(amount)
          .append(data)
          .append(lore)
          .append(mat)
          .append(title)
          .append(glow)
          .append(skullOwner)
          .append(skullCustomTexture)
          .append(unbreakable)
          .append(color)
          .append(potionEffects)
          .toHashCode();
    }

    @Override
    public String toString() {
        return "ItemBuilder{" +
          "enchants=" + enchants +
          ", amount=" + amount +
          ", data=" + data +
          ", lore=" + lore +
          ", mat=" + mat +
          ", title='" + title + '\'' +
          ", glow=" + glow +
          ", skullOwner='" + skullOwner + '\'' +
          ", skullCustomTexture='" + skullCustomTexture + '\'' +
          ", unbreakable=" + unbreakable +
          ", color=" + color +
          ", potionEffects=" + potionEffects +
          '}';
    }

    public String getNMSName(ItemStack itemStack) {
        if (itemStack == null) {
            return "Air";
        }

        net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
        return stack.getName();
    }
}