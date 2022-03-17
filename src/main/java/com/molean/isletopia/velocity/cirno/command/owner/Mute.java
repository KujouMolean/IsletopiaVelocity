package com.molean.isletopia.velocity.cirno.command.owner;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CirnoUtils;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import net.mamoe.mirai.contact.NormalMember;

import java.util.List;
import java.util.Locale;

public class Mute implements BotCommandExecutor {
    public Mute() {
        CommandHandler.setExecutor("mute", this);
    }

    private static int parse(String time) {
        int week = 0;
        int year = 0;
        int century = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        int month = 0;

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
            }
        }

        int seconds = 0;
        seconds += century * 100 * 365 * 24 * 60 * 60;
        seconds += year * 365 * 24 * 60 * 60;
        seconds += week * 7 * 24 * 60 * 60;
        seconds += month * 30 * 24 * 60 * 60;
        seconds += day * 24 * 60 * 60;
        seconds += hour * 60 * 60;
        seconds += minute * 60;
        seconds += second;

        return seconds;
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 2) {
            return I18nString.of("cirno.kick.mute");
        }
        long qq;
        try {
            qq = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
        }

        if (!CirnoUtils.getGameGroup().contains(qq)) {
            return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
        }
        NormalMember normalMember = CirnoUtils.getGameGroup().get(qq);
        if (normalMember == null) {
            return I18nString.of("cirno.targetNotFound").add("target", args.get(0));
        }
        normalMember.mute(parse(args.get(1)));
        return I18nString.of("OK");
    }

}
