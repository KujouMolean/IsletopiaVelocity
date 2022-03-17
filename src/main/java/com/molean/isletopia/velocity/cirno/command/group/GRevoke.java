package com.molean.isletopia.velocity.cirno.command.group;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import  com.molean.isletopia.velocity.cirno.PermissionHandler;

import java.util.ArrayList;
import java.util.List;

public class GRevoke implements BotCommandExecutor {
    public GRevoke() {
        CommandHandler.setExecutor("gRevoke", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {

        if (args.size() < 2) {
            return I18nString.of("cirno.gRevoke.usage");
        } else {

            String group = args.get(0);
            args.remove(0);

            ArrayList<String> removed = new ArrayList<>();
            ArrayList<String> ignored = new ArrayList<>();

            for (String arg : args) {
                if (PermissionHandler.hasPermission(arg, group)) {
                    PermissionHandler.removePermission(arg, group);
                    removed.add(arg);
                } else {
                    ignored.add(arg);
                }
            }

            if (removed.isEmpty()) {
                return I18nString.of("cirno.gRevoke.noSuch");
            } else {
                if (ignored.isEmpty()) {
                    return I18nString.of("cirno.gRevoke.ok");
                } else {
                    return I18nString.of("cirno.gRevoke.part").add("removed", String.join(",", removed)).add("ignored", String.join(",", ignored));
                }

            }
        }
    }
}
