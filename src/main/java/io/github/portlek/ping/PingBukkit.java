package io.github.portlek.ping;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.portlek.mcyaml.IYaml;
import io.github.portlek.mcyaml.YamlOf;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PingBukkit extends JavaPlugin implements Listener, PluginMessageListener {

    private final IYaml config = new YamlOf(this, "config");

    private Util util;

    private boolean bungee;

    private String pingItem;

    private ChatColor chatColor;

    @Override
    public void onEnable() {
        config.create();

        bungee = config.getOrSet("bungee", false);
        pingItem = config.getOrSet("ping-item", "@");
        final boolean messageEnable = config.getOrSet("ping-message.enable", false);
        final boolean soundEnable = config.getOrSet("ping-sound.enable", false);
        final boolean titleEnable = config.getOrSet("ping-title.enable", false);
        final boolean actionBarEnable = config.getOrSet("ping-actionbar.enable", false);
        final String message = config.getOrSet("ping-message.message", "");
        final String actionBarMessage = config.getOrSet("ping-actionbar.actionbar", "");
        final String titleMessage = config.getOrSet("ping-title.title", "");
        final String subTitleMessage = config.getOrSet("ping-title.sub-title", "");
        final int fadeIn = config.getOrSet("ping-title.fade-in", 20);
        final int showTime = config.getOrSet("ping-title.show-time", 20);
        final int fadeOut = config.getOrSet("ping-title.fade-out", 20);
        final String soundString = config.getOrSet("ping-sound.sound", "");
        final String colorString = config.getOrSet("player-name-color", "RESET");
        chatColor = ChatColor.valueOf(colorString);

        util = new Util(
            messageEnable,
            actionBarEnable,
            titleEnable,
            soundEnable,
            message,
            actionBarMessage,
            titleMessage,
            subTitleMessage,
            soundString,
            fadeIn,
            showTime,
            fadeOut
        );

        if (!bungee) {
            getServer().getPluginManager().registerEvents(this, this);
        } else {
            getServer().getMessenger().getIncomingChannelRegistrations(this, "PingBungee");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void ping(AsyncPlayerChatEvent event) {
        if (bungee || event.isCancelled() || pingItem == null || !event.getMessage().contains(pingItem)) {
            return;
        }

        final String[] split = event.getMessage().split(" ");
        final StringBuilder builder = new StringBuilder();
        final List<Player> players = new ArrayList<>();

        for (String messageSplit : split) {
            final String messagePiece = messageSplit.replaceAll(pingItem, "");
            final Player target = Bukkit.getPlayer(messagePiece);

            if (!messageSplit.contains(pingItem) || target == null || target.equals(event.getPlayer())) {
                builder.append(messageSplit).append( " ");
                continue;
            }

            if (!players.contains(target)){
                players.add(target);
            }

            builder.append(chatColor).append(messagePiece).append(ChatColor.RESET).append(" ");
        }

        players.forEach(player -> util.send(event.getPlayer(), player));

        event.setMessage(builder.toString());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!bungee || !channel.equals("PingBungee")) {
            return;
        }

        final ByteArrayDataInput in = ByteStreams.newDataInput(message);
        final String subchannel = in.readUTF();

        System.out.println(subchannel);
    }

}
