package io.github.portlek.ping;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ping extends JavaPlugin implements Listener {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.config = getConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void ping(AsyncPlayerChatEvent event) {
        if (config == null || event.isCancelled()) return;

        final String pingItem = config.getString("ping-item");

        if (pingItem == null || !event.getMessage().contains(pingItem)) return;

        final String[] split = event.getMessage().split(" ");

    }

}
