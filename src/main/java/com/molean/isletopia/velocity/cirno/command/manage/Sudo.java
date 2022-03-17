package com.molean.isletopia.velocity.cirno.command.manage;

import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.platform.PlatformRelatedUtils;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.pojo.req.CommandExecuteRequest;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.velocitypowered.api.proxy.ConsoleCommandSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Sudo implements BotCommandExecutor {
    public Sudo() {
        CommandHandler.setExecutor("sudo", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 2) {
            return I18nString.of("/sudo [server|servers] cmd");
        }

        String serverName = args.get(0);
        StringBuilder cmd = new StringBuilder();
        for (int i = 1; i < args.size(); i++) {
            cmd.append(args.get(i)).append(" ");
        }
        CommandExecuteRequest obj = new CommandExecuteRequest(cmd.toString());
        switch (serverName) {
            case "servers" -> {
                int i =0;
                for (String server : PlatformRelatedUtils.getInstance().getIslandServers()) {
                    if (server.startsWith("server")) {
                        VelocityRelatedUtils.proxyServer.getScheduler().buildTask(VelocityRelatedUtils.getPlugin(), () -> {
                            ServerMessageUtils.sendMessage(server, "CommandExecuteRequest", obj);
                        }).delay(i++ * 2, TimeUnit.SECONDS).schedule();
                    }
                }
            }
            case "proxy" -> {
                ConsoleCommandSource consoleCommandSource = VelocityRelatedUtils.getProxyServer().getConsoleCommandSource();
                VelocityRelatedUtils.getProxyServer().getCommandManager().executeAsync(consoleCommandSource, cmd.toString());
            }
            default -> ServerMessageUtils.sendMessage(serverName, "CommandExecuteRequest", obj);
        }
        return I18nString.of("OK");
    }
}
