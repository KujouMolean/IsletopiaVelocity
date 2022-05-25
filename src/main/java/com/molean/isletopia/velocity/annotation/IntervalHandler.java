package com.molean.isletopia.velocity.annotation;

import com.molean.isletopia.shared.ClassResolver;
import com.molean.isletopia.shared.annotations.BeanHandler;
import com.molean.isletopia.shared.annotations.BeanHandlerPriority;
import com.molean.isletopia.shared.annotations.Interval;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.velocitypowered.api.scheduler.Scheduler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@BeanHandlerPriority(0)
public class IntervalHandler implements BeanHandler {
    @Override
    public void handle(Object object) {
        outer:
        for (Method declaredMethod : object.getClass().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(Interval.class)) {
                Interval annotation = declaredMethod.getAnnotation(Interval.class);
                int interval = annotation.value();
                List<Object> parameters = new ArrayList<>();
                for (Parameter parameter : declaredMethod.getParameters()) {
                    Object parameterObject = ClassResolver.INSTANCE.getObject(parameter);
                    if (parameterObject == null) {
                        continue outer;
                    }
                    parameters.add(parameterObject);
                }
                Object plugin = VelocityRelatedUtils.getPlugin();
                Scheduler scheduler = VelocityRelatedUtils.getProxyServer().getScheduler();
                scheduler.buildTask(plugin, () -> {
                    declaredMethod.setAccessible(true);
                    try {
                        declaredMethod.invoke(object, parameters.toArray());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).repeat(Duration.ofMillis(50L * interval));
            }
        }
    }
}
