package com.molean.isletopia.velocity.annotation;

import com.molean.isletopia.shared.annotations.BeanHandler;
import com.molean.isletopia.shared.annotations.BeanHandlerPriority;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;

@BeanHandlerPriority
public class ListenerHandler implements BeanHandler {
    @Override
    public void handle(Object object) {
        if (object.getClass().isAnnotationPresent(Listener.class)) {
            Object plugin = VelocityRelatedUtils.getPlugin();
            VelocityRelatedUtils.getProxyServer().getEventManager().register(plugin, object);
        }
    }
}
