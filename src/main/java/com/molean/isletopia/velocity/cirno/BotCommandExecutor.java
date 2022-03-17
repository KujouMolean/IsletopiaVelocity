package com.molean.isletopia.velocity.cirno;

import java.util.List;

public interface BotCommandExecutor {
    I18nString execute(long id, List<String> args) throws Exception;
}
