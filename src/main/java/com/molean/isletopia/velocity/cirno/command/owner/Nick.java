package com.molean.isletopia.velocity.cirno.command.owner;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CirnoUtils;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import net.mamoe.mirai.contact.NormalMember;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Nick implements BotCommandExecutor {
    public Nick() {
        CommandHandler.setExecutor("nick", this);
    }

    @Override
    public I18nString execute(long id, @NotNull List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.nick.usage");
        }
        long qq;
        try {
            qq = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
             return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
 
        }
        String name = "";
        if (args.size() >= 2) {
            name = args.get(1);
        }
        if (!CirnoUtils.getGameGroup().contains(qq)) {
             return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
 
        }
        NormalMember normalMember = CirnoUtils.getGameGroup().get(qq);
        if (normalMember == null) {
             return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
 
        }
        String nameCardByQQ = CirnoUtils.getNameCardByQQ(normalMember.getId());
        if (name.equals("query")) {
            return I18nString.of(normalMember.getNameCard());
        }
        normalMember.setNameCard(name);
        return I18nString.of("cirno.nick.ok").add("target", nameCardByQQ).add("name", name);
    }

}
