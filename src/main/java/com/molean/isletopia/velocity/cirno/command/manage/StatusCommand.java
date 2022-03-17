package com.molean.isletopia.velocity.cirno.command.manage;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusCommand implements BotCommandExecutor {
    public StatusCommand() {
        CommandHandler.setExecutor("status", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        Collection<RegisteredServer> allServers = VelocityRelatedUtils.getProxyServer().getAllServers();
        Map<String, Long> lastUpdate = new HashMap<>();
        for (RegisteredServer allServer : allServers) {
            String servername = allServer.getServerInfo().getName();
            if (servername.startsWith("server")) {
                long l = Long.parseLong(RedisUtils.getCommand().get("ServerStatus:LastUpdate:" + servername));
                lastUpdate.put(servername, l);
            }
        }

        boolean bad = false;
        long l = System.currentTimeMillis();
        StringBuilder message = new StringBuilder("Some error occurred:");
        for (String s : lastUpdate.keySet()) {
            long diff = l - lastUpdate.get(s);
            if (diff > 10 * 1000) {
                bad = true;
                message.append("\n").append(s).append(" has already no response for ").append(diff / 1000).append(" seconds");
            }
        }
        if (bad) {
            return I18nString.of(message.toString());
        } else {
            return I18nString.of("Everything is ok");
        }

    }

}
