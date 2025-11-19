package com.cyvertray.massrenamegui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MassrenameCommand implements CommandExecutor, TabCompleter {

    private final Massrename plugin;

    public MassrenameCommand(Massrename plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Oɴʟʏ ᴘʟᴀʏᴇʀs ᴄᴀɴ ᴜsᴇ ᴛʜɪs ᴄᴏᴍᴍᴀɴᴅ.");
            return true;
        }

        // /massrename reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("massrename.reload")) {
                player.sendMessage("§cYᴏᴜ ᴅᴏ ɴᴏᴛ ʜᴀᴠᴇ ᴘᴇʀᴍɪssɪᴏɴ ᴛᴏ ʀᴇʟᴏᴀᴅ ᴛʜᴇ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ.");
                return true;
            }

            plugin.reloadPluginConfig();
            player.sendMessage("§2MᴀssʀᴇɴᴀᴍᴇGUI ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ ʀᴇʟᴏᴀᴅᴇᴅ.");
            return true;
        }

        // /massrename open GUI perms
        if (!player.hasPermission("massrename.use")) {
            player.sendMessage("§cYᴏᴜ ᴅᴏ ɴᴏᴛ ʜᴀᴠᴇ ᴘᴇʀᴍɪssɪᴏɴ ᴛᴏ ᴜsᴇ MᴀssʀᴇɴᴀᴍᴇGUI.");
            return true;
        }

        plugin.getRenameGUI().open(player);


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("massrename.reload")) {
                completions.add("reload");
            }
            return completions;
        }
        return Collections.emptyList();
    }
}
