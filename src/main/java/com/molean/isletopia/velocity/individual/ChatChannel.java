package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.service.RedisService;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;

import java.util.*;

@Singleton
public class ChatChannel {

    private final List<String> availableChannels = List.of("黑", "深蓝", "深绿", "湖蓝", "深红", "紫", "金", "灰", "深灰", "蓝", "绿", "天蓝", "红", "粉红", "黄", "白");


    private RedisService redisService;

    public String getChannelColor(String channel) {
        if (availableChannels.contains(channel)) {
            return String.format("§%x", availableChannels.indexOf(channel));
        }
        return "§f";
    }

    public List<String> getChannels(UUID uuid) {
        String s = redisService.getCommand().get("Channel:" + uuid);
        if (s == null || s.isEmpty()) {
            s = "白";
        }
        return new ArrayList<>(Arrays.asList(s.split(",")));
    }

    public Collection<UUID> getPlayersInChannel(String channel) {
        ArrayList<UUID> uuids = new ArrayList<>();
        for (Player player : VelocityRelatedUtils.getProxyServer().getAllPlayers()) {
            Optional<ServerConnection> currentServer = player.getCurrentServer();
            if (currentServer.isEmpty()) {
                continue;
            }
            if (currentServer.get().getServerInfo().getName().startsWith("club_")) {
                continue;
            }
            List<String> channels = getChannels(player.getUniqueId());
            if (channels.contains(channel)) {
                uuids.add(player.getUniqueId());
            }
        }
        return uuids;
    }
}
