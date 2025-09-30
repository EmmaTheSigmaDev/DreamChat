package me.bay.dreamChat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EmojiManager {

    private final DreamChat plugin;
    private File emojiFile;
    private FileConfiguration emojiConfig;
    private Map<String, String> emojis = new HashMap<>();

    public EmojiManager(DreamChat plugin) {
        this.plugin = plugin;
        load();
    }

    /** Load emojis.yml from plugin folder (or create it if missing) */
    private void load() {
        emojiFile = new File(plugin.getDataFolder(), "emojis.yml");

        if (!emojiFile.exists()) {
            plugin.saveResource("emojis.yml", false);
        }

        emojiConfig = YamlConfiguration.loadConfiguration(emojiFile);

        emojis.clear();

        if (emojiConfig.isConfigurationSection("emojis")) {
            for (String key : emojiConfig.getConfigurationSection("emojis").getKeys(false)) {
                String value = emojiConfig.getString("emojis." + key, "");
                if (value != null && !value.isEmpty()) {
                    emojis.put(key, value);
                }
            }
            plugin.getLogger().info("Loaded " + emojis.size() + " emojis from emojis.yml");
        } else {
            plugin.getLogger().warning("No 'emojis' section found in emojis.yml!");
            emojis = Collections.emptyMap();
        }
    }

    /** Reload emojis.yml */
    public void reload() {
        load();
    }

    /** Replace all emoji shortcuts in a chat message */
    public String replaceEmojis(String message) {
        if (message == null || message.isEmpty()) return message;

        for (Map.Entry<String, String> entry : emojis.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

    /** Returns unmodifiable view of emojis */
    public Map<String, String> getEmojis() {
        return Collections.unmodifiableMap(emojis);
    }
}
