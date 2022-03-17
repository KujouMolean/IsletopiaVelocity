package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UniversalTell implements SimpleCommand {

    public UniversalTell() {
        CommandManager commandManager = VelocityRelatedUtils.getProxyServer().getCommandManager();
        CommandMeta meta = commandManager.metaBuilder("tell")
                // Specify other aliases (optional)
                .aliases("msg", "w")
                .build();
        commandManager.register(meta, this);
    }


    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player sourcePlayer)) {
            return;
        }
        String[] args = invocation.arguments();
        if (args.length < 2) {
            sourcePlayer.sendMessage(Component.text("§c用法: /tell [玩家] [消息..]"));
            return;
        }

        Optional<Player> targetPlayer = VelocityRelatedUtils.getProxyServer().getPlayer(args[0]);
        if (targetPlayer.isEmpty()) {
            sourcePlayer.sendMessage(Component.text("§c玩家不在线"));
            return;
        }

        //

        ArrayList<String> strings = new ArrayList<>(Arrays.asList(args));
        strings.remove(0);
        String message = String.join(" ", strings);
        message = message.replaceAll("\\$", "￥");
        String name = sourcePlayer.getUsername();
        String finalMessage = "§7" + name + " §7-> " + targetPlayer.get().getUsername() + "§7: " + message;
        targetPlayer.get().sendMessage(Component.text(finalMessage));
        sourcePlayer.sendMessage(Component.text(finalMessage));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] arguments = invocation.arguments();

        if (arguments.length == 1) {
            return VelocityRelatedUtils.getProxyServer().getAllPlayers().stream()
                    .map(Player::getUsername)
                    .filter(s -> s.startsWith(arguments[0]))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
    }
}
