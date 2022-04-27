package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.database.IslandDao;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.pojo.obj.PlaySoundObject;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.shared.utils.*;
import com.molean.isletopia.velocity.MessageUtils;
import com.molean.isletopia.velocity.cirno.CirnoUtils;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UniversalChat {
    final public static Map<String, Set<String>> collections = new HashMap<>();
    final public static Map<String, String> commandMapping = new HashMap<>();
    final public static Map<UUID, List<Pair<String, Long>>> playerChats = new HashMap<>();
    private static long lastTrim = 0L;

    public void addRecord(UUID uuid, String msg) {
        if (!playerChats.containsKey(uuid)) {
            playerChats.put(uuid, new ArrayList<>());
        }
        List<Pair<String, Long>> pairs = playerChats.get(uuid);
        pairs.add(Pair.of(msg, System.currentTimeMillis()));
    }

    public int previousSimilar(UUID uuid, String msg) {
        int cnt = 0;

        List<String> recordsRecent30s = getRecordsRecent30s(uuid);
        for (String recordsRecent30 : recordsRecent30s) {
            if (StringUtils.Levenshtein(msg, recordsRecent30) > 0.5) {
                cnt++;
            }
        }
        return cnt;
    }

    public List<String> getRecordsRecent30s(UUID uuid) {
        ArrayList<String> strings = new ArrayList<>();
        List<Pair<String, Long>> pairs = playerChats.get(uuid);
        if (pairs == null || pairs.isEmpty()) {

            return strings;
        }
        for (Pair<String, Long> pair : pairs) {
            if (pair.getValue() > System.currentTimeMillis() - 30 * 1000) {
                strings.add(pair.getKey());
            }
        }
        return strings;
    }

    public void trim() {
        if (System.currentTimeMillis() - lastTrim < 30 * 1000) {
            return;
        }
        lastTrim = System.currentTimeMillis();

        ArrayList<UUID> tobeRemove = new ArrayList<>();


        for (UUID uuid : playerChats.keySet()) {
            List<Pair<String, Long>> pairs = playerChats.get(uuid);
            pairs.removeIf(stringLongPair -> stringLongPair.getValue() < System.currentTimeMillis() - 30 * 1000);
            if (pairs.isEmpty()) {
                tobeRemove.add(uuid);
            }
        }

        for (UUID uuid : tobeRemove) {
            playerChats.remove(uuid);
        }
    }

    @Subscribe
    public void on(PlayerChatEvent event) {
        if (event.getMessage().startsWith("/")) {
            return;
        }

        Optional<ServerConnection> currentServer = event.getPlayer().getCurrentServer();
        if (currentServer.isEmpty()) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            return;
        }

        if (currentServer.get().getServerInfo().getName().startsWith("club_")) {
            return;
        }

        String p = event.getPlayer().getUsername();
        String m = event.getMessage();
        trim();

        if (previousSimilar(event.getPlayer().getUniqueId(), m) >= 2) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            MessageUtils.warn(event.getPlayer(), "不要刷屏!");
            return;
        }

        addRecord(event.getPlayer().getUniqueId(), m);
        List<String> channels = ChatChannel.getChannels(event.getPlayer().getUniqueId());
        for (String channel : channels) {
            UniversalChat.chatMessage(channel, p, I18nString.of(m));
            if (channel.equalsIgnoreCase("白")) {
                CirnoUtils.groupMessage("<" + p + "> " + m);
            }
        }
        event.setResult(PlayerChatEvent.ChatResult.denied());
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        String name = event.getPlayer().getUsername();
        for (Player player : VelocityRelatedUtils.getProxyServer().getAllPlayers()) {
            if (collections.containsKey(player.getUsername()) && collections.get(player.getUsername()).contains(name)) {
                player.sendActionBar(Component.text("§7" + name + "下线了"));
            }
        }

    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        String name = event.getPlayer().getUsername();
        for (Player player : VelocityRelatedUtils.getProxyServer().getAllPlayers()) {
            if (collections.containsKey(player.getUsername()) && collections.get(player.getUsername()).contains(name)) {
                player.sendActionBar(Component.text("§a" + name + "上线了"));
            }
        }
        String channel = UniversalParameter.getParameter(event.getPlayer().getUniqueId(), "Channel");
        if (channel == null || channel.isEmpty()) {
            channel = "白";
        }

    }


    public UniversalChat() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);
        VelocityRelatedUtils.getProxyServer().getScheduler().buildTask(VelocityRelatedUtils.getPlugin(), () -> {
            for (Player player : VelocityRelatedUtils.getProxyServer().getAllPlayers()) {
                if (RedisUtils.getCommand().exists("Collection-" + player.getUsername()) > 0) {
                    String s = RedisUtils.getCommand().get("Collection-" + player.getUsername());
                    if (s != null && !s.isEmpty()) {
                        String[] split = s.split(",");
                        HashSet<String> strings = new HashSet<>(Arrays.asList(split));
                        collections.put(player.getUsername(), strings);
                    }
                }
            }
        }).repeat(5, TimeUnit.SECONDS).schedule();

        commandMapping.put("wiki", "url#https://wiki.islet.world/");
        commandMapping.put("Wiki", "url#https://wiki.islet.world/");
        commandMapping.put("指南", "url#https://wiki.islet.world/");

        commandMapping.put("新合成表", "cmd#/menu recipe");

        commandMapping.put("FAQ", "url#https://wiki.islet.world/faq.html");
        commandMapping.put("faq", "url#https://wiki.islet.world/faq.html");
        commandMapping.put("常见问答", "url#https://wiki.islet.world/faq.html");

        commandMapping.put("游戏规则", "url#https://wiki.islet.world/guide/rules.html");

        commandMapping.put("魔改内容", "url#https://wiki.islet.world/guide/modification.html");

        commandMapping.put("特殊机制", "url#https://wiki.islet.world/guide/mechanism.html");
        commandMapping.put("特色系统", "url#https://wiki.islet.world/feature.html");
        commandMapping.put("社团制度", "url#https://wiki.islet.world/club.html");
        commandMapping.put("社团系统", "url#https://wiki.islet.world/club.html");

        commandMapping.put("活动", "url#https://wiki.islet.world/activities/");
        commandMapping.put("皮肤站", "url#http://skin.molean.com/");
        commandMapping.put("捐助", "url#https://afdian.net/@molean");
        commandMapping.put("MinecraftWiki", "url#https://minecraft.fandom.com/zh/wiki/Minecraft_Wiki");


        commandMapping.put("群号", "url#http://wiki.islet.world/introduction.html#%E4%B8%80%E8%B5%B7%E7%95%85%E8%81%8A");
        commandMapping.put("QQ群", "url#http://wiki.islet.world/introduction.html#%E4%B8%80%E8%B5%B7%E7%95%85%E8%81%8A");
        commandMapping.put("qq群", "url#http://wiki.islet.world/introduction.html#%E4%B8%80%E8%B5%B7%E7%95%85%E8%81%8A");
        commandMapping.put("企鹅群", "url#http://wiki.islet.world/introduction.html#%E4%B8%80%E8%B5%B7%E7%95%85%E8%81%8A");

        commandMapping.put("一号一岛制度", "url#http://wiki.islet.world/feature.html#%E4%B8%80%E5%8F%B7%E4%B8%80%E5%B2%9B%E5%88%B6%E5%BA%A6");
        commandMapping.put("多区负载均衡", "url#http://wiki.islet.world/feature.html#%E5%A4%9A%E5%8C%BA%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1");
        commandMapping.put("自由生物群系", "url#http://wiki.islet.world/feature.html#%E8%87%AA%E7%94%B1%E7%94%9F%E7%89%A9%E7%BE%A4%E7%B3%BB");
        commandMapping.put("岛屿独立备份", "url#http://wiki.islet.world/feature.html#%E5%B2%9B%E5%B1%BF%E7%8B%AC%E7%AB%8B%E5%A4%87%E4%BB%BD");
        commandMapping.put("原版内容轻改", "url#http://wiki.islet.world/feature.html#%E5%8E%9F%E7%89%88%E5%86%85%E5%AE%B9%E8%BD%BB%E6%94%B9");
        commandMapping.put("社团自治制度", "url#http://wiki.islet.world/feature.html#%E7%A4%BE%E5%9B%A2%E8%87%AA%E6%B2%BB%E5%88%B6%E5%BA%A6");
        commandMapping.put("化繁为简的设计理念", "url#http://wiki.islet.world/feature.html#%E5%8C%96%E7%B9%81%E4%B8%BA%E7%AE%80%E7%9A%84%E8%AE%BE%E8%AE%A1%E7%90%86%E5%BF%B5");
        commandMapping.put("TSI", "url#http://wiki.islet.world/club.html#_1-%E4%BC%8A%E7%A2%A7%E5%A1%94%E6%96%AF%E7%9A%84%E5%91%BC%E5%94%A4");
        commandMapping.put("伊碧塔斯的呼唤", "url#http://wiki.islet.world/club.html#_1-%E4%BC%8A%E7%A2%A7%E5%A1%94%E6%96%AF%E7%9A%84%E5%91%BC%E5%94%A4");
        commandMapping.put("幻想乡", "url#http://wiki.islet.world/club.html#_2-%E5%B9%BB%E6%83%B3%E4%B9%A1");
        commandMapping.put("Gensokyo", "url#http://wiki.islet.world/club.html#_2-%E5%B9%BB%E6%83%B3%E4%B9%A1");
        commandMapping.put("学习激励计划", "url#http://wiki.islet.world/activities/studybump.html");
        commandMapping.put("顶贴", "url#https://wiki.islet.world/activities/bump.html");
        commandMapping.put("顶帖", "url#https://wiki.islet.world/activities/bump.html");
        commandMapping.put("雕塑活动", "url#https://wiki.islet.world/activities/sculpture.html");
        commandMapping.put("小游戏设计大赛", "url#https://wiki.islet.world/activities/2021-07-20.html");
        commandMapping.put("鞘翅", "url#https://wiki.islet.world/faq.html#%E6%88%91%E8%AF%A5%E5%A6%82%E4%BD%95%E8%8E%B7%E5%8F%96%E9%9E%98%E7%BF%85");
        commandMapping.put("信标", "url#https://wiki.islet.world/faq.html#%E6%88%91%E8%AF%A5%E5%A6%82%E4%BD%95%E8%8E%B7%E5%8F%96%E4%BF%A1%E6%A0%87");
        commandMapping.put("泥土", "url#https://wiki.islet.world/faq.html#%E6%88%91%E8%AF%A5%E5%A6%82%E4%BD%95%E8%8E%B7%E5%8F%96%E6%B3%A5%E5%9C%9F");
        commandMapping.put("珊瑚", "url#https://wiki.islet.world/faq.html#%E6%88%91%E8%AF%A5%E5%A6%82%E4%BD%95%E8%8E%B7%E5%8F%96%E7%8F%8A%E7%91%9A");
        commandMapping.put("去别人岛", "url#https://wiki.islet.world/faq.html#%E6%88%91%E8%AF%A5%E5%A6%82%E4%BD%95%E8%AE%BF%E9%97%AE%E5%90%8C%E4%BC%B4%E5%B2%9B%E5%B1%BF");
        commandMapping.put("合作", "url#https://wiki.islet.world/faq.html#%E6%88%91%E8%AF%A5%E5%A6%82%E4%BD%95%E4%B8%8E%E5%90%8C%E4%BC%B4%E5%90%88%E4%BD%9C");
        commandMapping.put("红石", "url#https://wiki.islet.world/faq.html#%E6%88%91%E8%AF%A5%E5%A6%82%E4%BD%95%E8%8E%B7%E5%8F%96%E7%BA%A2%E7%9F%B3");
        commandMapping.put("村民", "url#https://wiki.islet.world/faq.html#%E6%88%91%E8%AF%A5%E5%A6%82%E4%BD%95%E8%8E%B7%E5%8F%96%E6%9D%91%E6%B0%91");
        commandMapping.put("铁锭", "url#https://wiki.islet.world/faq.html#%E6%88%91%E8%AF%A5%E5%A6%82%E4%BD%95%E8%8E%B7%E5%8F%96%E9%93%81-%E9%87%91-%E4%B8%8B%E5%B1%8A%E5%90%88%E9%87%91");
        commandMapping.put("新手教程", "url#https://various-monkey-519.notion.site/94ddc5dc458a449aa0033376d833fe83");
        commandMapping.put("新手教学", "url#https://various-monkey-519.notion.site/94ddc5dc458a449aa0033376d833fe83");
        commandMapping.put("怎么玩", "url#https://various-monkey-519.notion.site/94ddc5dc458a449aa0033376d833fe83");
    }


    private static final Map<String, List<Pair<String, Long>>> playerChatDataMap = new HashMap<>();


    public static void chatMessage(String channel, String sourcePlayer, I18nString i18nString) {

        ArrayList<Player> targetPlayers = ChatChannel.getPlayersInChannel(channel).stream()
                .map(uuid -> VelocityRelatedUtils.getProxyServer().getPlayer(uuid)).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toCollection(ArrayList::new));
        String color = ChatChannel.getChannelColor(channel);
        ArrayList<Player> targetPlayerList = new ArrayList<>(targetPlayers);
        targetPlayerList.sort((o1, o2) -> o2.getUsername().length() - o1.getUsername().length());

        ConcurrentHashMap<Integer, Island> idCache = new ConcurrentHashMap<>();
        ConcurrentHashMap<IslandId, Island> islandIdCache = new ConcurrentHashMap<>();
        ConcurrentHashMap<Locale, String> displayCache = new ConcurrentHashMap<>();

        UUID uuid = UUIDManager.get(sourcePlayer);

        for (Player targetPlayer : targetPlayers) {
            String rawMessage = I18n.getMessage(targetPlayer.getPlayerSettings().getLocale(), i18nString.getNode());
            for (Pair<String, String> stringStringPair : i18nString.getPairList()) {
                rawMessage = rawMessage.replaceAll("<" + stringStringPair.getKey() + ">", stringStringPair.getValue());
            }
            for (Pair<String, Function<Locale, String>> stringFunctionPair : i18nString.getDelayedList()) {
                rawMessage = rawMessage.replaceAll("<" + stringFunctionPair.getKey() + ">", stringFunctionPair.getValue().apply(targetPlayer.getPlayerSettings().getLocale()));
            }
            Set<String> collections = UniversalChat.collections.get(targetPlayer.getUsername());
            boolean subscribe = collections != null && collections.contains(sourcePlayer.replaceAll("§.", ""));

            String finalRawMessage = rawMessage;
            VelocityRelatedUtils.getInstance().runAsync(() -> {
                chatMessageToPlayer(sourcePlayer, uuid, targetPlayer, finalRawMessage, color, subscribe, targetPlayerList, idCache, islandIdCache, displayCache);
            });
        }

    }

    public static void chatMessageToPlayer(String sourcePlayer, UUID sourcePlayerUUID, Player target, String message, String color, boolean subscribe, ArrayList<Player> targetPlayerListWithLengthDescOrder, Map<Integer, Island> idCache, Map<IslandId, Island> islandIdCache, Map<Locale, String> playerDisplayCache) {

        Locale locale = target.getPlayerSettings().getLocale();
        //遍历文本，进行特殊替换。
        TextComponent.Builder mainText = Component.text();
        int i = 0;
        int j = 0;
        outer:
        while (j < message.length()) {
            String substring = message.substring(j);
            String substringLower = substring.toLowerCase(Locale.ROOT);

            if (substringLower.startsWith("{")) {
                //如果是图片，则用"[图片]"方式显示，并添加超链接下划线
                Pattern pattern = Pattern.compile("\\{\\{\\{url#(.*?)#(.*?)}}}");
                Matcher matcher = pattern.matcher(substring);
                if (matcher.find()) {
                    String group = matcher.group();
                    String text = matcher.group(1);
                    String url = matcher.group(2);
                    TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                    mainText.append(subMessageComponent);
                    TextComponent textComponent = Component.text(color + "§n" + text + "§r" + color);
                    textComponent = textComponent.clickEvent(ClickEvent.openUrl(url));
                    mainText.append(textComponent);
                    j += group.length();
                    i = j;
                    continue;
                }
            }

            //首先判定在线玩家，优先匹配长ID
            for (Player player : targetPlayerListWithLengthDescOrder) {
                //确定子文本
                if (substring.startsWith(player.getUsername()) || substring.startsWith(player.getUsername().replaceAll("#", ""))) {
                    //匹配到了玩家，先把ID前的文本添加。
                    TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                    mainText.append(subMessageComponent);

                    //把玩家名异色处理,然后复原颜色
                    TextComponent textComponent = Component.text("§7" + player.getUsername() + "§r" + color);
                    //添加点击指令
                    String cmd = "/player " + player.getUsername();
                    textComponent = textComponent.clickEvent(ClickEvent.runCommand(cmd));
                    if (sourcePlayerUUID != null) {
                        if (playerDisplayCache.containsKey(locale)) {
                            textComponent = textComponent.hoverEvent(HoverEvent.showText(Component.text(playerDisplayCache.get(locale))));
                        } else {
                            String display = PlayerUtils.getDisplay(sourcePlayerUUID);
                            playerDisplayCache.put(locale, display);
                            textComponent = textComponent.hoverEvent(HoverEvent.showText(Component.text(playerDisplayCache.get(locale))));
                        }

                    }

                    mainText.append(textComponent);

                    //提示该玩家被At了
                    if (player.getUniqueId().equals(target.getUniqueId())) {
                        notify(player.getUsername());
                    }

                    //跳转到下一个匹配位置，并停止当前位置的后续匹配
                    j += player.getUsername().length();
                    i = j;
                    continue outer;
                }
            }


            //匹配关键字，添加超链接与下划线
            for (String s : commandMapping.keySet()) {
                if (substring.startsWith(s)) {
                    //匹配到了，添加之前的文本
                    TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                    mainText.append(subMessageComponent);

                    //添加下划线与超链接
                    TextComponent textComponent = Component.text(color + "§n" + s + "§r" + color);
                    String cmd = commandMapping.get(s);
                    ArrayList<String> strings = new ArrayList<>(Arrays.asList(cmd.split("#")));
                    String operate = strings.get(0).toLowerCase(Locale.ROOT);
                    strings.remove(0);
                    String value = String.join("#", strings);

                    switch (operate) {
                        case "url" -> textComponent = textComponent.clickEvent(ClickEvent.openUrl(value));
                        case "cmd" -> textComponent = textComponent.clickEvent(ClickEvent.runCommand(value));
                        case "suggest" -> textComponent = textComponent.clickEvent(ClickEvent.suggestCommand(value));
                    }


                    //添加到总文本中
                    mainText.append(textComponent);

                    //跳转到下一个匹配位置，并停止当前位置的后续匹配
                    j += s.length();
                    i = j;
                    continue outer;
                }
            }

            if (substringLower.startsWith("#")) {
                //如果是岛屿链接 提示访问

                Pattern pattern = Pattern.compile("^#([0-9]+)");
                Matcher matcher = pattern.matcher(substring);
                if (matcher.find()) {
                    String group = matcher.group();

                    int island_id = Integer.parseInt(matcher.group(1));
                    Island islandById = null;
                    if (idCache.containsKey(island_id)) {
                        islandById = idCache.get(island_id);
                    } else {
                        try {
                            islandById = IslandDao.getIslandById(island_id);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (islandById != null) {
                        String name = islandById.getName();
                        String display;
                        display = Objects.requireNonNullElseGet(name, () -> "#" + island_id);
                        TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                        mainText.append(subMessageComponent);
                        TextComponent textComponent = Component.text(color + "§n" + display + "§r" + color);
                        String cmd = "/visit " + group;
                        textComponent = textComponent.clickEvent(ClickEvent.suggestCommand(cmd));
                        textComponent = textComponent.hoverEvent(Component.text(IslandUtils.getDisplayInfo(target.getPlayerSettings().getLocale(), islandById)));
                        mainText.append(textComponent);
                        j += group.length();
                        i = j;
                        continue;
                    }


                }
            }

            //匹配超链接
            if (substringLower.startsWith("server")) {
                Pattern pattern = Pattern.compile("^(server[0-9]+),(-?[0-9]+),(-?[0-9]+)");
                Matcher matcher = pattern.matcher(substring);
                if (matcher.find()) {
                    String group = matcher.group();

                    String server = matcher.group(1);
                    int x = Integer.parseInt(matcher.group(2));
                    int z = Integer.parseInt(matcher.group(3));
                    IslandId islandId = new IslandId(server, x, z);
                    Island islandById = null;
                    if (islandIdCache.containsKey(islandId)) {
                        islandById = islandIdCache.get(islandId);
                    } else {
                        try {
                            islandById = IslandDao.getIslandByIslandId(islandId);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (islandById != null) {

                        String name = islandById.getName();
                        String display = Objects.requireNonNullElseGet(name, () -> "#" + group);
                        TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                        mainText.append(subMessageComponent);
                        TextComponent textComponent = Component.text(color + "§n" + display + "§r" + color);
                        String cmd = "/visit " + matcher.group(1) + " " + matcher.group(2) + " " + matcher.group(3);
                        textComponent = textComponent.clickEvent(ClickEvent.suggestCommand(cmd));
                        textComponent = textComponent.hoverEvent(Component.text(IslandUtils.getDisplayInfo(target.getPlayerSettings().getLocale(), islandById)));
                        mainText.append(textComponent);
                        j += group.length();
                        i = j;
                        continue;
                    }


                }
            }

            //匹配超链接
            if (substringLower.startsWith("https://") || substringLower.startsWith("http://")) {

                //如果是B站bv超链接，则自动替换为精简串，并添加下划线与超链接

                Pattern pattern = Pattern.compile("^https?://www.bilibili.com/video/(BV[0-9a-zA-Z]+).*");
                Matcher matcher = pattern.matcher(substring);
                if (matcher.find()) {
                    String group = matcher.group();
                    TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                    mainText.append(subMessageComponent);
                    TextComponent textComponent = Component.text(color + "§n" + group + "§r" + color);
                    String cmd = "/visit " + group;
                    textComponent = textComponent.clickEvent(ClickEvent.suggestCommand(cmd));
                    mainText.append(textComponent);
                    j += group.length();
                    i = j;
                    continue;
                }
            }


            if (substringLower.startsWith("https://") || substringLower.startsWith("http://")) {
                //如果是普通超链接，则添加下划线与超链接
                Pattern pattern = Pattern.compile("^https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
                Matcher matcher = pattern.matcher(substring);
                if (matcher.find()) {
                    String group = matcher.group();
                    TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                    mainText.append(subMessageComponent);
                    TextComponent textComponent = Component.text(color + "§n" + group + "§r" + color);
                    textComponent = textComponent.clickEvent(ClickEvent.openUrl(group));
                    mainText.append(textComponent);
                    j += group.length();
                    i = j;
                    continue;
                }
            }


            //av
            if (substringLower.startsWith("av")) {
                Pattern pattern = Pattern.compile("^[aA][vV][0-9]+");
                Matcher matcher = pattern.matcher(substring);
                if (matcher.find()) {
                    String group = matcher.group();
                    TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                    mainText.append(subMessageComponent);
                    TextComponent textComponent = Component.text(color + "§n" + group + "§r" + color);
                    String url = "https://www.bilibili.com/video/" + group;
                    textComponent = textComponent.clickEvent(ClickEvent.openUrl(url));
                    mainText.append(textComponent);
                    j += group.length();
                    i = j;
                    continue;
                }
            }

            //bv
            if (substringLower.startsWith("bv")) {
                Pattern pattern = Pattern.compile("^[bB][vV][0-9a-zA-Z]+");
                Matcher matcher = pattern.matcher(substring);
                if (matcher.find()) {
                    String group = matcher.group();
                    TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                    mainText.append(subMessageComponent);
                    TextComponent textComponent = Component.text(color + "§n" + group + "§r" + color);
                    String url = "https://www.bilibili.com/video/" + group;
                    textComponent = textComponent.clickEvent(ClickEvent.openUrl(url));
                    mainText.append(textComponent);
                    j += group.length();
                    i = j;
                    continue;
                }
            }

            //cv
            if (substringLower.startsWith("cv")) {
                Pattern pattern = Pattern.compile("^[cC][vV][0-9]+");
                Matcher matcher = pattern.matcher(substring);
                if (matcher.find()) {
                    String group = matcher.group();
                    TextComponent subMessageComponent = Component.text(color + message.substring(i, j));
                    mainText.append(subMessageComponent);
                    TextComponent textComponent = Component.text(color + "§n" + group + "§r" + color);
                    String url = "https://www.bilibili.com/read/" + group;
                    textComponent = textComponent.clickEvent(ClickEvent.openUrl(url));
                    mainText.append(textComponent);
                    j += group.length();
                    i = j;
                    continue;
                }
            }
            j++;
        }

        mainText.append(Component.text(color + message.substring(i)));
        TextComponent.Builder commonFinalText = Component.text();

        {
            commonFinalText.append(Component.text("§r" + color + "<"));
            String visitCmd = "/player " + sourcePlayer.replaceAll("§.", "");
            TextComponent nameComponent = Component.text(color + sourcePlayer)
                    .clickEvent(ClickEvent.runCommand(visitCmd));
            if (sourcePlayerUUID != null) {
                if (playerDisplayCache.containsKey(locale)) {
                    nameComponent = nameComponent.hoverEvent(HoverEvent.showText(Component.text(playerDisplayCache.get(locale))));
                } else {
                    String display = PlayerUtils.getDisplay(sourcePlayerUUID);
                    playerDisplayCache.put(locale, display);
                    nameComponent = nameComponent.hoverEvent(HoverEvent.showText(Component.text(playerDisplayCache.get(locale))));
                }
            }

            commonFinalText.append(nameComponent);
            commonFinalText.append(Component.text("§r" + color + ">"));
            commonFinalText.append(Component.text(" "));
            commonFinalText.append(mainText);
        }
        TextComponent.Builder collectionFinalText = Component.text();

        {
            collectionFinalText.append(Component.text("§r" + color + "<"));
            String visitCmd = "/player " + sourcePlayer.replaceAll("§.", "");
            TextComponent nameComponent = Component.text("§3" + sourcePlayer)
                    .clickEvent(ClickEvent.runCommand(visitCmd));
            if (sourcePlayerUUID != null) {
                if (playerDisplayCache.containsKey(locale)) {
                    nameComponent = nameComponent.hoverEvent(HoverEvent.showText(Component.text(playerDisplayCache.get(locale))));
                } else {
                    String display = PlayerUtils.getDisplay(sourcePlayerUUID);
                    playerDisplayCache.put(locale, display);
                    nameComponent = nameComponent.hoverEvent(HoverEvent.showText(Component.text(playerDisplayCache.get(locale))));
                }
            }


            collectionFinalText.append(nameComponent);
            collectionFinalText.append(Component.text("§r" + color + ">"));
            collectionFinalText.append(Component.text(" "));
            collectionFinalText.append(mainText);
        }


        if (subscribe) {
            target.sendMessage(collectionFinalText, MessageType.CHAT);
        } else {
            target.sendMessage(commonFinalText, MessageType.CHAT);
        }
    }


    public static void notify(String playerName) {
        PlaySoundObject playSoundObject = new PlaySoundObject(playerName, "ENTITY_PLAYER_LEVELUP");
        ServerMessageUtils.broadcastBungeeMessage("PlaySound", playSoundObject);
    }

}
