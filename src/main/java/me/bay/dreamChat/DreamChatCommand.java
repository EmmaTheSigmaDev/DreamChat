package me.bay.dreamChat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DreamChatCommand implements CommandExecutor {

    private final DreamChat plugin;

    public DreamChatCommand(DreamChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "Usage: /dreamchat <reload|info>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("dreamchat.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don’t have permission to do that!");
                    return true;
                }

                plugin.reloadConfig(); // reload config.yml
                plugin.getEmojiManager().reload(); // reload emojis.yml
                sender.sendMessage(ChatColor.GREEN + "DreamChat configs reloaded successfully!");
                return true;

            case "info":
                if (!sender.hasPermission("dreamchat.info")) {
                    sender.sendMessage(ChatColor.RED + "You don’t have permission to do that!");
                    return true;
                }

                sender.sendMessage(ChatColor.GOLD + "DreamChat v" + plugin.getDescription().getVersion());
                sender.sendMessage(ChatColor.YELLOW + "Author(s): " + String.join(", ", plugin.getDescription().getAuthors()));
                sender.sendMessage(ChatColor.AQUA + "Description: " + plugin.getDescription().getDescription());
                sender.sendMessage(ChatColor.GRAY + "Website: " + plugin.getDescription().getWebsite());
                return true;

            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /dreamchat <reload|info>");
                return true;
        }
    }
}