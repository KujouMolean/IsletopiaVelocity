package com.molean.isletopia.velocity.cirno.command.info;

import com.molean.isletopia.shared.utils.I18n;
import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;
import java.util.UUID;

public class UUIDCommand implements BotCommandExecutor {
    public UUIDCommand() {
        CommandHandler.setExecutor("uuid", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) throws Exception {
        if (args.size() < 1) {
            return I18nString.of("cirno.uuid.usage");
        }

        String name = args.get(0);
        if (!name.matches("[0-9a-zA-z_#]{3,16}")) {
            return I18nString.of("cirno.uuid.invalid");
        }
        UUID uuid = UUIDManager.get(name);
        if (uuid == null) {
            uuid = UUIDManager.getOnlineSync(name);

        }
        if (uuid == null) {
            return I18nString.of("cirno.uuid.notFound").add("arg", args.get(0));
        }
        return I18nString.of(uuid.toString());
    }

}
