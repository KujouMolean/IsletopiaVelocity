package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.parameter.Parameter;
import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.velocity.annotation.Listener;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import net.kyori.adventure.text.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Listener
public class KickUnsupportedUser {
    @AutoInject
    private PlayerParameter playerParameter;

    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        String hostName = event.getPlayer().getRemoteAddress().getAddress().getHostName();
        playerParameter.set(uniqueId, "hostAddress", hostName);
        if (Parameter.of("HostName").get(hostName).getAsBoolean("isBanned")) {
            String reason = Parameter.of("HostName").get(hostName).getAsString("bannedReason");
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("你已被封禁!" + reason)));
            return;
        }
        String isBanned = playerParameter.get(uniqueId, "isBanned");
        if ("true".equalsIgnoreCase(isBanned)) {
            String pardonTime = playerParameter.get(uniqueId, "pardonTime");
            if (pardonTime != null && !pardonTime.isEmpty()) {
                long l = Long.parseLong(pardonTime);
                if (System.currentTimeMillis() > l) {
                    playerParameter.unset(uniqueId, "isBanned");
                    playerParameter.unset(uniqueId, "pardonTime");
                } else {
                    String reason = playerParameter.get(uniqueId, "bannedReason");
                    if (reason == null) {
                        reason = "";
                    }
                    LocalDateTime localDateTime = new Timestamp(l).toLocalDateTime();
                    localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
                    String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    event.setResult(ResultedEvent.ComponentResult.denied(Component.text("你已被封禁至" + format + "!" + reason)));
                }
            } else {
                String reason = playerParameter.get(uniqueId, "bannedReason");
                if (reason == null) {
                    reason = "";
                }
                event.setResult(ResultedEvent.ComponentResult.denied(Component.text("你已被封禁!" + reason)));
            }
        }
    }

}
