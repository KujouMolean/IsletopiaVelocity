package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.velocity.annotation.Listener;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Locale;
import java.util.Optional;

@Listener
public class IslandCommand {

    @Subscribe
    public void on(CommandExecuteEvent event) {
        CommandSource commandSource = event.getCommandSource();
        if (!(commandSource instanceof Player player)) {
            return;
        }
        if (event.getCommand().toLowerCase(Locale.ROOT).equals("is")) {
            Optional<ServerConnection> currentServer = player.getCurrentServer();
            if (currentServer.isPresent()) {
                if (currentServer.get().getServerInfo().getName().toLowerCase(Locale.ROOT).startsWith("club_")) {
                    Optional<RegisteredServer> dispatcher = VelocityRelatedUtils.getProxyServer().getServer("dispatcher");
                    if (dispatcher.isPresent()) {
                        player.createConnectionRequest(dispatcher.get()).connect();
                        event.setResult(CommandExecuteEvent.CommandResult.denied());
                    }
                }
            }
        }
    }
}