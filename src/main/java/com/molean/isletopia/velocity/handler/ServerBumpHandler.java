package com.molean.isletopia.velocity.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.MessageHandlerType;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.obj.ServerBumpObject;
import com.molean.isletopia.shared.utils.I18nString;
import com.molean.isletopia.velocity.individual.UniversalChat;

@MessageHandlerType(ServerBumpObject.class)
public class ServerBumpHandler implements MessageHandler<ServerBumpObject> {

    @AutoInject
    UniversalChat universalChat;

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, ServerBumpObject message) {
        String broadcastMessage = "玩家 " + message.getPlayer() +
                " 使用MCBBS账号 " + message.getUser() + " 为服务器顶贴," +
                " 琪露诺决定奖励它: " + String.join(", ", message.getItems());
        universalChat.chatMessage("白", "§bCirnoBot",  I18nString.of(broadcastMessage));
    }
}
