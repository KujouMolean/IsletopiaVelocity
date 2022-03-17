package com.molean.isletopia.velocity.individual;

import com.molean.isletopia.shared.platform.VelocityRelatedUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class WelcomeMessage {

    private static Favicon favicon;


    public WelcomeMessage() {
        VelocityRelatedUtils.getProxyServer().getEventManager().register(VelocityRelatedUtils.getPlugin(), this);

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("server-icon.png");
            assert inputStream != null;
            BufferedImage read = ImageIO.read(inputStream);
            favicon = Favicon.create(read);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Subscribe
    public void on(ProxyPingEvent event) {
        String line1 = "#§lM#§li#§ln#§le#§lc#§lr#§la#§lf#§lt #§l1#§l.#§l1#§l8#§l.#§l2 #§l- #§lI#§ls#§ll#§le#§lt#§lo#§lp#§li#§la#§lS#§le#§lr#§lv#§le#§lr";
        String line2 = "#§l欢#§l迎#§l加#§l入#§l梦#§l幻#§l之#§l屿#§l原#§l版#§l空#§l岛#§l服#§l务#§l器";
        Component description = generateRainbowText(line1)
                .append(Component.text("\n"))
                .append(generateRainbowText(line2));
        ServerPing ping = event.getPing();
        ServerPing build = ping.asBuilder().description(description)
                .favicon(favicon).build();

        event.setPing(build);
    }


    private static final Random random = new Random();

    public static Component generateRainbowText(String text) {
        String[] split = text.split("#");
        Color begin = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color end = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        int incrementRed = (end.getRed() - begin.getRed()) / (split.length - 1);
        int incrementGreen = (end.getGreen() - begin.getGreen()) / (split.length - 1);
        int incrementBlu = (end.getBlue() - begin.getBlue()) / (split.length - 1);
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text(split[0]));
        for (int i = 1; i < split.length; i++) {

            int r = begin.getRed() + incrementRed * i;
            int g = begin.getGreen() + incrementGreen * i;
            int b = begin.getBlue() + incrementBlu * i;

            builder.append(Component.text(split[i]).color(TextColor.color(r,g,b)));
        }
        return builder.asComponent();
    }
}
