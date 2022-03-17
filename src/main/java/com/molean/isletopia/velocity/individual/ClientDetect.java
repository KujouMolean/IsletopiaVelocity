package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.database.ParameterDao;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerClientBrandEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class ClientDetect {

    public ClientDetect() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);
    }

    @Subscribe
    public void onJoin(PlayerClientBrandEvent event) {
        Player player = event.getPlayer();
        String brand = event.getBrand();
        Optional<ServerConnection> currentServer = event.getPlayer().getCurrentServer();
        if (currentServer.isEmpty()) {
            return;
        }
        String name = currentServer.get().getServerInfo().getName();
        if (name.startsWith("club_")) {
            String s = ParameterDao.get("ClubRealm", name, "RequireVanilla");
            if (s != null && s.equals("true")) {
                if (brand != null && !brand.equals("vanilla")) {
                    player.disconnect(Component.text("【防作弊】该服务器只能使用原版端进入。"));
                }
            }
        }
    }


    @Subscribe
    public void onConnect(ServerConnectedEvent event) {
        String clientBrand = event.getPlayer().getClientBrand();
        RegisteredServer server = event.getServer();
        String name = server.getServerInfo().getName();
        if (name.startsWith("club_")) {
            String s = ParameterDao.get("ClubRealm", name, "RequireVanilla");
            if (s != null && s.equals("true")) {
                if (clientBrand != null && !clientBrand.equals("vanilla")) {
                    event.getPlayer().disconnect(Component.text("【防作弊】该服务器只能使用原版端进入。"));
                }
            }
        }
    }
}
