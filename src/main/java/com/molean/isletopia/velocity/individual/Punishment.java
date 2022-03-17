package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.database.ParameterDao;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Punishment {


    public Punishment() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);
    }

    private static final Map<UUID, BossBar> BOSS_BAR_MAP = new HashMap<>();

    @Subscribe
    @SuppressWarnings("all")
    public void on(ServerPostConnectEvent event) {
        check(event.getPlayer());

    }



    public static void check(Player player) {
        BossBar bossBar;
        if (BOSS_BAR_MAP.get(player.getUniqueId()) != null) {
            bossBar = BOSS_BAR_MAP.get(player.getUniqueId());
        } else {
            bossBar = BossBar.bossBar(Component.text(), BossBar.MAX_PROGRESS, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
            BOSS_BAR_MAP.put(player.getUniqueId(), bossBar);
        }
        player.hideBossBar(bossBar);
        String warning = ParameterDao.get("player", player.getUniqueId().toString(), "warning");
        String severe = ParameterDao.get("player", player.getUniqueId().toString(), "severe");
        if (severe != null && !severe.isEmpty()) {
            long l = Long.parseLong(severe);
            if (l > System.currentTimeMillis()) {
                bossBar.name(Component.text("你受到了严重警告处分，请注意言行。"));
                bossBar.progress((l - System.currentTimeMillis()) / (30 * 24 * 60 * 60 * 1000.0f));
                player.showBossBar(bossBar);
                return;
            }
        }
        if (warning != null && !warning.isEmpty()) {
            long l = Long.parseLong(warning);
            if (l > System.currentTimeMillis()) {
                bossBar.name(Component.text("你受到了警告处分，请注意言行。"));
                bossBar.progress((l - System.currentTimeMillis()) / (7 * 24 * 60 * 60 * 1000.0f));
                player.showBossBar(bossBar);
                return;
            }
        }


    }

}
