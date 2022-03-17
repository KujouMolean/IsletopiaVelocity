package com.molean.isletopia.velocity.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.obj.ServerBumpObject;
import com.molean.isletopia.velocity.cirno.CirnoUtils;
import com.molean.isletopia.velocity.cirno.I18nString;

public class ServerBumpHandler implements MessageHandler<ServerBumpObject> {
    public ServerBumpHandler() {
        RedisMessageListener.setHandler("ServerBump", this, ServerBumpObject.class);
    }


    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, ServerBumpObject message) {
        String broadcastMessage = "玩家 " + message.getPlayer() +
                " 使用MCBBS账号 " + message.getUser() + " 为服务器顶贴," +
                " 琪露诺决定奖励它: " + String.join(", ", message.getItems());
        CirnoUtils.broadcastMessage(I18nString.of(broadcastMessage));
    }
}
