package net.mrliam2614.mrliamapi.spigot.gui.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.mrliam2614.mrliamapi.spigot.messages.Logger;
import net.mrliam2614.mrliamapi.utils.MrliamColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

public class ItemBuilder {
    private ItemStack itemStack;

    private ItemBuilder() {}

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static ItemBuilder create() {
        return new ItemBuilder();
    }

    public static ItemBuilder createHead(OfflinePlayer player) {
        ItemBuilder itemBuilder = new ItemBuilder(new ItemStack(Material.PLAYER_HEAD));
        SkullMeta skullMeta = (SkullMeta) itemBuilder.itemStack.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(player);
        itemBuilder.itemStack.setItemMeta(skullMeta);
        return itemBuilder;
    }

    public static ItemBuilder createHead(String base64) {
        ItemBuilder itemBuilder = new ItemBuilder(new ItemStack(Material.PLAYER_HEAD));
        SkullMeta skullMeta = (SkullMeta) itemBuilder.itemStack.getItemMeta();
        try {
            Method setProfile = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            setProfile.setAccessible(true);

            GameProfile profile = new GameProfile(UUID.randomUUID(), "skull-texture");
            profile.getProperties().put("textures", new Property("textures", base64));

            setProfile.invoke(skullMeta, profile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Logger.error("There was a severe internal reflection error when attempting to set the skin of a player skull via base64!");
            e.printStackTrace();
        }
        itemBuilder.itemStack.setItemMeta(skullMeta);
        return itemBuilder;
    }

    public static ItemBuilder getFrom(ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
    }
    public static ItemBuilder createEmpty(){
        ItemBuilder item = new ItemBuilder();
        item.setItem(Material.BARRIER);
        item.setDisplayName("");
        item.setLore("");

        return item;
    }

    public ItemBuilder setItem(Material material) {
        this.itemStack = new ItemStack(material);
        return this;
    }


    public ItemBuilder setItemAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setMaterialData(MaterialData materialData) {
        this.itemStack.setData(materialData);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(MrliamColor.colorize(displayName));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(java.util.Arrays.asList(colorize(lore)));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

    private String[] colorize(String... strings) {
        for (int i = 0; i < strings.length; i++) {
            strings[i] = MrliamColor.colorize(strings[i]);
        }
        return strings;
    }
}
