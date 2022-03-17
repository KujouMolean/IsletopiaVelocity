package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ConnectionDetect {
    public ConnectionDetect() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);
    }

    @Subscribe
    public void on(KickedFromServerEvent event) {

        //如果玩家被踢出服务器, 不会直接退出群组, 而如果踢出理由带有#, 则例外.

        RegisteredServer server = event.getServer();
        if (server.getServerInfo().getName().equals("dispatcher")) {
            return;
        }
        Optional<Component> serverKickReason = event.getServerKickReason();
        if (serverKickReason.isPresent() && serverKickReason.get().contains(Component.text("#"))) {
            event.setResult(KickedFromServerEvent.DisconnectPlayer.create(serverKickReason.get()));
        } else {
            Optional<RegisteredServer> dispatcher = VelocityRelatedUtils.getProxyServer().getServer("dispatcher");
            dispatcher.ifPresent(registeredServer -> event.setResult(KickedFromServerEvent.RedirectPlayer.create(registeredServer)));
        }

    }
}
