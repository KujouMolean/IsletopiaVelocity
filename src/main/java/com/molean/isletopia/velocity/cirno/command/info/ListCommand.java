package com.molean.isletopia.velocity.cirno.command.info;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.molean.isletopia.shared.utils.I18n;
import com.molean.isletopia.velocity.cirno.BotCommandExecutor;
import com.molean.isletopia.velocity.cirno.CommandHandler;
import com.molean.isletopia.velocity.cirno.I18nString;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.*;

public class ListCommand implements BotCommandExecutor {
    public ListCommand() {
        CommandHandler.setExecutor("list", this);
    }

    @Override
    public I18nString execute(long id, List<String> args) {

        if (args.size() == 1) {
            Optional<RegisteredServer> server = VelocityRelatedUtils.getProxyServer().getServer(args.get(0));
            if (server.isPresent()) {
                RegisteredServer registeredServer = server.get();
                Collection<Player> playersConnected = registeredServer.getPlayersConnected();
                ArrayList<String> strings = new ArrayList<>();
                for (Player player : playersConnected) {
                    strings.add(player.getUsername());
                }
                return I18nString.of(String.join(",", strings));
            }

        }

        List<Player> players = new ArrayList<>(VelocityRelatedUtils.getProxyServer().getAllPlayers());

        if (players.size() > 0) {
            Random random = new Random();
            int starNumber = random.nextInt(Math.min(players.size(), 5)) + 1;
            Set<String> set = new HashSet<>();
            for (int i = 0; i < starNumber; i++) {
                set.add(players.get(random.nextInt(players.size())).getUsername());
            }

            I18nString i18nString = I18nString.of("cirno.list.info").add("size", players.size() + "").add("select", String.join(",", set));


            ArrayList<String> strings = new ArrayList<>();
            if (new Random().nextInt(100) < 10) {
                strings.add("cirno.list.info.something.beacon");

            }
            if (new Random().nextInt(100) < 10) {
                //beacon
                strings.add("cirno.list.info.something.heart");
            }
            if (new Random().nextInt(100) < 5) {
                //beacon
                strings.add("cirno.list.info.something.bundle");
            }
            if (new Random().nextInt(100) < 1) {
                //beacon
                strings.add("cirno.list.info.something.elytra");
            }

            i18nString.add("something", locale -> {
                StringBuilder str = new StringBuilder();
                for (String string : strings) {
                    str.append(I18n.getMessage(locale, string));
                }
                return str.toString();
            });
            return i18nString;

        }
        return I18nString.of("cirno.list.empty");
    }


}
