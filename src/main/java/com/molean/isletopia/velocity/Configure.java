package com.molean.isletopia.velocity;

import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Bean;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.Scheduler;

@Bean
public class Configure {


    @AutoInject
    private ProxyServer proxyServer;


    @Bean
    private Scheduler scheduler() {
        return proxyServer.getScheduler();
    }


}
