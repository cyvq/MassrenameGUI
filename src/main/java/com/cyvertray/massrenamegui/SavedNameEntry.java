package com.cyvertray.massrenamegui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class SavedNameEntry {

    private String name;
    private List<String> lore;

    public SavedNameEntry(String name, List<String> lore) {
        this.name = name;
        this.lore = lore != null ? new ArrayList<>(lore) : null;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public boolean hasLore() {
        return lore != null && !lore.isEmpty();
    }

    public ItemStack toDisplayItem() {
        ItemStack tag = new ItemStack(org.bukkit.Material.NAME_TAG);
        ItemMeta meta = tag.getItemMeta();
        if (meta == null) return tag;

        meta.setDisplayName(colorize(name));

        // footer
        List<String> displayLore = new ArrayList<>();
        if (hasLore()) {
            for (String line : lore) displayLore.add(colorize(line));
        }
        displayLore.add(""); 
        displayLore.add(colorize("&2Cʟɪᴄᴋ ᴛᴏ ʟᴏᴀᴅ Tᴇᴍᴘʟᴀᴛᴇ"));
        displayLore.add(colorize("&cSʜɪꜰᴛ+Rɪɢʜᴛ-Cʟɪᴄᴋ ᴛᴏ ᴅᴇʟᴇᴛᴇ"));

        meta.setLore(displayLore);
        tag.setItemMeta(meta);
        return tag;
    }

// dumy creation
    public ItemStack toDummyItem() {
        ItemStack tag = new ItemStack(org.bukkit.Material.NAME_TAG);
        ItemMeta meta = tag.getItemMeta();
        if (meta == null) return tag;

        meta.setDisplayName(colorize(name));
        if (hasLore()) {
            List<String> colorizedLore = new ArrayList<>();
            for (String line : lore) colorizedLore.add(colorize(line));
            meta.setLore(colorizedLore);
        }

        tag.setItemMeta(meta);
        return tag;
    }

    private String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    @Override
    public String toString() {
        return "SavedNameEntry{name=" + name + ", lore=" + lore + "}";
    }
}
