package me.bay.dreamChat;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class DreamChat extends JavaPlugin {

    public BukkitAudiences adventure;
    public EmojiManager emojiManager;
    public DreamEffects dreamEffects;

    public static DreamChat instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("emojis.yml", false);

        adventure = BukkitAudiences.create(this);
        emojiManager = new EmojiManager(this);
        dreamEffects = new DreamEffects(this);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new SleepListener(this), this);
    }

    @Override
    public void onDisable() {
        if (adventure != null) adventure.close();
        if (dreamEffects != null) dreamEffects.close();
    }

    public BukkitAudiences getAdventure() {
        return adventure;
    }

    public EmojiManager getEmojiManager() {
        return emojiManager;
    }

    public DreamEffects getDreamEffects() {
        return dreamEffects;
    }
}