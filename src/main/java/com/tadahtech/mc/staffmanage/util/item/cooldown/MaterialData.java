package com.tadahtech.mc.staffmanage.util.item.cooldown;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

public class MaterialData {

    private static final String MATERIAL_MEMBER = "mat";
    private static final String DATA_MEMBER = "data";

    public static MaterialData fromJson(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }

        JsonObject object = element.getAsJsonObject();
        if (!object.has(MATERIAL_MEMBER) || !object.has(DATA_MEMBER)) {
            return null;
        }

        Material material = Material.getMaterial(object.get(MATERIAL_MEMBER).getAsString());
        byte data = object.get(DATA_MEMBER).getAsByte();

        return new MaterialData(material, data);
    }

    public static final MaterialData AIR = new MaterialData(Material.AIR);

    private Material material;
    private byte data;

    public MaterialData(Location location) {
        this(location.getBlock());
    }

    public MaterialData(Block block) {
        this(block.getType(), block.getData());
    }

    public MaterialData(MaterialData data) {
        this(data.getMaterial(), data.getData());
    }

    public MaterialData(Material material) {
        this(material, (byte) 0);
    }

    public MaterialData(Material material, byte data) {
        this.material = material;
        this.data = data;
    }

    public ItemStack asItem() {
        return new ItemStack(this.material, 1, this.data);
    }

    public Material getMaterial() {
        return material;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public boolean isAir() {
        return this.getMaterial() == Material.AIR;
    }

    public boolean isBlock() {
        return this.getMaterial().isBlock() && !this.isAir();
    }

    public int getCombinedID() {
        return this.material.getId() + (this.data << 12);
    }

    public void place(Location... locations) {
        for (Location location : locations) {
            this.place(location);
        }
    }

    public void place(Location location) {
        Block block = location.getBlock();
        BlockState state = block.getState();

        state.setType(this.getMaterial());
        state.setRawData(this.getData());

        state.update(true, false);
    }

    public boolean equals(Location location) {
        Block block = location.getBlock();
        return block.getType() == this.getMaterial() && block.getData() == this.getData();
    }

    public boolean isSimilar(MaterialData data) {
        return this.equals(data) || data.isDirtOrGrass() && this.isDirtOrGrass();
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty(MATERIAL_MEMBER, this.getMaterial().name());
        object.addProperty(DATA_MEMBER, this.getData());
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        MaterialData other = (MaterialData) obj;

        return getData() == other.getData() && getMaterial() == other.getMaterial();
    }

    @Override
    public int hashCode() {
        int result = getMaterial().hashCode();
        result = 31 * result + (int) getData();
        return result;
    }

    public boolean isSolid() {
        return this.getMaterial().isSolid();
    }

    public boolean isDirtOrGrass() {
        return this.getMaterial() == Material.DIRT || this.getMaterial() == Material.GRASS;
    }
}
