package com.molean.isletopia.velocity;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class MessageUtils {
    public static void warn(Audience player, String message) {
        player.sendMessage(Component.text("§8[§c危险警告§8] §e" + message));
    }

    public static void info(Audience player, String message) {
        player.sendMessage(Component.text("§8[§3岛屿助手§8] §7" + message));
    }

    public static void notify(Audience player, String message) {
        player.sendMessage(Component.text("§8[§3温馨提示§8] §e" + message));
    }

    public static void strong(Audience player, String message) {
        player.sendMessage(Component.text("§8[§3温馨提示§8] §e§l" + message));
    }

    public static void fail(Audience player, String message) {
        player.sendMessage(Component.text("§8[§3岛屿助手§8] §c" + message));
    }

    public static void success(Audience player, String message) {
        player.sendMessage(Component.text("§8[§3岛屿助手§8] §6" + message));
    }

    public static void action(Audience player, String message) {
        player.sendActionBar(Component.text("§6" + message));
    }

    public static void custom(Audience player, String prefix, String message) {
        player.sendMessage(Component.text("§8[§3" + prefix + "§8] §c" + message));
    }
}
