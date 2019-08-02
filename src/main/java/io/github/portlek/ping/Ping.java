package io.github.portlek.ping;

import io.github.portlek.actionbar.api.ActionBarPlayer;
import io.github.portlek.actionbar.base.ActionBarPlayerOf;
import io.github.portlek.title.api.TitlePlayer;
import io.github.portlek.title.base.TitlePlayerOf;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Ping extends JavaPlugin implements Listener {

    private boolean messageEnable;
    private boolean soundEnable;
    private boolean titleEnable;
    private boolean actionBarEnable;

    private String pingItem;
    private String message;
    private String actionBarMessage;
    private String titleMessage;
    private String subTitleMessage;
    private int fadeIn;
    private int showTime;
    private int fadeOut;
    private String soundString;
    private ChatColor chatColor;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        final FileConfiguration config = getConfig();

        pingItem = config.getString("ping-item");

        messageEnable = config.getBoolean("ping-message.enable");
        soundEnable = config.getBoolean("ping-sound.enable");
        titleEnable = config.getBoolean("ping-title.enable");
        actionBarEnable = config.getBoolean("ping-actionbar.enable");

        message = config.getString("ping-message.message");

        actionBarMessage = config.getString("ping-message.actionbar");

        titleMessage = config.getString("ping-title.title");
        subTitleMessage = config.getString("ping-title.sub-title");
        fadeIn = config.getInt("ping-title.fade-in");
        showTime = config.getInt("ping-title.show-time");
        fadeOut = config.getInt("ping-title.fade-out");

        soundString = config.getString("ping-sound.sound");

        final String colorString = config.getString("player-name-color");
        chatColor = ChatColor.valueOf(
            colorString == null
                ? "RESET"
                : colorString
        );

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void ping(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        if (pingItem == null || !event.getMessage().contains(pingItem)) return;

        final String[] split = event.getMessage().split(" ");
        final StringBuilder builder = new StringBuilder();

        final List<Player> players = new ArrayList<>();

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

            if (!players.contains(target))
                players.add(target);

            builder.append(chatColor).append(messagePiece).append(ChatColor.RESET).append(" ");
        }

        players.forEach(target -> {
            if (messageEnable) {
                if (message != null && !message.isEmpty()) {
                    target.sendMessage(
                        c(message
                            .replaceAll("%sender%", event.getPlayer().getName())
                            .replaceAll("%player%", target.getName())
                        )
                    );
                }
            }

            if (actionBarEnable) {
                if (actionBarMessage != null && !actionBarMessage.isEmpty()) {
                    final ActionBarPlayer actionBarPlayer = new ActionBarPlayerOf(target);
                    actionBarPlayer.sendActionBar(c(actionBarMessage));
                }
            }

            if (titleEnable) {
                final TitlePlayer titlePlayer = new TitlePlayerOf(target);

                titlePlayer.sendTitle(
                    titleMessage == null ? "" : c(titleMessage),
                    subTitleMessage == null ? "" : c(subTitleMessage),
                    fadeIn, showTime, fadeOut
                );
            }

            if (soundEnable) {
                if (soundString != null && !soundString.isEmpty()) {
                    try {
                        target.playSound(target.getLocation(), Sound.valueOf(soundString), 1, 3);
                    } catch (Exception ignore) {
                    }
                }
            }
        });

        event.setMessage(builder.toString());
    }

    @NotNull
    private String c(@NotNull final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
