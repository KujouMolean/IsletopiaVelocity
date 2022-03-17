package com.molean.isletopia.velocity.cirno.command.permission;

import com.molean.isletopia.velocity.cirno.*;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;

import java.util.List;

public class RevokeCommand implements BotCommandExecutor {
    public RevokeCommand() {
        CommandHandler.setExecutor("revoke", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 2) {
            return I18nString.of("cirno.revoke.usage");
        } else {

            long qq;
            try {
                qq = Long.parseLong(args.get(0));
            } catch (NumberFormatException e) {
                return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
            }

            if(PermissionHandler.hasPermission(args.get(1),qq)){
                PermissionHandler.removePermission(args.get(1), Long.parseLong(args.get(0)));
                return I18nString.of("cirno.revoke.ok").add("target", CirnoUtils.getNameCardByQQ(qq)).add("permission", args.get(1));
            }else{
                return I18nString.of("cirno.revoke.failed").add("target", CirnoUtils.getNameCardByQQ(qq)).add("permission", args.get(1));
            }
        }
    }

}
