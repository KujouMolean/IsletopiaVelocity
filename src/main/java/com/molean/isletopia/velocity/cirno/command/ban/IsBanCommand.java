package com.molean.isletopia.velocity.cirno.command.ban;

import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class IsBanCommand implements BotCommandExecutor {
    public IsBanCommand() {
        CommandHandler.setExecutor("isban", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("cirno.isban.usage");
        } else {
            String player = args.get(0);
            UUID uuid = UUIDManager.get(player);
            if (uuid == null) {
                uuid = UUIDManager.getOnlineSync(player);
            }
            if (uuid == null) {
                return I18nString.of("cirno.isban.notFound");
            }

            String isBanned = PlayerParameter.get(uuid, "isBanned");
            String pardonTime = PlayerParameter.get(uuid, "pardonTime");
            String bannedReason = PlayerParameter.get(uuid, "bannedReason");
            if ("true".equals(isBanned)) {
                if (pardonTime != null && !pardonTime.isEmpty()) {
                    long l = Long.parseLong(pardonTime);

                    LocalDateTime localDateTime = new Timestamp(l).toLocalDateTime();
                    localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
                    String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    if (bannedReason != null && !bannedReason.isEmpty()) {

                        return I18nString.of("cirno.isban.tbannedWithReason")
                                .add("player", player)
                                .add("datetime", format)
                                .add("reason", bannedReason);
                    } else {
                        return I18nString.of("cirno.isban.tbannedWithoutReason")
                                .add("player", player)
                                .add("datetime", format);
                    }


                } else {
                    if (bannedReason != null && !bannedReason.isEmpty()) {

                        return I18nString.of("cirno.isban.bannedWithReason")
                                .add("player", player)
                                .add("reason", bannedReason);
                    } else {
                        return I18nString.of("cirno.isban.bannedWithoutReason")
                                .add("player", player);
                    }

                }
            } else {
                return I18nString.of("cirno.isban.no").add("player", player);
            }
        }

    }


}
