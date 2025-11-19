package com.cyvertray.massrenamegui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameGUI implements Listener {

    private final Map<Player, Inventory> inventories = new HashMap<>();
    private final Map<Player, ItemStack> inputItems = new HashMap<>();
    private final Map<Player, Boolean> loreToggle = new HashMap<>();
    private final Massrename plugin = Massrename.getInstance();

    private int size;
    private int inputSlot;
    private int acceptSlot;

    // lore togle 
    private int loreToggleSlot;

    public void open(Player player) {
        size = plugin.getConfig().getInt("gui-size", 54);
        if (size < 18) size = 18;
        if (size > 54) size = 54;

        Inventory gui = Bukkit.createInventory(player, size, ChatColor.DARK_GREEN + "Mᴀss Rᴇɴᴀᴍᴇ");
        inventories.put(player, gui);

        inputSlot = size - 9;
        acceptSlot = size - 1;

        // lore button placement
        loreToggleSlot = inputSlot + 4;

        loreToggle.putIfAbsent(player, false); // default off

        for (int i = size - 9; i < size; i++) {
            if (i == acceptSlot) {
                gui.setItem(i, createAcceptButton());
            }
            //slot for toggle button
            else if (i == loreToggleSlot && canShowLoreToggle(player)) {
                gui.setItem(i, createLoreToggleItem(loreToggle.get(player)));
            }
            else if (i != inputSlot) {
                gui.setItem(i, createFillerPane());
            }
        }

        if (!inputItems.containsKey(player)) {
            gui.setItem(inputSlot, createInputGhost());
        } else {
            gui.setItem(inputSlot, inputItems.get(player));
        }

        player.openInventory(gui);
    }

    // lore button config and perms
    private boolean canShowLoreToggle(Player p) {
        return plugin.getConfig().getBoolean("lore-copy-toggle-enabled", true)
                && p.hasPermission("massrename.lore");
    }

    // lore toggle button
    private ItemStack createLoreToggleItem(boolean enabled) {
        Material mat = enabled ? Material.GREEN_CONCRETE : Material.RED_CONCRETE;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(colorize("&eCᴏᴘʏ Lᴏʀᴇ"));
        meta.setLore(Arrays.asList(
                enabled ? colorize("&2Eɴᴀʙʟᴇᴅ") : colorize("&cDɪsᴀʙʟᴇᴅ"),
                colorize("&6Pʀᴇss ᴛʜɪs ʙᴜᴛᴛᴏɴ ᴛᴏ ᴛᴏɢɢʟᴇ"),
                colorize("&6Lᴏʀᴇ Dᴜᴘʟɪᴄᴀᴛɪᴏɴ")
        ));

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createInputGhost() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&e&lIɴᴘᴜᴛ Nᴀᴍᴇ"));
            meta.setLore(Arrays.asList(
                colorize("&6Dʀᴀɢ & ᴅʀᴏᴘ ᴀ ɴᴀᴍᴇᴅ ɪᴛᴇᴍ ʜᴇʀᴇ"),
                colorize("&6ᴛᴏ sᴇᴛ ᴛʜᴇ ɴᴀᴍᴇ sᴏᴜʀᴄᴇ")
            ));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        }
        return item;
    }

    private ItemStack createAcceptButton() {
        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&eRᴇɴᴀᴍᴇ Aʟʟ"));
            meta.setLore(Collections.singletonList(colorize("&6Cʟɪᴄᴋ ᴛᴏ ʀᴇɴᴀᴍᴇ ᴀʟʟ ɪᴛᴇᴍs")));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        }
        return item;
    }

    private ItemStack createFillerPane() {
        ItemStack item = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory gui = inventories.get(player);
        if (gui == null) return;

        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot >= size - 9 && rawSlot != inputSlot) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory gui = inventories.get(player);
        if (gui == null) return;

        int raw = event.getRawSlot();

        if (raw >= gui.getSize()) {
            return;
        }

        // lore toggle button
        if (raw == loreToggleSlot && canShowLoreToggle(player)) {
            event.setCancelled(true);

            boolean newState = !loreToggle.get(player);
            loreToggle.put(player, newState);

            gui.setItem(loreToggleSlot, createLoreToggleItem(newState));
            player.sendMessage(colorize("&eLᴏʀᴇ ᴄᴏᴘʏ ɪs ɴᴏᴡ: " + (newState ? "&2Eɴᴀʙʟᴇᴅ" : "&cDɪsᴀʙʟᴇᴅ")));
            return;
        }

        // accept button  
        if (raw == acceptSlot) {
            event.setCancelled(true);
            ItemStack source = inputItems.get(player);
            if (source == null || !source.hasItemMeta() || !source.getItemMeta().hasDisplayName()) {
                player.sendMessage(colorize("&cYᴏᴜ ᴍᴜsᴛ ꜰɪʀsᴛ ᴘʟᴀᴄᴇ ᴀ ɴᴀᴍᴇᴅ ɪᴛᴇᴍ ɪɴ ᴛʜᴇ ɪɴᴘᴜᴛ sʟᴏᴛ."));
                return;
            }

            String newName = colorize(source.getItemMeta().getDisplayName());

            boolean copyLore = loreToggle.get(player); 

            List<String> loreToCopy = null;
            if (copyLore && source.getItemMeta().hasLore()) {
                loreToCopy = new ArrayList<>(source.getItemMeta().getLore());
            }

            for (int i = 0; i < size - 9; i++) {
                ItemStack it = gui.getItem(i);
                if (it != null && it.getType() != Material.AIR) {
                    ItemMeta m = it.getItemMeta();
                    if (m != null) {
                        m.setDisplayName(newName);

                        // lore copying
                        if (copyLore) {
                            m.setLore(loreToCopy);
                        }

                        it.setItemMeta(m);
                    }
                }
            }

            player.sendMessage(colorize("&2Iᴛᴇᴍs ʀᴇɴᴀᴍᴇᴅ!"));
            return;
        }

        if (raw >= size - 9 && raw != inputSlot) {
            event.setCancelled(true);
            return;
        }

        if (raw == inputSlot) {
            ItemStack clicked = event.getCursor();
            if (clicked != null && clicked.getType() != Material.AIR) {
                inputItems.put(player, clicked.clone());
                gui.setItem(inputSlot, clicked.clone());
                event.setCursor(null);
                event.setCancelled(true);
            } else {
                ItemStack existing = inputItems.get(player);
                if (existing != null) {
                    event.setCursor(existing);
                    inputItems.remove(player);
                    gui.setItem(inputSlot, createInputGhost());
                }
                event.setCancelled(true);
            }
            return;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory gui = inventories.get(player);
        if (gui == null) return;

        for (int i = 0; i < size - 9; i++) {
            ItemStack it = gui.getItem(i);
            if (it != null && it.getType() != Material.AIR) {
                Map<Integer, ItemStack> left = player.getInventory().addItem(it);
                left.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            }
        }

        ItemStack input = inputItems.get(player);
        if (input != null) {
            Map<Integer, ItemStack> left = player.getInventory().addItem(input);
            left.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            inputItems.remove(player);
        }

        inventories.remove(player);

        // toggle state clean on close
        loreToggle.remove(player);
    }

    private String colorize(String input) {
        String s = ChatColor.translateAlternateColorCodes('&', input);
        Pattern hex = Pattern.compile("#[A-Fa-f0-9]{6}");
        Matcher m = hex.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, ChatColor.of(m.group()).toString());
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
