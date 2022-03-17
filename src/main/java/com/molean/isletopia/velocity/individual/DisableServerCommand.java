package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Locale;
import java.util.Optional;

public class DisableServerCommand {
    public DisableServerCommand() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);
    }

    @Subscribe
    public void on(CommandExecuteEvent event) {
        CommandSource commandSource = event.getCommandSource();
        if (!(commandSource instanceof Player player)) {
            return;
        }
        if (event.getCommand().toLowerCase(Locale.ROOT).equals("server")) {
            if (!player.getUsername().equals("Molean")) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
        }
    }
}
