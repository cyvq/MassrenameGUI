package com.cyvertray.massrenamegui;

import com.cyvertray.massrenamegui.Massrename;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SavedNameStorage {

    private static final int MAX_ENTRIES = 45;
    private static SavedNameStorage instance;

    private final Massrename plugin;
    private final File playerDataFolder;
//folder creation and naming of save files
    private SavedNameStorage(Massrename plugin) {
        this.plugin = plugin;
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    public static synchronized SavedNameStorage getInstance() {
        if (instance == null) {
            instance = new SavedNameStorage(Massrename.getInstance());
        }
        return instance;
    }

    private File getPlayerFile(Player player) {
        String fileName = player.getName() + "-" + player.getUniqueId().toString() + ".yml";
        return new File(playerDataFolder, fileName);
    }

    private YamlConfiguration loadPlayerConfig(Player player) {
        File playerFile = getPlayerFile(player);
        if (!playerFile.exists()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(playerFile);
    }

    private YamlConfiguration ensurePlayerConfig(Player player) {
        File playerFile = getPlayerFile(player);
        if (!playerFile.exists()) {
            try {
                if (!playerDataFolder.exists()) playerDataFolder.mkdirs();
                playerFile.createNewFile();

                YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
                config.options().header("List of saved names for player " + player.getName() +
                        " (" + player.getUniqueId() + ")");
                config.options().copyHeader(true);
                config.set("saved", new ArrayList<>());
                config.save(playerFile);
                return config;

            } catch (IOException e) {
                plugin.getLogger().severe("Could not create saved names file for " + player.getName());
                e.printStackTrace();
                return null;
            }
        } else {
            return YamlConfiguration.loadConfiguration(playerFile);
        }
    }


    public List<SavedNameEntry> getEntries(Player player) {
        YamlConfiguration config = loadPlayerConfig(player);
        if (config == null) return new ArrayList<>();

        List<Map<?, ?>> rawSavedList = config.getMapList("saved");
        List<SavedNameEntry> savedEntries = new ArrayList<>();

        for (Map<?, ?> entryMap : rawSavedList) {
            Object nameObj = entryMap.get("name");
            if (nameObj == null) continue;

            String displayName = String.valueOf(nameObj);
            List<String> loreLines = null; 

            Object loreObj = entryMap.get("lore");
            if (loreObj instanceof List) {
                loreLines = new ArrayList<>();
                for (Object line : (List<?>) loreObj) {
                    loreLines.add(String.valueOf(line));
                }
            }

            savedEntries.add(new SavedNameEntry(displayName, loreLines));
        }

        return savedEntries;
    }

    public synchronized boolean saveEntry(Player player, String name, List<String> lore) {
        String normalizedName = normalizeHexForms(name);
        List<String> normalizedLore = null;

        if (lore != null) {
            normalizedLore = new ArrayList<>();
            for (String line : lore) {
                normalizedLore.add(normalizeHexForms(line));
            }
        }

        YamlConfiguration config = ensurePlayerConfig(player);
        if (config == null) return false;

        List<Map<String, Object>> savedEntriesList = new ArrayList<>();
        List<Map<?, ?>> existingEntries = config.getMapList("saved");

        for (Map<?, ?> entry : existingEntries) {
            Map<String, Object> copyEntry = new HashMap<>();
            for (Map.Entry<?, ?> e : entry.entrySet()) {
                copyEntry.put(String.valueOf(e.getKey()), e.getValue());
            }
            savedEntriesList.add(copyEntry);
        }

        if (savedEntriesList.size() >= MAX_ENTRIES) {
            return false;
        }

        Map<String, Object> newEntryMap = new LinkedHashMap<>();
        newEntryMap.put("name", normalizedName);
        if (normalizedLore != null) newEntryMap.put("lore", normalizedLore);

        savedEntriesList.add(newEntryMap);
        config.set("saved", savedEntriesList);

        try {
            config.save(getPlayerFile(player));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save saved names for " + player.getName());
            e.printStackTrace();
        }

        return true;
    }

    public synchronized boolean removeEntry(Player player, int index) {
        YamlConfiguration config = loadPlayerConfig(player);
        if (config == null) return false;

        List<Map<?, ?>> savedList = config.getMapList("saved");
        if (index < 0 || index >= savedList.size()) return false;

        savedList.remove(index);
        config.set("saved", savedList);

        try {
            config.save(getPlayerFile(player));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to remove saved name for " + player.getName());
            e.printStackTrace();
        }

        return true;
    }


    public boolean isFull(Player player) {
        List<SavedNameEntry> entries = getEntries(player);
        return entries.size() >= MAX_ENTRIES;
    }

//color support
    private String normalizeHexForms(String input) {
        if (input == null) return null;
        return input.replaceAll("&\\{#([A-Fa-f0-9]{6})\\}", "#$1");
    }
}
