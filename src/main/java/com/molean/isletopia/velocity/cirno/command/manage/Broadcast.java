package com.molean.isletopia.velocity.cirno.command.manage;

import com.molean.isletopia.shared.utils.I18n;
import com.molean.isletopia.shared.utils.Pair;
import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.List;

public class Broadcast implements BotCommandExecutor {
    public Broadcast() {
        CommandHandler.setExecutor("broadcast", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) throws Exception {
        if (args.size() < 1) {
            return I18nString.of("cirno.broadcast");
        }
        for (Player player : VelocityRelatedUtils.getProxyServer().getAllPlayers()) {
            player.sendMessage(Component.text(I18n.getMessage(player.getPlayerSettings().getLocale(), "cirno.broadcast.info", Pair.of("msg",String.join(" ", args)))));
        }
        return I18nString.of("OK");
    }

}
