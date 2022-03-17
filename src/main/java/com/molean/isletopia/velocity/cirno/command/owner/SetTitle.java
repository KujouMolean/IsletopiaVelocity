package com.molean.isletopia.velocity.cirno.command.owner;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CirnoUtils;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import net.mamoe.mirai.contact.NormalMember;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetTitle implements BotCommandExecutor {
    public SetTitle() {
        CommandHandler.setExecutor("setTitle", this);
    }

    @Override
    public I18nString execute(long id, @NotNull List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.settitle.usage");
        }
        long qq;
        try {
            qq = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
             return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
 
        }
        String title = "";
        if (args.size() >= 2) {
            title = args.get(1);
        }
        if (!CirnoUtils.getGameGroup().contains(qq)) {
             return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
 
        }
        NormalMember normalMember = CirnoUtils.getGameGroup().get(qq);
        if (normalMember == null) {
             return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
 
        }
        normalMember.setSpecialTitle(title);
        return I18nString.of("cirno.success");
    }

}
