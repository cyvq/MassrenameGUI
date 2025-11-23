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

        getLogger().info("MᴀssʀᴇɴᴀᴍᴇGUI ᴠ1.1 ᴇɴᴀʙʟᴇᴅ.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MᴀssʀᴇɴᴀᴍᴇGUI ᴠ1.1 ᴅɪsᴀʙʟᴇᴅ.");
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
            getLogger().severe("Cᴏᴍᴍᴀɴᴅ 'massrename' ɪs ᴍɪssɪɴɢ ꜰʀᴏᴍ ᴘʟᴜɢɪɴ.ʏᴍʟ!");
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
        getLogger().info("&2ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ ʀᴇʟᴏᴀᴅᴇᴅ.");
    }
}
