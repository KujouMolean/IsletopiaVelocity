package com.molean.isletopia.velocity.cirno.command.group;

import com.molean.isletopia.shared.parameter.ContactParameter;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CirnoUtils;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.*;

public class AddMember implements BotCommandExecutor {
    public AddMember() {
        CommandHandler.setExecutor("addMember", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 2) {
            return I18nString.of("cirno.addMember.usage");
        } else {
            long qq;
            try {
                qq = Long.parseLong(args.get(0));
            } catch (NumberFormatException e) {
                return I18nString.of("cirno.addMember.notFound").add("args", args.get(0));
            }
            args.remove(0);
            String groupsString = ContactParameter.get(qq, "groups");
            if (groupsString == null) {
                groupsString = "";
            }
            Set<String> groups = new HashSet<>(Arrays.asList(groupsString.split(",")));
            groups.remove("");
            for (String arg : args) {
                groups.add(arg.toLowerCase(Locale.ROOT));
            }
            ContactParameter.set(qq, "groups", String.join(",", groups));

            return I18nString.of("cirno.addMember.ok").add("target", CirnoUtils.getNameCardByQQ(qq)).add("groups", String.join(",", args));
        }

    }

}
