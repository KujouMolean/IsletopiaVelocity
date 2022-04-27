package com.molean.isletopia.velocity.cirno;

import com.molean.isletopia.shared.utils.I18n;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.velocity.individual.UniversalChat;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.BotEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CirnoUtils {


    public static int realLength(String string) {
        int length = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < string.length(); i++) {
            String single = string.substring(i, i + 1);
            if (single.matches(chinese)) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length;
    }

    public static String leftString(String string, int size) {
        if (size == 0)
            return "";
        int length = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < string.length(); i++) {
            String single = string.substring(i, i + 1);
            if (single.matches(chinese)) {
                length += 2;
            } else {
                length += 1;
            }
            if (length >= size) {
                return string.substring(0, i + 1);
            }
        }
        return string;
    }


    public static String getNameCardByQQ(long id) {

        NormalMember normalMember = getGameGroup().get(id);
        if (normalMember == null) {
            return id + "";
        }
        String nameCard = normalMember.getNameCard();
        if (nameCard.isEmpty()) {
            return normalMember.getNick();
        } else {
            return normalMember.getNameCard();
        }
    }

    public static net.mamoe.mirai.contact.Group getGameGroup() {
        return CirnoBot.getMainBot().getGroup(483653595L);
    }

    public static void groupMessage(String message) {
        List<Bot> availableBot = new ArrayList<>();
        for (Bot bot : CirnoBot.getSubBotList()) {
            if (bot.isOnline()) {
                availableBot.add(bot);
            }
        }
        int dayOfMonth = LocalDateTime.now().getDayOfMonth();
        Bot bot;
        if (availableBot.size() == 0) {
            bot = CirnoBot.getMainBot();
        } else {
            bot = availableBot.get(dayOfMonth % availableBot.size());
        }

        if (bot != null) {
            Group group = bot.getGroup(483653595L);
            if (group != null) {
                group.sendMessage(message.replaceAll("§.", ""));
            }
        }
    }

    public static void broadcastMessage(I18nString message) {
        String zhCNRawMessage = I18n.getMessage(Locale.SIMPLIFIED_CHINESE, message.getNode());
        for (Pair<String, String> stringStringPair : message.getPairList()) {
            zhCNRawMessage = zhCNRawMessage.replaceAll("<" + stringStringPair.getKey() + ">", stringStringPair.getValue());
        }
        for (Pair<String, Function<Locale, String>> stringFunctionPair : message.getDelayedList()) {
            zhCNRawMessage = zhCNRawMessage.replaceAll("<" + stringFunctionPair.getKey() + ">", stringFunctionPair.getValue().apply(Locale.SIMPLIFIED_CHINESE));
        }
        UniversalChat.chatMessage("白", "§bCirnoBot", message);
        groupMessage(zhCNRawMessage);

    }

    public static void broadcastChat(String sender, String message) {
        String p = sender.replace('§', '&');
        String m = message.replace('§', '&');
        m = m.replace("\n\r", "[换行]︎");
        m = m.replace("\n", "[换行]︎");

        if (realLength(p) > 16) {
            p = leftString(p, 16) + "..";
        }

        int addition = 0;

        Pattern pattern = Pattern.compile("\\{\\{\\{url#(.*?)#(.*?)}}}");
        Matcher matcher = pattern.matcher(m);

        if (matcher.find()) {
            do {
                String group = matcher.group();
                addition += group.length();
            } while (matcher.find(matcher.start() + 1));
        }

        if (realLength(m) > 256 + addition) {
            m = leftString(m, 256 + addition) + "..";
        }
        if (m.trim().length() == 0)
            return;
        UniversalChat.chatMessage("白", "§o" + p, I18nString.of(m));
    }

    public static void registerListener(Bot bot,SimpleListenerHost simpleListenerHost) {
        EventChannel<BotEvent> eventChannel = bot.getEventChannel();
        eventChannel.registerListenerHost(simpleListenerHost);
    }


    public static boolean isBot(long id) {
        if (CirnoBot.getMainBot().getId() == id) {
            return true;
        }
        for (Bot bot : CirnoBot.getSubBotList()) {
            if (id == bot.getId()) {
                return true;
            }
        }
        return false;
    }
}
