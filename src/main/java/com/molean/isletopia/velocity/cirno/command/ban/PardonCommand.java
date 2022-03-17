package com.molean.isletopia.velocity.cirno.command.ban;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;
import java.util.UUID;

public class PardonCommand implements BotCommandExecutor {
    public PardonCommand() {
        CommandHandler.setExecutor("pardon", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.pardon.usage");
        } else {

            UUID uuid = UUIDManager.get(args.get(0));
            if (uuid == null) {
                uuid = UUIDManager.getOnlineSync(args.get(0));
            }
            if (uuid == null) {
                return I18nString.of("cirno.pardon.notFound");
            }
            PlayerParameter.set(uuid, "isBanned", "false");
            return I18nString.of("cirno.pardon.ok").add("player", args.get(0));

        }
    }
}
