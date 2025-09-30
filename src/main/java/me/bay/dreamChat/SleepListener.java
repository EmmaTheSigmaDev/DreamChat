package me.bay.dreamChat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SleepListener implements Listener {

    public final DreamChat plugin;
    public final Set<Player> sleepers = new HashSet<>();
    public final BukkitAudiences adventure;

    public SleepListener(DreamChat plugin) {
        this.plugin = plugin;
        this.adventure = plugin.getAdventure();
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        if (!plugin.getConfig().getBoolean("sleep_voting.enabled", true)) return;

        Player player = event.getPlayer();
        sleepers.add(player);

        int online = Bukkit.getOnlinePlayers().size();
        int current = sleepers.size();
        double threshold = plugin.getConfig().getDouble("sleep_voting.vote_threshold", 0.5);

        String msg = Objects.requireNonNull(plugin.getConfig().getString("sleep_voting.vote_message"))
                .replace("{current}", String.valueOf(current))
                .replace("{total}", String.valueOf(online))
                .replace("{percent}", String.valueOf((int) ((double) current / online * 100)));

        Component messageComponent = Component.text(msg);
        Bukkit.getOnlinePlayers().forEach(p -> adventure.player(p).sendMessage(messageComponent));

        if ((double) current / online >= threshold && online >= plugin.getConfig().getInt("sleep_voting.min_players", 2)) {
            plugin.getDreamEffects().startDreamCinematic(sleepers);
            sleepers.clear();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        sleepers.remove(event.getPlayer());
    }
}
