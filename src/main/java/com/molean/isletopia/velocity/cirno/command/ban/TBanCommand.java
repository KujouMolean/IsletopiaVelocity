package com.molean.isletopia.velocity.cirno.command.ban;

import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.utils.I18n;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TBanCommand implements BotCommandExecutor {
    public TBanCommand() {
        CommandHandler.setExecutor("tban", this);
    }


    private static long parse(String time) {
        int week = 0;
        int year = 0;
        int century = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        int month = 0;
        int millionSecond = 0;

        int i = 0;


        while (time.substring(i).matches("[0-9].*")) {

            StringBuilder amountStringBuilder = new StringBuilder();
            while (i < time.length() && Character.isDigit(time.substring(i).charAt(0))) {
                amountStringBuilder.append(time.substring(i).charAt(0));
                i++;
            }


            int amount = Integer.parseInt(amountStringBuilder.toString());


            StringBuilder operatorStringBuilder = new StringBuilder();
            while (i < time.length() && Character.isAlphabetic(time.substring(i).charAt(0))) {
                operatorStringBuilder.append(time.substring(i).charAt(0));
                i++;
            }
            String o = operatorStringBuilder.toString();

            switch (o.toLowerCase(Locale.ROOT)) {
                case "w" -> {
                    week += amount;
                }
                case "c" -> {
                    century += amount;
                }
                case "h" -> {
                    hour += amount;
                }
                case "y" -> {
                    year += amount;
                }
                case "d" -> {
                    day += amount;
                }
                case "m" -> {
                    minute += amount;
                }
                case "s" -> {
                    second += amount;
                }
                case "mon" -> {
                    month += amount;
                }
                case "ms" -> {
                    millionSecond += amount;
                }
            }
        }

        long timeMillis = 0;
        timeMillis += (long) century * 100 * 365 * 24 * 60 * 60 * 1000;
        timeMillis += (long) year * 365 * 24 * 60 * 60 * 1000;
        timeMillis += (long) week * 7 * 24 * 60 * 60 * 1000;
        timeMillis += (long) month * 30 * 24 * 60 * 60 * 1000;
        timeMillis += (long) day * 24 * 60 * 60 * 1000;
        timeMillis += (long) hour * 60 * 60 * 1000;
        timeMillis += (long) minute * 60 * 1000;
        timeMillis += (long) second * 1000;
        timeMillis += millionSecond;

        return timeMillis;
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 2) {
            return I18nString.of("cirno.tban.usage");
        } else {
            UUID uuid = UUIDManager.get(args.get(0));
            if (uuid == null) {
                uuid = UUIDManager.getOnlineSync(args.get(0));
            }
            if (uuid == null) {
                return I18nString.of("cirno.tban.notFound");
            }
            String player = args.get(0);
            String timeString = args.get(1);
            long duration;

            try {
                duration = parse(timeString);
            } catch (Exception e) {
                return I18nString.of("cirno.tban.parseError");
            }
            long l = System.currentTimeMillis() + duration;
            PlayerParameter.set(uuid, "isBanned", "true");
            PlayerParameter.set(uuid, "pardonTime", l + "");
            String reason;
            if (args.size() >= 2) {
                ArrayList<String> strings = new ArrayList<>(args);
                strings.remove(0);
                strings.remove(0);
                reason = String.join(" ", strings);
                PlayerParameter.set(uuid, "bannedReason", reason);
            } else {
                reason = "";
            }
            LocalDateTime localDateTime = new Timestamp(l).toLocalDateTime();
            localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
            String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Optional<Player> optionalPlayer = VelocityRelatedUtils.getProxyServer().getPlayer(player);

            optionalPlayer.ifPresent(value -> value.disconnect(Component.text(I18n.getMessage(value.getPlayerSettings().getLocale(), "cirno.tban.info", Pair.of("datetime", format), Pair.of("reason", reason)))));

            return I18nString.of("cirno.tban.ok").add("player", player).add("datetime", format);
        }
    }

}
