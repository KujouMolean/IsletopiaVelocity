package com.molean.isletopia.velocity.cirno.command.group;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.parameter.CustomParameter;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;

public class WPardon implements BotCommandExecutor {
    public WPardon() {
        CommandHandler.setExecutor("wpardon", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) throws Exception {

        if (args.size() < 1) {
            return I18nString.of("cirno.wpardon.usage");
        }
        CustomParameter.unset("wban", args.get(0));
        WBan.stringSet.remove(args.get(0));
        return I18nString.of("cirno.wpardon.ok").add("word", args.get(0));
    }
}
