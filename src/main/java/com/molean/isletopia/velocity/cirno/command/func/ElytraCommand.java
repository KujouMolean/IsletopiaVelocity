package com.molean.isletopia.velocity.cirno.command.func;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;
import java.util.UUID;

public class ElytraCommand implements BotCommandExecutor {
    public ElytraCommand() {
        CommandHandler.setExecutor("elytra", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 2) {
            return I18nString.of("cirno.elytra.usage");
        }
        String player = args.get(0);
        String reason = args.get(1);
        UUID uuid = UUIDManager.get(player);
        if (uuid == null) {
            return I18nString.of("cirno.elytra.notFound");
        } else {

            UniversalParameter.addParameter(uuid, "elytra", "true");
            UniversalParameter.setParameter(uuid, "elytraReason", reason);
            return I18nString.of("cirno.elytra.reason").add("player", player).add("reason", reason);
        }
    }

}
