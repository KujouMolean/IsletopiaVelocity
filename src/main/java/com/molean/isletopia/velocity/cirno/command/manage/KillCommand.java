package com.molean.isletopia.velocity.cirno.command.manage;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;

public class KillCommand implements BotCommandExecutor {
    public KillCommand() {
        CommandHandler.setExecutor("kill", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 1 || !args.get(0).startsWith("server")) {
            return I18nString.of("/kill serverX");
        }
        ProcessHandle.allProcesses().forEach(processHandle -> {
            ProcessHandle.Info info = null;
            try {
                info = processHandle.info();
            } catch (Exception ignore) {
            }
            if (info == null) {
                return;
            }
            if (info.commandLine().isPresent()) {
                String s = info.commandLine().get();
                if (s.contains("-DServerName=" + args.get(0) + " ")) {
                    processHandle.destroyForcibly();
                }
            }
        });
        return I18nString.of("Try killed " + args.get(0) + ", check the status in a minute.");
    }
}
