package com.molean.isletopia.velocity.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Bean;
import com.molean.isletopia.shared.annotations.MessageHandlerType;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;

import com.molean.isletopia.shared.utils.I18nString;
import com.molean.isletopia.velocity.individual.UniversalChat;

@MessageHandlerType(CommonResponseObject.class)
public class CommonResponseHandler implements MessageHandler<CommonResponseObject> {

    @AutoInject
    private UniversalChat universalChat;

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, CommonResponseObject responseObject) {
        var message = I18nString.of(responseObject.getMessage());
        universalChat.chatMessage("白", "§bCirnoBot", message);

    }
}
