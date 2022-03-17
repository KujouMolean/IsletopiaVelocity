package com.molean.isletopia.velocity.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.velocity.cirno.CirnoUtils;
import com.molean.isletopia.velocity.cirno.I18nString;

public class CommonResponseHandler implements MessageHandler<CommonResponseObject> {
    public CommonResponseHandler() {
        RedisMessageListener.setHandler("CommonResponse", this, CommonResponseObject.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, CommonResponseObject message) {
        CirnoUtils.broadcastMessage(I18nString.of(message.getMessage()));

    }
}
