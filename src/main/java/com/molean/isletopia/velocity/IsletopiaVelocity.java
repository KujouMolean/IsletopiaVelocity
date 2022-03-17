package com.molean.isletopia.velocity;

import com.google.inject.Inject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.velocity.cirno.CirnoBot;
import com.molean.isletopia.velocity.cirno.CirnoHandlerImpl;
import com.molean.isletopia.velocity.cirno.CommandsRegister;
import com.molean.isletopia.velocity.handler.HandlerRegister;
import com.molean.isletopia.velocity.individual.*;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.File;
import java.util.Locale;
import java.util.Set;

@Plugin(
        id = "isletopia_velocity",
        name = "IsletopiaVelocity",
        version = "1.0-SNAPSHOT"
)
public class IsletopiaVelocity {


    private final ProxyServer server;
    private final Logger logger;

    private static final Set<String> blacklist = Set.of(
            "IsletopiaShared"
    );

    @Inject
    public IsletopiaVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        VelocityRelatedUtils.proxyServer = server;
        File lib = new File("lib");
        File[] files = lib.listFiles();
        assert files != null;
        outer:
        for (File file : files) {
            for (String s : blacklist) {
                if (file.getName().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                    continue outer;
                }
            }
            if (file.getName().endsWith(".jar")) {
                server.getPluginManager().addToClasspath(this, file.toPath());
            }
        }
        new CommandsRegister();
        if (VelocityRelatedUtils.getProxyServer().getConfiguration().getShowMaxPlayers() != 5)
            CirnoBot.init();
        CirnoBot.setCirnoHandler(new CirnoHandlerImpl());
        new UniversalTell();
        new WelcomeMessage();
        new UniversalChat();
        new ConnectionDetect();
        new KickUnsupportedUser();
        new OnlineModeSwitcher();
        RedisMessageListener.init();
        new HandlerRegister();
        new PlayerInfoBroadcaster();
        new PlayerLogin();
        new IslandCommand();
        new DisableServerCommand();
        new ClientDetect();
        new Punishment();
    }

    @Subscribe
    public void onProxyInitialization(ProxyShutdownEvent event) {
        try {
            RedisMessageListener.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            RedisUtils.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CirnoBot.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
