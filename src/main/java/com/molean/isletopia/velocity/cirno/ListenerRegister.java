package com.molean.isletopia.velocity.cirno;

import  com.molean.isletopia.velocity.cirno.event.GroupMessage;
import  com.molean.isletopia.velocity.cirno.event.MemberJoin;

public class ListenerRegister {
    public ListenerRegister() {
        new GroupMessage();
        new MemberJoin();

    }
}
