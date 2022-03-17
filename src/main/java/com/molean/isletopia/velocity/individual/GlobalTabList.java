package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.TabListEntry;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.*;

public class GlobalTabList {
    private static final Map<UUID, String> map = new HashMap<>();

    public GlobalTabList() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);
    }


    @Subscribe
    public void on(DisconnectEvent event) {
        quitProxy(event.getPlayer());
    }

    public void joinProxy(Player player) {

        //clear
        for (TabListEntry tabListEntry : new ArrayList<>(player.getTabList().getEntries())) {
            player.getTabList().removeEntry(tabListEntry.getProfile().getId());
        }

        //add to all other player's tab list who are not in club server

        for (RegisteredServer registeredServer : VelocityRelatedUtils.getProxyServer().getAllServers()) {
            if (!registeredServer.getServerInfo().getName().startsWith("club_")) {
                for (Player player1 : registeredServer.getPlayersConnected()) {
                    player1.getTabList().addEntry(TabListEntry.builder()
                            .tabList(player.getTabList())
                            .profile(player.getGameProfile())
                            .build());
                }
            }
        }

        //add all other player to this player's tab list
        for (Player player1 : VelocityRelatedUtils.getProxyServer().getAllPlayers()) {
            player.getTabList().addEntry(TabListEntry.builder()
                    .tabList(player1.getTabList())
                    .profile(player1.getGameProfile())
                    .build());
        }


    }

    public void quitProxy(Player player) {
        //remove from all other player's tab list
        for (Player player1 : VelocityRelatedUtils.getProxyServer().getAllPlayers()) {
            player1.getTabList().removeEntry(player.getUniqueId());
        }
    }

    public void joinClub(Player player, String club) {
        //clear
        for (TabListEntry tabListEntry : new ArrayList<>(player.getTabList().getEntries())) {
            player.getTabList().removeEntry(tabListEntry.getProfile().getId());
        }
        Optional<RegisteredServer> server = VelocityRelatedUtils.getProxyServer().getServer(club);
        if (server.isEmpty()) {
            return;
        }
        for (Player player1 : server.get().getPlayersConnected()) {
            player1.getTabList().addEntry(TabListEntry.builder()
                    .tabList(player.getTabList())
                    .profile(player.getGameProfile())
                    .build());

            player.getTabList().addEntry(TabListEntry.builder()
                    .tabList(player1.getTabList())
                    .profile(player1.getGameProfile())
                    .build());
        }


    }

    public void quitClub(Player player, String club) {
        Optional<RegisteredServer> server = VelocityRelatedUtils.getProxyServer().getServer(club);
        if (server.isEmpty()) {
            return;
        }
        for (Player player1 : server.get().getPlayersConnected()) {
            player1.getTabList().removeEntry(player.getUniqueId());
        }

        for (TabListEntry entry : new ArrayList<>(player.getTabList().getEntries())) {
            player.getTabList().removeEntry(entry.getProfile().getId());
        }
        joinProxy(player);
    }

    @Subscribe
    public void on(ServerConnectedEvent event) {
        RegisteredServer server = event.getServer();
        Optional<RegisteredServer> previousServer = event.getPreviousServer();
        if (previousServer.isPresent() && previousServer.get().getServerInfo().getName().startsWith("club_")) {
            quitClub(event.getPlayer(), previousServer.get().getServerInfo().getName());
        }
        if (server.getServerInfo().getName().startsWith("club_")) {
            joinClub(event.getPlayer(), server.getServerInfo().getName());
        }
    }
}
