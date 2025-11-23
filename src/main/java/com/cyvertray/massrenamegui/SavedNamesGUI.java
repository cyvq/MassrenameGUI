package com.cyvertray.massrenamegui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SavedNamesGUI implements Listener {

    // gui layout
    private static final int GUI_SIZE = 54;
    private static final int MAX_SAVED_SLOTS = 45;
    private static final int BACK_SLOT = 49;

    private static final Pattern HEX_PATTERN = Pattern.compile("#[A-Fa-f0-9]{6}");
    private static final String GUI_TITLE = ChatColor.translateAlternateColorCodes('&', "&2Sᴀᴠᴇᴅ Nᴀᴍᴇs");

    private static final Material ENTRY_EMPTY_FILLER = Material.GRAY_STAINED_GLASS_PANE;
    private static final Material BOTTOM_FILLER = Material.PURPLE_STAINED_GLASS_PANE;
    private static final Material BACK_ITEM = Material.ARROW;

    private ItemStack cachedEmptyFiller;
    private ItemStack cachedBottomFiller;
    private ItemStack cachedBackButton;

    private static final SavedNameStorage STORAGE = Massrename.getInstance().getSavedNameStorage();
    private static final SavedNamesGUI LISTENER_INSTANCE = new SavedNamesGUI();

    private SavedNamesGUI() {
    }

    public static SavedNamesGUI getListener() {
        return LISTENER_INSTANCE;
    }

    public void open(Player player) {
        if (player == null) return;

        Inventory gui = Bukkit.createInventory(player, GUI_SIZE, GUI_TITLE);

        List<SavedNameEntry> entries = STORAGE.getEntries(player);

        for (int slot = 0; slot < MAX_SAVED_SLOTS; slot++) {
            if (slot < entries.size()) {
                SavedNameEntry entry = entries.get(slot);
                ItemStack display = entry.toDisplayItem();
                gui.setItem(slot, display);
            } else {
                gui.setItem(slot, getCachedEmptyFiller());
            
            }
        }

        // bttom row
        for (int i = GUI_SIZE - 9; i < GUI_SIZE; i++) {
            if (i == BACK_SLOT) {
                gui.setItem(i, getCachedBackButton());
            } else {
                gui.setItem(i, getCachedBottomFiller());
            }
        }

        player.openInventory(gui);
    }

    //items
    private ItemStack getCachedBackButton() {
        if (cachedBackButton != null) return cachedBackButton;
        ItemStack item = new ItemStack(BACK_ITEM);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&eBᴀᴄᴋ"));
            meta.setLore(Collections.singletonList(colorize("&6Rᴇᴛᴜʀɴ ᴛᴏ ᴛʜᴇ Mᴀɪɴ Gᴜɪ")));
            item.setItemMeta(meta);
        }
        cachedBackButton = item;
        return item;
    }

    private ItemStack getCachedEmptyFiller() {
        if (cachedEmptyFiller != null) return cachedEmptyFiller;
        ItemStack item = new ItemStack(ENTRY_EMPTY_FILLER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        cachedEmptyFiller = item;
        return item;
    }

    private ItemStack getCachedBottomFiller() {
        if (cachedBottomFiller != null) return cachedBottomFiller;
        ItemStack item = new ItemStack(BOTTOM_FILLER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        cachedBottomFiller = item;
        return item;
    }

    //handlers
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!isOurGui(event.getView())) return;

        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < GUI_SIZE) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!isOurGui(event.getView())) return;

        Player player = (Player) event.getWhoClicked();
        int raw = event.getRawSlot();

        event.setCancelled(true);

        if (raw >= GUI_SIZE) {
            return;
        }

        if (raw == BACK_SLOT) {
            Massrename.getInstance().getRenameGUI().open(player);
            return;
        }

        if (raw < 0 || raw >= MAX_SAVED_SLOTS) {
            return;
        }

        List<SavedNameEntry> entries = STORAGE.getEntries(player);
        if (raw >= entries.size()) {
            return;
        }

        boolean isShift = event.isShiftClick();
        boolean isRight = event.isRightClick();
// delete
        if (isShift && isRight) {
            boolean removed = STORAGE.removeEntry(player, raw);
            if (removed) {
                player.sendMessage(colorize("&2Sᴀᴠᴇᴅ ɴᴀᴍᴇ ʀᴇᴍᴏᴠᴇᴅ."));
            } else {
                player.sendMessage(colorize("&cFᴀɪʟᴇᴅ ᴛᴏ ʀᴇᴍᴏᴠᴇ sᴀᴠᴇᴅ ɴᴀᴍᴇ."));
            }
            this.open(player);
            return;
        }

        // load template
        if (!isShift) {
            SavedNameEntry selected = entries.get(raw);
            if (selected != null) {
                Massrename.getInstance().getRenameGUI().loadSavedTemplate(player, selected.getName(), selected.getLore());
                Massrename.getInstance().getRenameGUI().open(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
    }

    private static boolean isOurGui(org.bukkit.inventory.InventoryView view) {
        if (view == null) return false;
        String title = view.getTitle();
        return title.equals(GUI_TITLE) && view.getTopInventory().getSize() == GUI_SIZE;
    }

    private static String colorize(String input) {
        if (input == null) return "";
        String s = ChatColor.translateAlternateColorCodes('&', input);
        Matcher m = HEX_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, ChatColor.of(m.group()).toString());
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
