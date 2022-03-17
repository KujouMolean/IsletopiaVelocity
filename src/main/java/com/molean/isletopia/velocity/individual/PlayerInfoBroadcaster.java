package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.pojo.obj.PlayerInfoObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerInfoBroadcaster {
    public PlayerInfoBroadcaster() {
        ScheduledTask schedule = VelocityRelatedUtils.getProxyServer().getScheduler().buildTask(VelocityRelatedUtils.getPlugin(), () -> {
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
                ServerMessageUtils.broadcastBungeeMessage("PlayerInfo", playerInfoObject);
            }

        }).repeat(1, TimeUnit.SECONDS).schedule();
    }
}
