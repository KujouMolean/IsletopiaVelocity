package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.database.SkinDao;
import com.molean.isletopia.velocity.annotation.Listener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.util.GameProfile;

import java.sql.SQLException;
import java.util.UUID;


@Listener
public class OnlineModeSwitcher {

    @Subscribe
    public void playerPreLogin(PreLoginEvent event) {
        event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
    }

    @Subscribe
    public void onPreLogin(LoginEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        GameProfile.Property property = event.getPlayer().getGameProfileProperties().get(0);
        String value = property.getValue();
        String signature = property.getSignature();
        try {
            if (!value.equalsIgnoreCase(SkinDao.getSkinValue(uniqueId)) || !signature.equalsIgnoreCase(SkinDao.getSkinSignature(uniqueId))) {
                SkinDao.setSkin(event.getPlayer().getUniqueId(), value, signature);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
