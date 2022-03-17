package com.molean.isletopia.velocity.cirno.command.info;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;
import java.util.UUID;

public class HostNameCommand implements BotCommandExecutor {
    public HostNameCommand() {
        CommandHandler.setExecutor("hostname", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.hostname.usage");
        }
        UUID uuid = UUIDManager.get(args.get(0));
        if (uuid == null) {
            uuid = UUIDManager.getOnlineSync(args.get(0));
        }
        if (uuid == null) {
            return I18nString.of("cirno.hostname.notFound.name");
        }
        String hostAddress = PlayerParameter.get(uuid, "hostAddress");

        if (hostAddress == null || hostAddress.isEmpty()) {
            return I18nString.of("cirno.hostname.notFound.hostname");
        } else {
            return I18nString.of(hostAddress);
        }
    }
}
