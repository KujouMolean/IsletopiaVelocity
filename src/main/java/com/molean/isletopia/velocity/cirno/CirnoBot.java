package com.molean.isletopia.velocity.cirno;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.utils.PropertiesUtils;
import com.molean.isletopia.velocity.cirno.event.GroupMessage;
import com.molean.isletopia.velocity.cirno.event.MemberJoin;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;

import java.util.*;

public class CirnoBot {
    private static final Map<Long, String> accounts = new HashMap<>();
    private static Bot mainBot = null;
    private static final List<Bot> subBotList = new ArrayList<>();
    private static CirnoHandler CIRNO_HANDLER;


    public static Bot getMainBot() {
        return mainBot;
    }

    public static List<Bot> getSubBotList() {
        return subBotList;
    }

    static {
        Properties config = PropertiesUtils.getProperties("config");
        for (String stringPropertyName : config.stringPropertyNames()) {
            if (stringPropertyName.matches("bot\\.sub\\.[0-9]*\\.qq")) {
                long qq = Long.parseLong(config.getProperty(stringPropertyName));
                String pass = config.getProperty("qq.password." + qq);
                accounts.put(qq, pass);
            }
        }

    }

    public static void init() {
        VelocityRelatedUtils.getProxyServer().getScheduler().buildTask(VelocityRelatedUtils.getPlugin(), () -> {

            Properties config = PropertiesUtils.getProperties("config");
            String qq = config.getProperty("bot.main.qq");
            String pass = config.getProperty("qq.password." + qq);
            mainBot = BotFactory.INSTANCE.newBot(Long.parseLong(qq), pass, new BotConfiguration() {
                {
                    fileBasedDeviceInfo("deviceInfo.json");
                    int playerLimit = VelocityRelatedUtils.getProxyServer().getConfiguration().getShowMaxPlayers();
                    if (playerLimit != 5) {
                        this.setProtocol(MiraiProtocol.ANDROID_PAD);
                    } else {
                        this.setProtocol(MiraiProtocol.ANDROID_PHONE);
                    }
                    autoReconnectOnForceOffline();
                    setReconnectionRetryTimes(Integer.MAX_VALUE);

                }
            });
            mainBot.login();
            new GroupMessage(mainBot);

        }).schedule();


        accounts.forEach((aLong, s) -> {
            VelocityRelatedUtils.getProxyServer().getScheduler().buildTask(VelocityRelatedUtils.getPlugin(), () -> {
                Bot bot = BotFactory.INSTANCE.newBot(aLong, s, new BotConfiguration() {
                    {
                        fileBasedDeviceInfo("deviceInfo.json");
                        int playerLimit = VelocityRelatedUtils.getProxyServer().getConfiguration().getShowMaxPlayers();
                        if (playerLimit != 5) {
                            this.setProtocol(MiraiProtocol.ANDROID_PAD);
                        } else {
                            this.setProtocol(MiraiProtocol.ANDROID_PHONE);
                        }
                    }
                });
                subBotList.add(bot);
                bot.login();
                new MemberJoin(bot);
            }).schedule();
        });
    }

    public static CirnoHandler getCirnoHandler() {
        return CIRNO_HANDLER;
    }

    public static void setCirnoHandler(CirnoHandler cirnoHandler) {
        CIRNO_HANDLER = cirnoHandler;
    }

    public static void destroy() {
        try {
            mainBot.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Bot bot : subBotList) {
            try {
                bot.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
