package com.molean.isletopia.velocity.cirno;

import net.mamoe.mirai.message.data.MessageChain;

public interface CirnoHandler {
    void handler(long id, String nameCard, String plainMessage, MessageChain messageChain);
}
