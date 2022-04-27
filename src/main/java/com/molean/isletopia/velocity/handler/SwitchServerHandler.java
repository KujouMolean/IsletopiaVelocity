package com.molean.isletopia.velocity.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.platform.PlatformRelatedUtils;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.SwitchServerRequest;
import com.molean.isletopia.velocity.ThreadUtil;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class SwitchServerHandler implements MessageHandler<SwitchServerRequest> {
    public SwitchServerHandler() {
        RedisMessageListener.setHandler("SwitchServer", this, SwitchServerRequest.class);
    }

    private static final Map<String, Long> map = new HashMap<>();

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, SwitchServerRequest message) {
        String player = message.getPlayer();
        String targetServer = message.getServer();
        PlatformRelatedUtils.getInstance().runAsync(() -> {
            for (int i = 0; i < 3; i++) {
                Optional<Player> proxiedPlayer = VelocityRelatedUtils.getProxyServer().getPlayer(player);
                if (proxiedPlayer.isEmpty()) {
                    return;
                }
                Optional<ServerConnection> currentServer = proxiedPlayer.get().getCurrentServer();
                if (currentServer.isEmpty()) {
                    ThreadUtil.sleepForSecond();
                    continue;
                }
                String name = currentServer.get().getServerInfo().getName();
                if (targetServer.equalsIgnoreCase(name)) {
                    return;
                }
                Optional<RegisteredServer> server = VelocityRelatedUtils.getProxyServer().getServer(targetServer);
                if (server.isEmpty()) {
                    return;
                }
                proxiedPlayer.get().createConnectionRequest(server.get()).connect();
                PlatformRelatedUtils.getInstance().getLogger().info("Switch " + player + " from " + name + " to " + targetServer);
                return;
            }
        });

    }
}
