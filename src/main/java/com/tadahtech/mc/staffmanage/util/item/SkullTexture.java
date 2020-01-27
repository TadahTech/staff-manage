package com.tadahtech.mc.staffmanage.util.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;

/**
 * A utility class for creating custom skull textures. These are skins that
 * are not required to be currently in use as a skin.
 * <p>
 * To create new ones, simply take a random Minecraft account, change skin
 * and go to this site: "http://www.slicedlime.tv/custom-player-heads/"
 * You will have to decode the Base64 property value that is given in the
 * command. Any online Base64 encoder/decoder should do the job. You should
 * find a URL starting with "http://textures.minecraft.net/texture/". Take
 * what comes after this, and that is what you use here.
 */
public class SkullTexture {

    /*
     * Various textures we may use, you know, somewhere. Add some docs if the
     * use etc. isn't super obvious.
     */

    /**
     * A more yellowish Lucky Block texture. We can use this as an icon when
     * we are not referring to any specific {@link }, e. g.
     */
    public static final String WOODEN_QUESTION_MARK = "5163dafac1d91a8c91db576caac784336791a6e18d8f7f62778fc47bf146b6";

    /**
     * Creates a new skull using the given texture link. See docs for
     * {@link SkullTexture} for further info.
     *
     * @param url The URL suffix for the custom texture
     * @return A skull {@link ItemStack} with the custom texture
     */
    public static ItemStack createSkull(String url) {
        // This could be done using Bukkit#getUnsafe() but I feel that this is cleaner
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (url == null || url.isEmpty()) {
            return skull;
        }

        url = "http://textures.minecraft.net/texture/" + url;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        assert profileField != null;
        profileField.setAccessible(true);

        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

    /**
     * Extracts the texture link used to create this particular skull.
     *
     * @param meta The meta to extract from
     * @return The texture link, {@code null} if none is found. This is
     * what you would use as the argument to create a new skull with the
     * same texture in the {@link #createSkull(String)} method.
     */
    public static String getTexture(SkullMeta meta) {
        Field profileField = null;

        try {
            profileField = meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        assert profileField != null;
        profileField.setAccessible(true);

        try {
            GameProfile profile = (GameProfile) profileField.get(meta);
            if (profile == null) {
                return null;
            }

            Collection<Property> propertyCollection = profile.getProperties().get("textures");
            Property texture = propertyCollection.stream()
              .filter(property -> property.getName().equalsIgnoreCase("textures"))
              .findFirst().orElse(null);

            if (texture == null) {
                return null;
            }

            String textureString = new String(Base64.decodeBase64(texture.getValue().getBytes()));
            textureString = textureString.substring(60, textureString.length() - 4);
            return textureString;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}