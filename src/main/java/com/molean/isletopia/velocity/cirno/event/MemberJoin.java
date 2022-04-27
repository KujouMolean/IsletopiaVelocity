package com.molean.isletopia.velocity.cirno.event;

import com.molean.isletopia.velocity.cirno.CirnoUtils;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import org.jetbrains.annotations.NotNull;

public class MemberJoin extends SimpleListenerHost {



    public MemberJoin(Bot bot) {
        CirnoUtils.registerListener(bot, this);

    }


    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {

    }

    @EventHandler
    public void onMemberJoinRequest(MemberJoinRequestEvent event) {
        String message = event.getMessage();
        if (message.contains("梦幻之屿")) {
            event.accept();
        } else {
            event.reject(false, "答案错误");
        }
    }
}
