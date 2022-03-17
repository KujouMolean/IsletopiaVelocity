package com.molean.isletopia.velocity.cirno.command.ban;

import com.molean.isletopia.shared.parameter.HostNameParameter;
import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.utils.I18n;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.UUID;

public class BanIpCommand implements BotCommandExecutor {
    public BanIpCommand() {
        CommandHandler.setExecutor("ban-ip", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.ban-ip.usage");
        } else {
            UUID uuid = UUIDManager.get(args.get(0));
            if (uuid == null) {
                uuid = UUIDManager.getOnlineSync(args.get(0));
            }
            if (uuid == null) {
                return I18nString.of("cirno.ban-ip.notFound");
            }

            String hostName = PlayerParameter.get(uuid, "hostAddress");
            if (hostName == null) {
                return I18nString.of("cirno.ban-ip.notFound");
            }
            HostNameParameter.set(hostName, "isBanned", "true");
            for (Player player : VelocityRelatedUtils.getProxyServer().getAllPlayers()) {
                if (player.getUsername().equalsIgnoreCase(args.get(0))) {
                    player.disconnect(Component.text(I18n.getMessage(player.getPlayerSettings().getLocale(), "cirno.ban.banned")));
                }
            }

            return I18nString.of("cirno.ban-ip.ok").add("hostname", hostName);
        }
    }
}
