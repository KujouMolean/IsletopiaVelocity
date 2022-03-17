package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.util.GameProfile;

public class OnlineModeSwitcher {
    public OnlineModeSwitcher() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);
    }

    @Subscribe
    public void playerPreLogin(PreLoginEvent event) {
        event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
    }

    @Subscribe
    public void onPreLogin(LoginEvent event) {
        String name = event.getPlayer().getUsername();
        GameProfile.Property property = event.getPlayer().getGameProfileProperties().get(0);
        String value = property.getValue();
        String signature = property.getSignature();
        RedisUtils.getCommand().set(name + ":SkinValue", value);
        RedisUtils.getCommand().set(name + ":SkinSignature", signature);
    }
}
