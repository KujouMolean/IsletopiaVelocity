package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.database.IslandDao;
import com.molean.isletopia.shared.database.MSPTDao;
import com.molean.isletopia.shared.database.UUIDDao;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.platform.PlatformRelatedUtils;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerLogin {

    private static final Map<Player, Long> lastDispatchMap = new HashMap<>();


    public PlayerLogin() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);

        VelocityRelatedUtils.getProxyServer().getScheduler().buildTask(VelocityRelatedUtils.getPlugin(), () -> {
            Optional<RegisteredServer> dispatcher = VelocityRelatedUtils.getProxyServer().getServer("dispatcher");
            if (dispatcher.isEmpty()) {
                return;
            }
            for (Player player : dispatcher.get().getPlayersConnected()) {
                dispatch(player);
            }
        }).repeat(2, TimeUnit.SECONDS).schedule();
    }

    public void dispatch(Player player) {
        long l = System.currentTimeMillis();
        if (l - lastDispatchMap.getOrDefault(player, 0L) < 15 * 1000) {
            return;
        }
        lastDispatchMap.put(player, l);
        if (UUIDDao.query(player.getUniqueId()) != null) {
            UUIDDao.update(player.getUniqueId(), player.getUsername());
        } else {
            UUIDDao.insert(player.getUniqueId(), player.getUsername());
        }
        UUID uuid = player.getUniqueId();
        Integer integer = null;
        try {
            integer = IslandDao.countIslandByPlayer(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String server = null;
        if (integer == null || integer == 0) {
            ArrayList<RegisteredServer> islandServer = new ArrayList<>();
            Map<RegisteredServer, Double> balanceMap = new HashMap<>();
            for (RegisteredServer allServer : VelocityRelatedUtils.getProxyServer().getAllServers()) {
                if (allServer.getServerInfo().getName().startsWith("server")) {
                    islandServer.add(allServer);
                    double balance = 25;
                    try {
                        balance = MSPTDao.queryLastMSPT(allServer.getServerInfo().getName());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    balanceMap.put(allServer, balance);
                }
            }
            PlatformRelatedUtils.getInstance().getLogger().info("==Balance start==");
            PlatformRelatedUtils.getInstance().getLogger().info("Balance Info: " + balanceMap);
            int i = new Random().nextInt(islandServer.size() * islandServer.size());
            int index = (int) Math.floor(Math.sqrt(i));
            islandServer.sort(Comparator.comparingDouble(balanceMap::get));
            Collections.reverse(islandServer);
            RegisteredServer registeredServer = islandServer.get(index);
            PlatformRelatedUtils.getInstance().getLogger().info("Random int: " + i + ", selected " + registeredServer.getServerInfo().getName());
            PlatformRelatedUtils.getInstance().getLogger().info("==Balance end==");
            UniversalParameter.setParameter(uuid, "server", registeredServer.getServerInfo().getName());
            server = registeredServer.getServerInfo().getName();
        }

        String lastServer = UniversalParameter.getParameter(uuid, "lastServer");
        if (lastServer != null) {
            server = lastServer;
        }
        ServerMessageUtils.switchServer(player.getUsername(), server);
    }


    public void joinCheck(Player player) {
        Optional<ServerConnection> currentServer = player.getCurrentServer();
        if (currentServer.isPresent() && !currentServer.get().getServerInfo().getName().equalsIgnoreCase("dispatcher")) {
            dispatch(player);
        }
    }

    @Subscribe
    public void on(ServerConnectedEvent event) throws SQLException {
        Player player = event.getPlayer();
        String serverName = event.getServer().getServerInfo().getName();
        if (serverName.equalsIgnoreCase("dispatcher")) {
            joinCheck(player);
        }
    }

    @Subscribe
    public void onSuccess(ServerConnectedEvent event) {
        Optional<RegisteredServer> previousServer = event.getPreviousServer();
        if (previousServer.isPresent() && previousServer.get().getServerInfo().getName().equals("dispatcher")) {
            lastDispatchMap.remove(event.getPlayer());
        }
    }


}