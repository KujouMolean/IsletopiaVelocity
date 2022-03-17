package com.molean.isletopia.velocity.cirno.command.group;

import com.molean.isletopia.shared.utils.I18n;
import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import  com.molean.isletopia.velocity.cirno.PermissionHandler;

import java.util.List;

public class GPermission implements BotCommandExecutor {
    public GPermission() {
        CommandHandler.setExecutor("gPermission",this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.gPermission.usage");
        }
        String group = args.get(0);
        List<String> list = PermissionHandler.getPermissions(group);

        if (list.isEmpty()) {
            return I18nString.of("cirno.gPermission.empty").add("group", group);
        } else {
            return I18nString.of("cirno.gPermission.list").add("group", group).add("permission", String.join(", ", list));
        }
    }
}
