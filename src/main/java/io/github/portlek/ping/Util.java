package io.github.portlek.ping;

import io.github.portlek.actionbar.base.ActionBarPlayerOf;
import io.github.portlek.title.base.TitlePlayerOf;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class Util {

    private final boolean messageEnable;

    private final boolean actionBarEnable;

    private final boolean titleEnable;

    private final boolean soundEnable;

    @NotNull
    private final String message;

    @NotNull
    private final String actionBarMessage;

    @NotNull
    private final String titleMessage;

    @NotNull
    private final String subTitleMessage;

    @NotNull
    private final String soundString;

    private final int fadeIn;

    private final int showTime;

    private final int fadeOut;

    public Util(boolean messageEnable, boolean actionBarEnable, boolean titleEnable, boolean soundEnable,
                @NotNull String message, @NotNull String actionBarMessage, @NotNull String titleMessage,
                @NotNull String subTitleMessage, @NotNull String soundString, int fadeIn, int showTime, int fadeOut) {
        this.messageEnable = messageEnable;
        this.actionBarEnable = actionBarEnable;
        this.titleEnable = titleEnable;
        this.soundEnable = soundEnable;
        this.message = message;
        this.actionBarMessage = actionBarMessage;
        this.titleMessage = titleMessage;
        this.subTitleMessage = subTitleMessage;
        this.soundString = soundString;
        this.fadeIn = fadeIn;
        this.showTime = showTime;
        this.fadeOut = fadeOut;
    }

    public void send(@NotNull Player player, @NotNull Player target) {
        if (messageEnable && !message.isEmpty()) {
            target.sendMessage(
                c(message, player, target)
            );
        }

        if (actionBarEnable && !actionBarMessage.isEmpty()) {
            new ActionBarPlayerOf(target).sendActionBar(c(actionBarMessage, player, target));
        }

        if (titleEnable) {
            new TitlePlayerOf(target).sendTitle(
                c(titleMessage, player, target),
                c(subTitleMessage, player, target),
                fadeIn, showTime, fadeOut
            );
        }

        if (soundEnable && !soundString.isEmpty()) {
            try {
                target.playSound(target.getLocation(), Sound.valueOf(soundString), 1, 3);
            } catch (Exception ignored) {
                // ignored
            }
        }
    }

    @NotNull
    private String c(@NotNull final String text, @NotNull final Player sender, @NotNull final Player target) {
        return ChatColor.translateAlternateColorCodes('&', text)
            .replaceAll("%sender%", sender.getName())
            .replaceAll("%player%", target.getName());
    }
}
