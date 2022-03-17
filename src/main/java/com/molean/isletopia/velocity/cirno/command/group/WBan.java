package com.molean.isletopia.velocity.cirno.command.group;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.parameter.CustomParameter;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.bossbar.BossBar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WBan implements BotCommandExecutor {

    public static final Set<String> stringSet = new HashSet<>();

    public WBan() {
        CommandHandler.setExecutor("wban", this);
        stringSet.addAll(CustomParameter.keys("wban"));

    }

    @Override
    public I18nString execute(long id, List<String> args) throws Exception {
        if (args.size() < 1) {
            return I18nString.of("cirno.wban.usage");
        }

        CustomParameter.set("wban", args.get(0), "true");
        stringSet.add(args.get(0));
        return I18nString.of("cirno.wban.ok").add("word", args.get(0));
    }

}
