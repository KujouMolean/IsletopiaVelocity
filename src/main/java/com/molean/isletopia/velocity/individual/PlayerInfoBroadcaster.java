package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Interval;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.message.ServerMessageService;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.pojo.obj.PlayerInfoObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class PlayerInfoBroadcaster {


    @AutoInject
    private ServerMessageService serverMessageService;

    @Interval(20)
    public void broadcast() {
        ArrayList<Player> proxiedPlayers = new ArrayList<>(VelocityRelatedUtils.getProxyServer().getAllPlayers());
        Map<UUID, String> playersName = new HashMap<>();
        Map<String, Map<UUID, String>> serverPlayersMap = new HashMap<>();

        for (RegisteredServer allServer : VelocityRelatedUtils.getProxyServer().getAllServers()) {
            serverPlayersMap.put(allServer.getServerInfo().getName(), new HashMap<>());
        }


        for (Player proxiedPlayer : proxiedPlayers) {
            if (proxiedPlayer.getCurrentServer().isEmpty()) {
                continue;
            }
            playersName.put(proxiedPlayer.getUniqueId(), proxiedPlayer.getUsername());
            String server = proxiedPlayer.getCurrentServer().get().getServerInfo().getName();
            Map<UUID, String> map = serverPlayersMap.get(server);
            map.put(proxiedPlayer.getUniqueId(), proxiedPlayer.getUsername());
        }

        PlayerInfoObject playerInfoObject = new PlayerInfoObject();
        playerInfoObject.setPlayers(playersName);
        playerInfoObject.setPlayersPerServer(serverPlayersMap);
        if (proxiedPlayers.size() > 0) {
            serverMessageService.broadcastBungeeMessage(playerInfoObject);
        }

    }
}
