package com.molean.isletopia.velocity.cirno.command.info;

import com.molean.isletopia.shared.utils.I18n;
import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.velocitypowered.api.proxy.Player;

import java.util.List;
import java.util.Optional;

public class IsOnlineCommand implements BotCommandExecutor {
    public IsOnlineCommand() {
        CommandHandler.setExecutor("isonline", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.isonline.usage");
        }
        String name = args.get(0);
        Optional<Player> player = VelocityRelatedUtils.getProxyServer().getPlayer(name);
        if (player.isEmpty()) {
            return I18nString.of("cirno.isonline.offline").add("player",name);
        }
        return I18nString.of("cirno.isonline.online").add("player", name);
    }
}
