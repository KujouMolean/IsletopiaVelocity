package com.molean.isletopia.velocity.cirno.command.manage;

import com.molean.isletopia.shared.database.ParameterDao;
import com.molean.isletopia.shared.parameter.ContactParameter;
import com.molean.isletopia.shared.parameter.GroupParameter;
import com.molean.isletopia.shared.parameter.HostNameParameter;
import com.molean.isletopia.shared.parameter.PlayerParameter;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ParameterCommand implements BotCommandExecutor {
    public ParameterCommand() {
        CommandHandler.setExecutor("parameter", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {
        if (args.size() < 4) {
            return I18nString.of("用法: /parameter item target operator key [value]");
        }
        String item = args.get(0);
        String target = args.get(1);
        String operator = args.get(2);
        String key = args.get(3);

        switch (item.toLowerCase(Locale.ROOT)) {
            case "player" -> {
                UUID uuid = UUIDManager.get(target);
                if (uuid == null) {
                    uuid = UUIDManager.getOnlineSync(target);
                }
                if (uuid == null) {
                    return I18nString.of("不存在该用户名的正版账号。");
                }
                switch (operator.toLowerCase(Locale.ROOT)) {
                    case "set" -> {
                        if (args.size() < 5) {
                            return I18nString.of("用法: /parameter item target set key value");
                        } else {
                            String value = args.get(4);
                            PlayerParameter.set(uuid, key, value);
                            return I18nString.of("OK");
                        }
                    }
                    case "view" -> {
                        String s = PlayerParameter.get(uuid, key);
                        return I18nString.of(s == null ? "null" : s);
                    }
                    case "unset" -> {
                        PlayerParameter.unset(uuid, key);
                        return I18nString.of("OK");
                    }
                }
            }

            case "hostname" -> {
                switch (operator.toLowerCase(Locale.ROOT)) {
                    case "set" -> {
                        if (args.size() < 5) {
                            return I18nString.of("用法: /parameter item target set key value");
                        } else {
                            String value = args.get(4);
                            HostNameParameter.set(target, key, value);
                            return I18nString.of("OK");
                        }
                    }
                    case "view" -> {
                        String s = HostNameParameter.get(target, key);
                        return I18nString.of(s == null ? "null" : s);
                    }
                    case "unset" -> {
                        HostNameParameter.unset(target, key);
                        return I18nString.of("OK");
                    }
                }

            }
            case "contact" -> {

                if (target.startsWith("@")) {
                    target = target.substring(1);
                }

                long l;
                try {
                    l = Long.parseLong(target);
                } catch (Exception e) {
                    return I18nString.of("用法: /parameter item targetID set key value");
                }


                switch (operator.toLowerCase(Locale.ROOT)) {
                    case "set" -> {
                        if (args.size() < 5) {
                            return I18nString.of("用法: /parameter item target set key value");
                        } else {
                            String value = args.get(4);
                            ContactParameter.set(l, key, value);
                            return I18nString.of("OK");
                        }
                    }
                    case "view" -> {
                        String s = ContactParameter.get(l, key);
                        return I18nString.of(s == null ? "null" : s);
                    }
                    case "unset" -> {
                        ContactParameter.unset(l, key);
                        return I18nString.of("OK");
                    }
                }

            }
            case "group" -> {
                switch (operator.toLowerCase(Locale.ROOT)) {
                    case "set" -> {
                        if (args.size() < 5) {
                            return I18nString.of("用法: /parameter item target set key value");
                        } else {
                            String value = args.get(4);
                            GroupParameter.set(target, key, value);
                            return I18nString.of("OK");
                        }
                    }
                    case "view" -> {
                        String s = GroupParameter.get(target, key);
                        return I18nString.of(s == null ? "null" : s);
                    }
                    case "unset" -> {
                        GroupParameter.unset(target, key);
                        return I18nString.of("OK");
                    }
                }
            }
            case "universal" -> {
                switch (operator.toLowerCase(Locale.ROOT)) {
                    case "set" -> {
                        if (args.size() < 5) {
                            return I18nString.of("用法: /parameter item target set key value");
                        } else {
                            String value = args.get(4);
                            UniversalParameter.setParameter(UUIDManager.get(target), key, value);

                            return I18nString.of("OK");
                        }
                    }
                    case "view" -> {
                        String s = UniversalParameter.getParameter(UUIDManager.get(target), key);
                        return I18nString.of(s == null ? "null" : s);
                    }
                    case "unset" -> {
                        UniversalParameter.unsetParameter(UUIDManager.get(target), key);
                        return I18nString.of("OK");
                    }
                }
            }
            default -> {
                switch (operator.toLowerCase(Locale.ROOT)) {
                    case "set" -> {
                        if (args.size() < 5) {
                            return I18nString.of("用法: /parameter item target set key value");
                        } else {
                            String value = args.get(4);
                            ParameterDao.set(item, target, key, value);
                            return I18nString.of("OK");
                        }
                    }
                    case "view" -> {
                        String s = ParameterDao.get(item, target, key);
                        return I18nString.of(s == null ? "null" : s);
                    }
                    case "unset" -> {
                        ParameterDao.delete(item, target, key);
                        return I18nString.of("OK");
                    }
                }
            }
        }

        return I18nString.of("用法: /parameter item target operator key [value]");
    }
}
