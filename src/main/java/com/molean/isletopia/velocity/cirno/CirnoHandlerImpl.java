package com.molean.isletopia.velocity.cirno;

import  com.molean.isletopia.velocity.cirno.command.group.WBan;
import  net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CirnoHandlerImpl implements CirnoHandler {

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

    @SuppressWarnings("all")
    public static String getPlainMessage(MessageChain rawMessage) {
        boolean hasQuote = false;
        for (SingleMessage singleMessage : rawMessage) {
            if (singleMessage instanceof QuoteReply) {
                hasQuote = true;
                break;
            }
        }
        StringBuilder plainMessage = new StringBuilder();

        for (SingleMessage singleMessage : rawMessage) {
            String subMessage;
            if (singleMessage instanceof QuoteReply) {
                subMessage = getText((QuoteReply) singleMessage);
            } else if (hasQuote && singleMessage instanceof At && CirnoUtils.isBot(((At) singleMessage).getTarget())) {
                subMessage = "";
            } else if (singleMessage instanceof At) {
                long target = ((At) singleMessage).getTarget();
                subMessage = "@" + CirnoUtils.getNameCardByQQ(target);

            } else if (singleMessage instanceof PlainText) {
                subMessage = singleMessage.contentToString();
            } else if (singleMessage instanceof Image) {
                if (singleMessage instanceof net.mamoe.mirai.internal.message.OnlineImage) {
                    String url = ((net.mamoe.mirai.internal.message.OnlineImage) singleMessage).getOriginUrl();
                    subMessage = "{{{url#" + singleMessage.contentToString() + "#" + url + "}}}";
                } else {
                    subMessage = singleMessage.contentToString();
                }

            } else if (singleMessage instanceof Voice) {
                String url = ((Voice) singleMessage).getUrl();
                subMessage = "{{{url#" + singleMessage.contentToString() + "#" + url + "}}}";
            } else if (singleMessage instanceof Face) {
                subMessage = singleMessage.contentToString();
            } else {
                String s = singleMessage.contentToString();
                if (s.startsWith("<")) {
                    subMessage = "";
                } else {
                    subMessage = singleMessage.contentToString();
                }

            }
            plainMessage.append(subMessage);
        }
        return plainMessage.toString();
    }


    @Override
    public void handler(long id, String nameCard, String plainMessage, MessageChain messageChain) {
        for (Bot bot : CirnoBot.getSubBotList()) {
            if (bot.getId() == id) {
                return;
            }

        }


        String plainMessage1 = getPlainMessage(messageChain);

        I18nString commandCallback = null;
        //command handle
        if (plainMessage.startsWith("/")) {
            String command = plainMessage.substring(1);
            commandCallback = CommandHandler.handleCommand(id, command);
        }
        NormalMember normalMember = CirnoUtils.getGameGroup().get(id);

        boolean bad = false;
        if (normalMember != null) {
            for (String s : WBan.stringSet) {
                if (plainMessage1.contains(s)) {
                    bad = true;
                    if (!PermissionHandler.hasPermission("wban.bypass", id)) {
                        normalMember.mute(60 * 10);
                    }
                    MessageSource.recall(messageChain);
                    break;
                }
            }
        }

        if (!bad) {
            CirnoUtils.broadcastChat(CirnoUtils.getNameCardByQQ(id), plainMessage1);
        }
        if (commandCallback != null) {
            CirnoUtils.broadcastMessage(commandCallback);
        }
        if (messageChain.get(1) instanceof QuoteReply quoteReply) {
            if (PermissionHandler.hasPermission("essence", id)) {
                if (messageChain.get(2) instanceof PlainText plainText) {
                    if (plainText.contentToString().trim().equals("/essence")) {
                        CirnoUtils.getGameGroup().setEssenceMessage(quoteReply.getSource());
                    }
                }
            }
            if (PermissionHandler.hasPermission("recall", id)) {
                if (messageChain.get(2) instanceof PlainText plainText) {
                    if (plainText.contentToString().trim().equals("/recall")) {
                        MessageSource.recall(quoteReply.getSource());
                    }
                }
            }

        }


    }
}
