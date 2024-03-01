package net.mrliam2614.mrliamapi.spigot.gui.items;

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

public class ItemBuilder {
    private ItemStack itemStack;

    public ItemBuilder() {
    }

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
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            skullMeta = (SkullMeta) dataInput.readObject();
            dataInput.close();
            inputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getLogger().severe("Impossibile decodificare la testa del giocatore: " + e.getMessage());
        }
        itemBuilder.itemStack.setItemMeta(skullMeta);
        return itemBuilder;
    }

    public static ItemBuilder getFrom(ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
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
