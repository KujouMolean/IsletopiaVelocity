package com.molean.isletopia.velocity.cirno.command.manage;

import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;
import java.util.UUID;

public class Warn implements BotCommandExecutor {
    public Warn() {
        CommandHandler.setExecutor("warn", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) throws Exception {
        if (args.size() < 1) {
            return I18nString.of("/warn [player]");
        }
        UUID uuid = UUIDManager.get(args.get(0));
        if (uuid == null) {
            uuid = UUIDManager.getOnlineSync(args.get(0));
        }
        if (uuid == null) {
            return I18nString.of("cirno.ban.noSuchPlayer");
        }
        PlayerParameter.set(uuid, "warning", (System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L) + "");
        return I18nString.of("OK");
    }

}
