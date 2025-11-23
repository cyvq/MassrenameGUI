package com.cyvertray.massrenamegui;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

public class Massrename extends JavaPlugin {

    private static Massrename instance;

    private RenameGUI renameGUI;
    private SavedNameStorage savedNameStorage;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.savedNameStorage = new SavedNameStorage(this);

        // gui listener
        renameGUI = new RenameGUI();
        getServer().getPluginManager().registerEvents(renameGUI, this);

        getServer().getPluginManager().registerEvents(SavedNamesGUI.getListener(), this);

        registerCommands();

        getLogger().info("MassrenameGUI v1.2.1 enabled.");
    }

    @Override
    public void onDisable() {
        if (this.savedNameStorage != null) {
            this.savedNameStorage.saveData();
        }
        getLogger().info("MassrenameGUI v1.2.1 disabled.");
        instance = null;
    }
//comand register
    private void registerCommands() {
        PluginCommand cmd = getCommand("massrename");
        if (cmd != null) {
            MassrenameCommand massRenameCmd = new MassrenameCommand(this);
            cmd.setExecutor(massRenameCmd);
            cmd.setTabCompleter(massRenameCmd);
        } else {
            getLogger().severe("Command /massrename is missing from plugin.yml!");
        }
    }

    public static Massrename getInstance() {
        return instance;
    }

    public RenameGUI getRenameGUI() {
        return renameGUI;
    }

    public SavedNameStorage getSavedNameStorage() {
        return savedNameStorage;
    }

    //Config reload
    public void reloadPluginConfig() {
        reloadConfig();
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&2Configuration reloaded."));
    }
}
