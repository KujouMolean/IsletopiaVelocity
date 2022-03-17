package com.molean.isletopia.velocity.cirno.command.permission;

import com.molean.isletopia.velocity.cirno.*;

import java.util.List;

public class GrantCommand implements BotCommandExecutor {
    public GrantCommand() {
        CommandHandler.setExecutor("grant", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 2) {
            return I18nString.of("cirno.grant.usage");
        } else {
            long qq;
            try {
                qq = Long.parseLong(args.get(0));
            } catch (NumberFormatException e) {
                return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
            }
            args.remove(0);
            for (String arg : args) {
                PermissionHandler.grantPermission(arg, qq);
            }
            return I18nString.of("OK");
        }

    }

}
