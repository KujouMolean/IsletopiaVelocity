package com.molean.isletopia.velocity.cirno.command.info;

import com.google.gson.JsonParser;
import com.molean.isletopia.shared.database.PlayerStatsDao;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.sql.SQLException;
import java.util.*;

public class Rank implements BotCommandExecutor {
    public Rank() {
        CommandHandler.setExecutor("rank", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) throws SQLException {
        //rank minecraft:custom minecraft:play_time
        if (args.size() < 2) {
            return I18nString.of("cirno.rank.usage");
        }
        String type = args.get(0).toLowerCase(Locale.ROOT);
        String item = args.get(1).toLowerCase(Locale.ROOT);

        Map<UUID, String> map = PlayerStatsDao.queryAll();
        HashMap<UUID, Integer> records = new HashMap<>();

        JsonParser jsonParser = new JsonParser();
        map.forEach((uuid, stats) -> {
            int record = 0;
            try {
                record = jsonParser.parse(stats).getAsJsonObject()
                        .get("stats").getAsJsonObject()
                        .get("minecraft:" + type).getAsJsonObject()
                        .get("minecraft:" + item).getAsInt();
            } catch (Exception ignored) {
            }
            if (record > 0) {
                records.put(uuid, record);
            }
        });
        if (records.size() == 0) {
            return I18nString.of("cirno.rank.notFound").add("type", type).add("item", item);
        }


        PriorityQueue<UUID> priorityQueue = new PriorityQueue<>((o1, o2) -> records.get(o2) - records.get(o1));
        priorityQueue.addAll(records.keySet());

        if (args.size() > 2) {
            String playername = args.get(2);
            UUID uuid = UUIDManager.get(playername);
            if (uuid == null) {
                return I18nString.of("cirno.rank.notFound.player").add("player", playername);
            }
            int rank = 0;
            int count = 1;
            while (!priorityQueue.isEmpty()) {
                if (uuid.equals(priorityQueue.poll())) {
                    rank = count;
                    break;
                }
                count++;
            }
            if (rank > 0) {
                return I18nString.of("cirno.rank.info.player")
                        .add("rank", rank + "")
                        .add("player", playername)
                        .add("score", records.get(uuid) + "")
                        .add("type", (locale) -> {
                            if (type.equals("custom")) {
                                return LangUtils.get(locale, "stat.generalButton");
                            } else {
                               return LangUtils.get(locale, "stat_type.minecraft." + type);
                            }
                        })
                        .add("item", locale -> {
                            if (LangUtils.contains(locale, "stat.minecraft." + item)) {
                                return LangUtils.get(locale, "stat.minecraft." + item);
                            }
                            if (LangUtils.contains(locale, "item.minecraft." + item)) {
                                return LangUtils.get(locale, "item.minecraft." + item);
                            }
                            if (LangUtils.contains(locale, "block.minecraft." + item)) {
                                return LangUtils.get(locale, "block.minecraft." + item);
                            }
                            if (LangUtils.contains(locale, "entity.minecraft." + item)) {
                                return LangUtils.get(locale, "entity.minecraft." + item);
                            }
                            return item;
                        })
                        .add("over", "%.2f".formatted((records.size() - rank + 1) * 100.0 / records.size()));
            } else {
                return I18nString.of("cirno.rank.notFound.rank");
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < priorityQueue.size() && i < 10; i++) {
            UUID poll = priorityQueue.poll();
            if (poll == null) {
                continue;
            }
            String name = UUIDManager.get(poll);
            if (name == null) {
                continue;
            }
            stringBuilder.append("%s(%d) ".formatted(name, records.get(poll)));
        }

        return I18nString.of(stringBuilder.toString());
    }

}
