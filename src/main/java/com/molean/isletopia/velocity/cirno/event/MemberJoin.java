package com.molean.isletopia.velocity.cirno.event;

import  com.molean.isletopia.velocity.cirno.CirnoUtils;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;

public class MemberJoin extends SimpleListenerHost {

    public MemberJoin() {
        CirnoUtils.registerListener(this);

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
