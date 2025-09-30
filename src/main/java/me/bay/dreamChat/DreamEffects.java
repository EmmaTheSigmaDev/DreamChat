package me.bay.dreamChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DreamEffects {

    private final DreamChat plugin;
    private final BukkitAudiences adventure;

    private final Map<String, Sound> defaultSounds = Map.of(
            "ambient.cave", Sound.AMBIENT_CAVE,
            "player.levelup", Sound.ENTITY_PLAYER_LEVELUP,
            "ender_dragon.ambient", Sound.ENTITY_ENDER_DRAGON_AMBIENT
    );

    public DreamEffects(DreamChat plugin) {
        this.plugin = plugin;
        this.adventure = plugin.getAdventure();
    }

    private Sound getSound(String name) {
        return defaultSounds.getOrDefault(name.toLowerCase(), null);
    }

    public void startDreamCinematic(Set<Player> sleepers) {
        if (!plugin.getConfig().getBoolean("dream_cinematic.enabled", true) || sleepers.isEmpty()) return;

        int speed = plugin.getConfig().getInt("dream_cinematic.speed", 5);
        boolean globalParticles = plugin.getConfig().getBoolean("dream_cinematic.global_particle.enabled", true);
        boolean globalSounds = plugin.getConfig().getBoolean("dream_cinematic.global_sound.enabled", true);

        boolean messagesEnabled = plugin.getConfig().getBoolean("dream_messages.enabled", true);
        boolean randomOrder = plugin.getConfig().getBoolean("dream_messages.random_order", true);
        String displayType = plugin.getConfig().getString("dream_messages.display", "action_bar");
        List<?> messages = plugin.getConfig().getList("dream_messages.messages");

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;

                for (Player p : sleepers) {
                    // Fast-forward night
                    p.getWorld().setTime((ticks * 2400L / speed) % 24000);

                    // Global particles
                    if (globalParticles) {
                        Particle particle = Particle.SMOKE;
                        p.getWorld().spawnParticle(particle, p.getLocation().add(0, 1.5, 0), 5, 0.3, 0.5, 0.3, 0.0);
                    }

                    // Global sound
                    if (globalSounds) {
                        Sound sound = getSound("ambient.cave");
                        if (sound != null) p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
                    }

                    // Per-message effects
                    if (messagesEnabled && messages != null && !messages.isEmpty()) {
                        Map<?, ?> messageEntry;
                        if (randomOrder) {
                            int idx = (int) (Math.random() * messages.size());
                            messageEntry = (Map<?, ?>) messages.get(idx);
                        } else {
                            messageEntry = (Map<?, ?>) messages.get(ticks % messages.size());
                        }

                        // Check if message enabled
                        if (!(messageEntry.get("enabled") instanceof Boolean enabled) || !enabled) continue;

                        // Message text
                        String msg = messageEntry.get("message") instanceof String m ? m : "";
                        if (msg.isEmpty()) continue;

                        // Per-message particles
                        if (messageEntry.get("particle") instanceof Map<?, ?> particleMap) {
                            if (particleMap.get("enabled") instanceof Boolean partEnabled && partEnabled) {
                                String type = particleMap.get("type") instanceof String t ? t : "SMOKE";
                                Particle particle;
                                try {
                                    particle = Particle.valueOf(type.toUpperCase());
                                } catch (IllegalArgumentException e) {
                                    particle = Particle.SMOKE;
                                }
                                int count = particleMap.get("count") instanceof Number c ? c.intValue() : 5;
                                double ox = particleMap.get("offset_x") instanceof Number oxn ? oxn.doubleValue() : 0.3;
                                double oy = particleMap.get("offset_y") instanceof Number oyn ? oyn.doubleValue() : 0.5;
                                double oz = particleMap.get("offset_z") instanceof Number ozn ? ozn.doubleValue() : 0.3;
                                double speedVal = particleMap.get("speed") instanceof Number spd ? spd.doubleValue() : 0.0;

                                p.getWorld().spawnParticle(particle, p.getLocation().add(0, 1.5, 0), count, ox, oy, oz, speedVal);
                            }
                        }

                        // Per-message sounds
                        if (messageEntry.get("sound") instanceof Map<?, ?> soundMapEntry) {
                            if (soundMapEntry.get("enabled") instanceof Boolean sndEnabled && sndEnabled) {
                                String sName = soundMapEntry.get("effect") instanceof String sn ? sn : "ambient.cave";
                                float vol = soundMapEntry.get("volume") instanceof Number v ? v.floatValue() : 1.0f;
                                float pitch = soundMapEntry.get("pitch") instanceof Number pch ? pch.floatValue() : 1.0f;

                                Sound snd = getSound(sName);
                                if (snd != null) p.playSound(p.getLocation(), snd, vol, pitch);
                            }
                        }

                        // Send message
                        Component componentMsg = Component.text(msg);
                        if ("chat".equalsIgnoreCase(displayType)) {
                            adventure.player(p).sendMessage(componentMsg);
                        } else {
                            adventure.player(p).sendActionBar(componentMsg);
                        }
                    }
                }

                if (ticks >= speed) cancel();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void close() {
        adventure.close();
    }
}
