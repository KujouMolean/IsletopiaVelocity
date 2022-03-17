package com.molean.isletopia.velocity.cirno.command.permission;

import com.molean.isletopia.velocity.cirno.*;

import java.util.List;

public class PermissionCommand implements BotCommandExecutor {
    public PermissionCommand() {
        CommandHandler.setExecutor("permission", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.permission.usage");
        }
        long qq;
        try {
            qq = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
        }

        List<String> list = PermissionHandler.getPermissions(qq);

        if (list.isEmpty()) {
            return I18nString.of("cirno.permission.empty").add("target", CirnoUtils.getNameCardByQQ(qq));
        } else {
            return I18nString.of("cirno.permission.list").add("permissions", String.join(", ", list));
        }
    }

}
