package com.molean.isletopia.velocity;

import com.google.inject.Inject;
import com.molean.isletopia.shared.ClassResolver;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.service.RedisService;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.io.File;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

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


    public void initLibrary() {
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
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws Exception {
        VelocityRelatedUtils.proxyServer = server;
        VelocityRelatedUtils.logger = logger;

        initLibrary();

        ClassResolver.INSTANCE.addBean(server);
        ClassResolver.INSTANCE.addBean(this);
        ClassResolver.INSTANCE.addBean(server);

        ClassResolver.INSTANCE.loadClass();
        ClassResolver.INSTANCE.resolveBean();
        ClassResolver.INSTANCE.resolveFieldInject();
    }

    @Subscribe
    public void on(ProxyShutdownEvent event) {
        DisableTasks object = ClassResolver.INSTANCE.getObject(DisableTasks.class);
        assert object != null;
        for (Runnable task : object.getTasks()) {
            task.run();
        }
    }
}
