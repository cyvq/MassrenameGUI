package com.cyvertray.massrenamegui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RenameGUI implements Listener {
// uuid instead opf palyername
    private final Map<UUID, ItemStack> inputItems = new HashMap<>();
    private final Map<UUID, Boolean> inputIsDummy = new HashMap<>();
    private final Map<UUID, Boolean> loreToggle = new HashMap<>();
    private static final Pattern HEX_PATTERN = Pattern.compile("#[A-Fa-f0-9]{6}");
//cache items
    private ItemStack cachedFiller;
    private ItemStack cachedAccept;
    private ItemStack cachedInputGhost;
    private ItemStack cachedSaveName;

    private final Massrename plugin = Massrename.getInstance();

    private static final int DEFAULT_SIZE = 54;
    private static final int MIN_SIZE = 18;
    private static final int MAX_SIZE = 54;

    private static final String GUI_TITLE = ChatColor.DARK_GREEN + "Mᴀss Rᴇɴᴀᴍᴇ";

    public void open(Player player) {
        int size = plugin.getConfig().getInt("gui-size", DEFAULT_SIZE);
        if (size < MIN_SIZE) size = MIN_SIZE;
        if (size > MAX_SIZE) size = MAX_SIZE;
        if (size % 9 != 0) size = (size / 9) * 9;

        Inventory gui = Bukkit.createInventory(player, size, GUI_TITLE);

        int inputSlot = size - 9;
        int acceptSlot = size - 1;
        int loreToggleSlot = inputSlot + 4;
        int saveNameSlot = acceptSlot - 1;

        UUID uuid = player.getUniqueId();
        loreToggle.putIfAbsent(uuid, false);
        inputIsDummy.putIfAbsent(uuid, false);

        // bottom row
        for (int i = size - 9; i < size; i++) {
            if (i == acceptSlot) gui.setItem(i, createAcceptButton());
            else if (i == saveNameSlot) gui.setItem(i, createSaveNameButton());
            else if (i == loreToggleSlot) {
                if (plugin.getConfig().getBoolean("lore-copy-enabled", true) && player.hasPermission("massrename.lore")) {
                    loreToggle.putIfAbsent(uuid, false);
                    gui.setItem(i, createLoreToggleItem(loreToggle.get(uuid)));
                } else {
                    loreToggle.remove(uuid);
                    gui.setItem(i, createFillerPane());
                }
            }
            else if (i != inputSlot) gui.setItem(i, createFillerPane());
        }

        // input slot stored or ghost
        ItemStack stored = inputItems.get(uuid);
        if (stored != null) gui.setItem(inputSlot, stored.clone());
        else gui.setItem(inputSlot, createInputGhost());

        player.openInventory(gui);
    }

    private boolean canShowLoreToggle(Player p) {
        return plugin.getConfig().getBoolean("lore-copy-enabled", true)
                && p.hasPermission("massrename.lore");
    }
// create items
    private ItemStack createLoreToggleItem(boolean enabled) {
        Material mat = enabled ? Material.GREEN_CONCRETE : Material.RED_CONCRETE;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&eCᴏᴘʏ Lᴏʀᴇ"));
            meta.setLore(Arrays.asList(
                    enabled ? colorize("&2Eɴᴀʙʟᴇᴅ") : colorize("&cDɪsᴀʙʟᴇᴅ"),
                    colorize("&6Pʀᴇss ᴛʜɪs ʙᴜᴛᴛᴏɴ ᴛᴏ ᴛᴏɢɢʟᴇ"),
                    colorize("&6Lᴏʀᴇ Dᴜᴘʟɪᴄᴀᴛɪᴏɴ")
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createSaveNameButton() {
        if (cachedSaveName != null) return cachedSaveName.clone();

        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&eSᴀᴠᴇ Nᴀᴍᴇ"));
            meta.setLore(Arrays.asList(
                    colorize("&3Rɪɢʜᴛ-ᴄʟɪᴄᴋ&6 ᴛᴏ sᴀᴠᴇ ᴛʜᴇ ᴄᴜʀʀᴇɴᴛ ɪɴᴘᴜᴛ ɴᴀᴍᴇ"),
                    colorize("&6(ᴀʟsᴏ sᴀᴠᴇs ʟᴏʀᴇ ᴡʜᴇɴ ᴇɴᴀʙʟᴇᴅ)"),
                    "",
                    colorize("&bLᴇꜰᴛ-ᴄʟɪᴄᴋ&6 ᴛᴏ ᴏᴘᴇɴ sᴀᴠᴇᴅ ɴᴀᴍᴇs")
            ));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        }
        cachedSaveName = item;
        return item;
    }

    private ItemStack createInputGhost() {
        if (cachedInputGhost != null) return cachedInputGhost.clone();

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
        cachedInputGhost = item;
        return item;
    }

    private ItemStack createAcceptButton() {
        if (cachedAccept != null) return cachedAccept.clone();

        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&eRᴇɴᴀᴍᴇ Aʟʟ"));
            meta.setLore(Collections.singletonList(colorize("&6Cʟɪᴄᴋ ᴛᴏ ʀᴇɴᴀᴍᴇ ᴀʟʟ ɪᴛᴇᴍs")));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        }
        cachedAccept = item;
        return item;
    }

    private ItemStack createFillerPane() {
        if (cachedFiller != null) return cachedFiller.clone();

        ItemStack item = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        cachedFiller = item;
        return item;
    }
// button events
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title == null || !title.equals(GUI_TITLE)) return;
        Inventory top = event.getView().getTopInventory();
        if (!isOurGui(title, top.getSize())) return;

        int size = top.getSize();
        int inputSlot = size - 9;

        for (int raw : event.getRawSlots()) {
            if (raw >= size - 9 && raw != inputSlot) {
                event.setCancelled(true);
                return;
            }
            if (raw == inputSlot) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title == null || !title.equals(GUI_TITLE)) return;
        Inventory top = event.getView().getTopInventory();
        if (!isOurGui(title, top.getSize())) return;

        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        int size = top.getSize();
        int inputSlot = size - 9;
        int acceptSlot = size - 1;
        int loreToggleSlot = inputSlot + 4;
        int saveNameSlot = acceptSlot - 1;
        int raw = event.getRawSlot();
        ClickType click = event.getClick();
        InventoryAction action = event.getAction();
        int hotbarButton = event.getHotbarButton(); 

        if (raw >= size) return;

        if (raw >= size - 9) {
            if (raw != inputSlot) {
                event.setCancelled(true);

                // lore toggle button
                if (raw == loreToggleSlot && canShowLoreToggle(player)) {
                    boolean current = loreToggle.getOrDefault(uuid, false);
                    boolean next = !current;
                    loreToggle.put(uuid, next);
                    top.setItem(loreToggleSlot, createLoreToggleItem(next));
                    player.sendMessage(colorize("&eLᴏʀᴇ ᴄᴏᴘʏ ɪs ɴᴏᴡ: " + (next ? "&2Eɴᴀʙʟᴇᴅ" : "&cDɪsᴀʙʟᴇᴅ")));
                }

                // save name
                else if (raw == saveNameSlot) {
                    if (click == ClickType.LEFT) {
                        SavedNamesGUI.getListener().open(player);
                    } else if (click == ClickType.RIGHT) {
                        ItemStack stored = inputItems.get(uuid);
                        if (stored == null || !stored.hasItemMeta() || !stored.getItemMeta().hasDisplayName()) {
                            player.sendMessage(colorize("&cNᴏ ɴᴀᴍᴇᴅ ɪɴᴘᴜᴛ ᴛᴏ sᴀᴠᴇ."));
                        } else {
                            boolean copyLore = loreToggle.getOrDefault(uuid, false);
                            List<String> lore = (copyLore && stored.getItemMeta().hasLore())
                                    ? new ArrayList<>(stored.getItemMeta().getLore())
                                    : null;
                            boolean saved = Massrename.getInstance().getSavedNameStorage().saveEntry(player, stored.getItemMeta().getDisplayName(), lore);
                            if (!saved) player.sendMessage(colorize("&cYᴏᴜʀ sᴀᴠᴇᴅ ɴᴀᴍᴇ ʟɪsᴛ ɪs ꜰᴜʟʟ."));
                            else player.sendMessage(colorize("&2Sᴀᴠᴇᴅ!"));
                        }
                    }
                }

                // accept
                else if (raw == acceptSlot && click.isLeftClick()) {
                    ItemStack stored = inputItems.get(uuid);
                    if (stored == null || !stored.hasItemMeta() || !stored.getItemMeta().hasDisplayName()) {
                        player.sendMessage(colorize("&cYᴏᴜ ᴍᴜsᴛ ꜰɪʀsᴛ ᴘʟᴀᴄᴇ ᴀ ɴᴀᴍᴇᴅ ɪᴛᴇᴍ ɪɴ ᴛʜᴇ ɪɴᴘᴜᴛ sʟᴏᴛ."));
                    } else {
                        String newName = colorize(stored.getItemMeta().getDisplayName());
                        boolean copyLore = loreToggle.getOrDefault(uuid, false);
                        List<String> loreToCopy = (copyLore && stored.getItemMeta().hasLore())
                                ? new ArrayList<>(stored.getItemMeta().getLore()) : null;

                        for (int i = 0; i < size - 9; i++) {
                            ItemStack it = top.getItem(i);
                            if (it != null && it.getType() != Material.AIR) {
                                ItemMeta m = it.getItemMeta();
                                if (m != null) {
                                    m.setDisplayName(newName);
                                    if (copyLore) m.setLore(loreToCopy);
                                    it.setItemMeta(m);
                                }
                            }
                        }
                        player.sendMessage(colorize("&2Iᴛᴇᴍs ʀᴇɴᴀᴍᴇᴅ!"));
                    }
                }

                return; 
            }

        }


        if (raw == inputSlot) {
            if (event.isShiftClick() || action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                return;
            }

            if (hotbarButton >= 0) {
                event.setCancelled(true);
                return;
            }

            if (click == ClickType.DROP || click == ClickType.CONTROL_DROP) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);

            ItemStack cursor = event.getCursor();
            ItemStack stored = inputItems.get(uuid);
            boolean wasDummy = inputIsDummy.getOrDefault(uuid, false);
// dummy handling and legit item return
            if (stored != null && wasDummy) {
                if (cursor != null && cursor.getType() != Material.AIR) {
                    ItemStack toStore = cursor.clone();
                    inputItems.put(uuid, toStore);
                    inputIsDummy.put(uuid, false);
                    top.setItem(inputSlot, toStore.clone());
                    event.setCursor(null);
                    return;
                }
                return;
            }

            if (cursor != null && cursor.getType() != Material.AIR) {
                if (stored != null) {
                    event.setCursor(stored.clone());
                } else {
                    event.setCursor(null);
                }
                ItemStack toStore = cursor.clone();
                inputItems.put(uuid, toStore);
                inputIsDummy.put(uuid, false);
                top.setItem(inputSlot, toStore.clone());
                return;
            }

            if (stored != null) {
                event.setCursor(stored.clone());
                inputItems.remove(uuid);
                inputIsDummy.remove(uuid);
                top.setItem(inputSlot, createInputGhost());
                return;
            }

            return;
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title == null || !title.equals(GUI_TITLE)) return;
        Inventory top = event.getView().getTopInventory();
        if (top.getSize() < MIN_SIZE) return;

        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        int size = top.getSize();

        for (int i = 0; i < size - 9; i++) {
            ItemStack it = top.getItem(i);
            if (it != null && it.getType() != Material.AIR) {
                Map<Integer, ItemStack> leftover = player.getInventory().addItem(it);
                if (!leftover.isEmpty()) leftover.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            }
        }

        ItemStack input = inputItems.get(uuid);
        boolean isDummy = inputIsDummy.getOrDefault(uuid, false);
        if (input != null && !isDummy) {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(input);
            if (!leftover.isEmpty()) leftover.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            inputItems.remove(uuid);
        } else {
            inputItems.remove(uuid);
            inputIsDummy.remove(uuid);
        }

        // cleanup
        loreToggle.remove(uuid);
    }

    public void loadSavedTemplate(Player player, String name, List<String> lore) {
        UUID uuid = player.getUniqueId();
        ItemStack dummy = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = dummy.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null && !lore.isEmpty()) meta.setLore(lore);
            dummy.setItemMeta(meta);
        }

        if (player.getOpenInventory() != null) {
            String viewTitle = player.getOpenInventory().getTitle();
            Inventory viewTop = player.getOpenInventory().getTopInventory();
            if (viewTitle != null && viewTitle.equals(GUI_TITLE) && viewTop.getSize() % 9 == 0) {
                int size = viewTop.getSize();
                int inputSlot = size - 9;
                inputItems.put(uuid, dummy);
                inputIsDummy.put(uuid, true);
                viewTop.setItem(inputSlot, dummy);
                return;
            }
        }

        inputItems.put(uuid, dummy);
        inputIsDummy.put(uuid, true);
    }

    private boolean isOurGui(String title, int size) {
        if (title == null) return false;
        if (!title.equals(GUI_TITLE)) return false;
        return size >= MIN_SIZE && size <= MAX_SIZE && (size % 9 == 0);
    }
// color support
    private String colorize(String input) {
        if (input == null) return "";
        String s = ChatColor.translateAlternateColorCodes('&', input);
        Matcher m = HEX_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) m.appendReplacement(sb, ChatColor.of(m.group()).toString());
        m.appendTail(sb);
        return sb.toString();
    }
}
