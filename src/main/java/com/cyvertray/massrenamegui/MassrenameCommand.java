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
    //reload config perm
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("massrename.reload")) {
                plugin.reloadPluginConfig();
                sender.sendMessage(ChatColor.GREEN + "MᴀssʀᴇɴᴀᴍᴇGUI ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ ʀᴇʟᴏᴀᴅᴇᴅ.");
            } else {
                sender.sendMessage(ChatColor.RED + "Yᴏᴜ ᴅᴏ ɴᴏᴛ ʜᴀᴠᴇ ᴘᴇʀᴍɪssɪᴏɴ ᴛᴏ ʀᴇʟᴏᴀᴅ ᴛʜᴇ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ.");
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Oɴʟʏ ᴘʟᴀʏᴇʀs ᴄᴀɴ ᴜsᴇ ᴛʜᴇ GUI.");
            return true;
        }

        //massrename perms
        if (args.length == 0) {
            if (!player.hasPermission("massrename.use")) {
                player.sendMessage(ChatColor.RED + "Yᴏᴜ ᴅᴏ ɴᴏᴛ ʜᴀᴠᴇ ᴘᴇʀᴍɪssɪᴏɴ ᴛᴏ ᴜsᴇ MᴀssʀᴇɴᴀᴍᴇGUI.");
                return true;
            }
            plugin.getRenameGUI().open(player);
            return true;
        }

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
