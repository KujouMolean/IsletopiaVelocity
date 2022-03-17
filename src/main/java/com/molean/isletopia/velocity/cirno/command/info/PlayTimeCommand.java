package com.molean.isletopia.velocity.cirno.command.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.database.PlayTimeStatisticsDao;
import com.molean.isletopia.shared.database.PlayerStatsDao;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;
import java.util.UUID;

public class PlayTimeCommand implements BotCommandExecutor {

    public PlayTimeCommand() {
        CommandHandler.setExecutor("playtime", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.playtime.usage");
        }

        String player = args.get(0);
        long l = System.currentTimeMillis();
        UUID uuid = UUIDManager.get(player);
        if (uuid == null) {
            return I18nString.of("cirno.playtime.notFound").add("arg", args.get(0));
        }
        long recent30d = PlayTimeStatisticsDao.getRecentPlayTime(uuid, l - 30L * 24 * 60 * 60 * 1000);
        long recent7d = PlayTimeStatisticsDao.getRecentPlayTime(uuid, l - 7L * 24 * 60 * 60 * 1000);
        long recent3d = PlayTimeStatisticsDao.getRecentPlayTime(uuid, l - 3L * 24 * 60 * 60 * 1000);
        long total = 0;


        try {
            if (PlayerStatsDao.exist(uuid)) {
                String s = PlayerStatsDao.queryForce(uuid);
                JsonElement parse = new JsonParser().parse(s);
                int playtime = parse.getAsJsonObject().get("stats").getAsJsonObject()
                        .get("minecraft:custom").getAsJsonObject()
                        .get("minecraft:play_time").getAsInt();
                if (playtime > 0) {
                    total = playtime / 20 * 1000L;
                }
            }
        } catch (Exception ignored) {
        }

        total /= 1000 * 60 * 60;
        recent30d /= 1000 * 60 * 60;
        recent7d /= 1000 * 60 * 60;
        recent3d /= 1000 * 60 * 60;

        if (recent30d > total) {
            recent30d = total;
        }
        if (recent7d > recent30d) {
            recent7d = recent30d;
        }
        if (recent3d > recent7d) {
            recent3d = recent7d;
        }

        if (total > 0) {
            return I18nString.of("cirno.playtime.infoWithTotal")
                    .add("player", player)
                    .add("3d", recent3d + "")
                    .add("7d", recent7d + "")
                    .add("30d", recent30d + "")
                    .add("total", total+"");
        }else{
            return I18nString.of("cirno.playtime.infoWithoutTotal")
                    .add("player", player)
                    .add("3d", recent3d + "")
                    .add("7d", recent7d + "")
                    .add("30d", recent30d + "");
        }


    }

}
