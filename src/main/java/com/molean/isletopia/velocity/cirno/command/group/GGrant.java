package com.molean.isletopia.velocity.cirno.command.group;

import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.molean.isletopia.velocity.cirno.PermissionHandler;

import java.util.List;

public class GGrant implements BotCommandExecutor {
    public GGrant() {
        CommandHandler.setExecutor("gGrant", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 2) {
            return I18nString.of("cirno.gGrant.usage");
        } else {

            String group = args.get(0);
            args.remove(0);
            for (String arg : args) {
                PermissionHandler.grantPermission(arg, group);
            }
            return I18nString.of("cirno.gGrant.ok").add("group", group).add("permissions", String.join(",", args));
        }
    }
}
