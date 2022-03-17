package com.molean.isletopia.velocity.cirno.command.group;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CirnoUtils;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.parameter.ContactParameter;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Members implements BotCommandExecutor {
    public Members() {
        CommandHandler.setExecutor("members",this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.members.usage");
        } else {
            String group = args.get(0);
            HashSet<String> strings = new HashSet<>();

            for (Long target : ContactParameter.targets()) {
                String groups = ContactParameter.get(target, "groups");
                if (groups == null) {
                    groups = "";
                }
                if (Arrays.asList(groups.split(",")).contains(group)) {
                    strings.add(CirnoUtils.getNameCardByQQ(target));
                }
            }
            if (!strings.isEmpty()) {
                return I18nString.of("cirno.members.list").add("members", String.join(",", strings));
            }else{
                return I18nString.of("cirno.members.none");
            }
        }
    }
}
