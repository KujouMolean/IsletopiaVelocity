package com.molean.isletopia.velocity.cirno.command.group;

import  com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import  com.molean.isletopia.velocity.cirno.CirnoUtils;
import  com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import net.mamoe.mirai.contact.file.AbsoluteFolder;
import net.mamoe.mirai.utils.ExternalResource;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WBanList implements BotCommandExecutor {
    public WBanList() {
        CommandHandler.setExecutor("wbanlist", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) throws Exception {
        String join = String.join("\n", WBan.stringSet);
        String path = "屏蔽词" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")) + ".txt";
        AbsoluteFolder folder = CirnoUtils.getGameGroup().getFiles().getRoot().resolveFolder("失效文件");
        assert folder != null;
        folder.uploadNewFile(path, ExternalResource.create(join.getBytes(StandardCharsets.UTF_8), "txt"));
        return I18nString.of("cirno.wbanlist.upload");
    }

}
