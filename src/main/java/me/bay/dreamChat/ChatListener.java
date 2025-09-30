package me.bay.dreamChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    public final DreamChat plugin;
    public final BukkitAudiences adventure;

    public ChatListener(DreamChat plugin) {
        this.plugin = plugin;
        this.adventure = plugin.getAdventure();
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("emoji_chat.enabled", true)) return;

        String msg = plugin.getEmojiManager().replaceEmojis(event.getMessage());
        event.setCancelled(true);

        Component componentMsg = Component.text("<" + event.getPlayer().getName() + "> " + msg);
        for (var p : event.getRecipients()) {
            adventure.player(p).sendMessage(componentMsg);
        }
    }
}