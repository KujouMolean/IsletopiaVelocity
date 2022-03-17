package com.molean.isletopia.velocity.handler;

public class HandlerRegister {
    public HandlerRegister() {
        new ServerBumpHandler();
        new CommonResponseHandler();
        new SwitchServerHandler();
    }
}
