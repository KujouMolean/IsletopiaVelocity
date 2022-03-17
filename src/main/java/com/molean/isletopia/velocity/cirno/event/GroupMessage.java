package com.molean.isletopia.velocity.cirno.event;

import  com.molean.isletopia.velocity.cirno.CirnoBot;
import  com.molean.isletopia.velocity.cirno.CirnoUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupMessage extends SimpleListenerHost {

    public GroupMessage() {
       CirnoUtils.registerListener(this);

    }

    private static String getText(QuoteReply quoteReply) {
        MessageChain originalMessage = quoteReply.getSource().getOriginalMessage();
        String originText = originalMessage.contentToString();
        Pattern pattern = Pattern.compile("<([a-zA-Z0-9_]{3,18})> .*");
        Matcher matcher = pattern.matcher(originText);
        if (matcher.matches()) {
            String group = matcher.group(1);
            System.out.println(group);
            return "@" + group + " ";
        } else {
            return quoteReply.contentToString();
        }
    }

    @EventHandler
    public void onGroupMessage(GroupMessageEvent event) {
        if (event.getGroup().getId() != 483653595) {
            return;
        }
        long id = event.getSender().getId();
        for (Bot bot : CirnoBot.getSubBotList()) {
            if (id == bot.getId()) {
                return;
            }
        }

        if (Calendar.getInstance().getTimeInMillis() / 1000 - event.getTime() > 30) {
            return;
        }

        boolean hasQuote = false;
        for (SingleMessage singleMessage : event.getMessage()) {
            if (singleMessage instanceof QuoteReply) {
                hasQuote = true;
                break;
            }
        }

        String nameCard = event.getSender().getNameCard();
        StringBuilder plainMessage = new StringBuilder();
        MessageChain rawMessage = event.getMessage();
        for (SingleMessage singleMessage : rawMessage) {
            String subMessage;
            if (singleMessage instanceof QuoteReply) {
                subMessage = getText((QuoteReply) singleMessage);
            } else if (hasQuote && singleMessage instanceof At && ((At) singleMessage).getTarget() == CirnoBot.getMainBot().getId()) {
                subMessage = "";
            } else if (singleMessage instanceof At) {
                subMessage = singleMessage.contentToString();
            } else if (singleMessage instanceof PlainText) {
                subMessage = singleMessage.contentToString();
            } else if (singleMessage instanceof Image) {
                subMessage = singleMessage.contentToString();
            } else if (singleMessage instanceof Voice) {
                subMessage = singleMessage.contentToString();
            } else if (singleMessage instanceof Face) {
                subMessage = singleMessage.contentToString();
            } else {
                subMessage = "";
            }
            plainMessage.append(subMessage);
        }
        if (CirnoBot.getCirnoHandler() != null) {
            CirnoBot.getCirnoHandler().handler(id, nameCard, plainMessage.toString(), event.getMessage());
        }
    }
}
