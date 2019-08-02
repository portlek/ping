package io.github.portlek.ping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ping extends JavaPlugin implements Listener {

    private FileConfiguration config;

    private boolean messageEnable;
    private boolean soundEnable;
    private boolean titleEnable;
    private boolean actionBarEnable;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        messageEnable = config.getBoolean("ping-message.enable");
        soundEnable = config.getBoolean("ping-sound.enable");
        titleEnable = config.getBoolean("ping-title.enable");
        actionBarEnable = config.getBoolean("ping-actionbar.enable");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void ping(AsyncPlayerChatEvent event) {
        if (config == null || event.isCancelled()) return;

        final String pingItem = config.getString("ping-item");

        if (pingItem == null || !event.getMessage().contains(pingItem)) return;

        final String[] split = event.getMessage().split(" ");

        final StringBuilder builder = new StringBuilder();

        for (String messageSplit : split) {

            if (!messageSplit.contains(pingItem)) {
                builder.append(messageSplit).append(" ");
                continue;
            }

            final String messagePiece = messageSplit.replaceAll(pingItem, "");
            final Player target = Bukkit.getPlayer(messagePiece);

            if (target == null) {
                builder.append(messageSplit).append(" ");
                continue;
            }

            final String colorString = config.getString("player-name-color");
            final ChatColor chatColor = ChatColor.valueOf(
                colorString == null
                    ? "RESET"
                    : colorString
            );

            builder.append(chatColor).append(messagePiece).append(ChatColor.RESET).append(" ");
        }

        event.setMessage(builder.toString());
    }

}
