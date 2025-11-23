package com.cyvertray.massrenamegui;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Massrename extends JavaPlugin {

    private static Massrename instance;

    private RenameGUI renameGUI;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        // gui listener
        renameGUI = new RenameGUI();
        getServer().getPluginManager().registerEvents(renameGUI, this);

        getServer().getPluginManager().registerEvents(SavedNamesGUI.getListener(), this);

        registerCommands();

        getLogger().info("MassrenameGUI v1.2 enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MassrenameGUI v1.2 disabled.");
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

    //Config reload
    public void reloadPluginConfig() {
        reloadConfig();
        getLogger().info("&2Configuration reloaded.");
    }
}
