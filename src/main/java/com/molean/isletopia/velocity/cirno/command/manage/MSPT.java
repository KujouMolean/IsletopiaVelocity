package com.molean.isletopia.velocity.cirno.command.manage;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.shared.database.MSPTDao;
import com.molean.isletopia.shared.platform.PlatformRelatedUtils;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.sql.SQLException;
import java.util.List;

public class MSPT implements BotCommandExecutor {
    public MSPT() {
        CommandHandler.setExecutor("mspt", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1) {
            return I18nString.of("/mspt [server|servers] ");
        }

        String serverName = args.get(0);
        if ("servers".equals(serverName)) {
            double sum = 0;
            int count = 0;
            StringBuilder result = new StringBuilder();
            for (String server : PlatformRelatedUtils.getInstance().getIslandServers()) {
                if (server.startsWith("server")) {
                    double v = -1;
                    try {
                        v = MSPTDao.queryLastMSPT(server);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if (v > 0) {
                        sum+=v;
                        count++;
                        result.append(server).append("(%.2f) ".formatted(v));
                    } else {
                        result.append(server).append("(Unknown) ");
                    }
                }
            }
            if (count != 0) {
                return I18nString.of(result + "\n" + ("Average:%.2f").formatted(sum / count));
            } else {
                return I18nString.of(result.toString());
            }
        } else {
            try {
                double v = MSPTDao.queryLastMSPT(serverName);
                if (v > 0) {
                    return I18nString.of(("MSPT of " + serverName + " is %.2f").formatted(v));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return I18nString.of("Cannot query MSPT of " + serverName);
        }

    }
}

