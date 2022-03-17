package com.molean.isletopia.velocity.cirno.command.ban;

import com.molean.isletopia.shared.utils.I18n;
import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.parameter.HostNameParameter;
import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;
import java.util.UUID;

public class PardonIpCommand implements BotCommandExecutor {
    public PardonIpCommand() {
        CommandHandler.setExecutor("pardon-ip", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.pardon-ip.usage");
        } else {

            UUID uuid = UUIDManager.get(args.get(0));
            if (uuid == null) {
                uuid = UUIDManager.getOnlineSync(args.get(0));
            }
            if (uuid == null) {
                return I18nString.of("cirno.pardon-ip.notFound.name");
            }
            String hostName = PlayerParameter.get(uuid, "hostAddress");
            if (hostName == null) {
                return I18nString.of("cirno.pardon-ip.notFound.hostname");
            }
            HostNameParameter.set(hostName, "isBanned", "false");
            return I18nString.of("cirno.pardon-ip.ok").add("hostname", hostName);
        }
    }

}
