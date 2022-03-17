package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.parameter.HostNameParameter;
import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import net.kyori.adventure.text.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class KickUnsupportedUser {


    public KickUnsupportedUser() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);
    }


    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        String hostName = event.getPlayer().getRemoteAddress().getAddress().getHostName();
        PlayerParameter.set(uniqueId, "hostAddress", hostName);
        String isBannedHostName = HostNameParameter.get(hostName, "isBanned");
        if ("true".equalsIgnoreCase(isBannedHostName)) {
            String reason = HostNameParameter.get(hostName, "bannedReason");
            if (reason == null) {
                reason = "";
            }
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("你已被封禁!" + reason)));

            return;
        }
        String isBanned = PlayerParameter.get(uniqueId, "isBanned");
        if ("true".equalsIgnoreCase(isBanned)) {

            String pardonTime = PlayerParameter.get(uniqueId, "pardonTime");
            if (pardonTime != null && !pardonTime.isEmpty()) {

                long l = Long.parseLong(pardonTime);
                if (System.currentTimeMillis() > l) {
                    PlayerParameter.unset(uniqueId, "isBanned");
                    PlayerParameter.unset(uniqueId, "pardonTime");
                } else {
                    String reason = PlayerParameter.get(uniqueId, "bannedReason");
                    if (reason == null) {
                        reason = "";
                    }
                    LocalDateTime localDateTime = new Timestamp(l).toLocalDateTime();
                    localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
                    String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    event.setResult(ResultedEvent.ComponentResult.denied(Component.text("你已被封禁至" + format + "!" + reason)));
                    return;
                }
            } else {
                String reason = PlayerParameter.get(uniqueId, "bannedReason");
                if (reason == null) {
                    reason = "";
                }
                event.setResult(ResultedEvent.ComponentResult.denied(Component.text("你已被封禁!" + reason)));
                return;
            }
        }
    }

}
