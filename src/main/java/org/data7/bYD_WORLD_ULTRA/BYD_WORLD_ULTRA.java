package org.data7.bYD_WORLD_ULTRA;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.data7.bYD_WORLD_ULTRA.PAPI.PAPI;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.data7.bYD_WORLD_ULTRA.Tpa.*;

public final class BYD_WORLD_ULTRA extends JavaPlugin {

    // 从左到右的渐变生成方法
    static public String generateHorizontalGradientText(String text) {
        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();

        // 起始颜色 (#f12eff) 和结束颜色 (#00fbff) 的RGB值
        int[] startRGB = {241, 46, 255};   // #f12eff
        int[] endRGB = {0, 251, 255};      // #00fbff

        int index = 0;
        for (String line : lines) {
            if (line.isEmpty()) {
                result.append("\n");
                continue;
            }

            int lineLength = line.length();
            for (int j = 0; j < lineLength; j++) {
                // 计算当前字符的颜色（水平方向的线性插值）
                double ratio = (double) j / (lineLength - 1);
                int r = (int) (startRGB[0] + (endRGB[0] - startRGB[0]) * ratio);
                int g = (int) (startRGB[1] + (endRGB[1] - startRGB[1]) * ratio);
                int b = (int) (startRGB[2] + (endRGB[2] - startRGB[2]) * ratio);

                // 为每个字符添加ANSI颜色代码
                result.append(String.format("\u001B[38;2;%d;%d;%dm%c", r, g, b, line.charAt(j)));
            }
            if (index == Arrays.stream(lines).count() - 1) {
                result.append("\u001B[0m"); // 重置颜色
            }
            else result.append("\u001B[0m\n"); // 重置颜色并换行

            index++;
        }

        return result.toString();
    }

    @Override
    public void onEnable() {

        // Plugin startup logic
//        getServer().getLogger().info("\n ____  _  _  ____        __  __  ____  ____    __    __   \n" +
//                "(  _ \\( \\/ )(  _ \\      (  )(  )(_  _)(  _ \\  /__\\  (  )  \n" +
//                " ) _ < \\  /  )(_) ) ___  )(__)(   )(   )   / /(__)\\  )(__ \n" +
//                "(____/ (__) (____/ (___)(______) (__) (_)\\_)(__)(__)(____)");

        getServer().getLogger().info(generateHorizontalGradientText(
                "\n ____  _  _  ____        __  __  __    ____  ____    __   \n" +
                "(  _ \\( \\/ )(  _ \\      (  )(  )(  )  (_  _)(  _ \\  /__\\  \n" +
                " ) _ < \\  /  )(_) ) ___  )(__)(  )(__   )(   )   / /(__)\\ \n" +
                "(____/ (__) (____/ (___)(______)(____) (__) (_)\\_)(__)(__)\n"
        ));

        getServer().getLogger().info(generateHorizontalGradientText("BYD_WORLD_ULTRA is enabled!"));
        String version = getDescription().getVersion();
        getServer().getLogger().info("Plugin version:" + "\033[1;34m" + version + "\033[0m");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPI().register();
            getServer().getLogger().info(generateHorizontalGradientText("BYD_PAPI_EXPANSION is enabled!"));
        }
        else getServer().getLogger().info(generateHorizontalGradientText("BYD_PAPI_EXPANSION is disabled!(PLUGIN_NOT_FOUND)"));

        //配置文件
        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            saveResource("config.yml", false);
        }

        File broadcastFile = new File(getDataFolder(), "broadcast.yml");
        if (!broadcastFile.exists()) {
            saveResource("broadcast.yml", false);
        }
        File tpaFile = new File(getDataFolder(), "tpa.yml");
        if (!tpaFile.exists()) {
            saveResource("tpa.yml", false);
        }

        //注册监听
        this.getServer().getPluginManager().registerEvents(new PlayerJoin.PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new Playerdeath(), this);

        Tpa tpa = new Tpa();
        tpa.connect();
        tpa.InitialDB();

        this.getCommand("suicide").setExecutor(this);
        this.getCommand("tpa").setExecutor(this);

        // i18n
        File langFolder = new File(getDataFolder(), "lang");

        if (!langFolder.exists()) {
            Bukkit.getLogger().info(generateHorizontalGradientText("No Locals Folder Found, Created!"));
            langFolder.mkdirs();
            saveResource("lang/lang_en_us.properties", false);
            saveResource("lang/lang_zh_cn.properties", false);
        }

        File[] filelist = langFolder.listFiles((dir, name) -> name.endsWith(".properties"));

        TranslationStore.StringBased<MessageFormat> store = TranslationStore.messageFormat(Key.key("bydworld:translation"));
        boolean hasLanguages = false;

        if (filelist != null && filelist.length > 0) {
            Bukkit.getLogger().info(generateHorizontalGradientText("Found Locals:"));
            for (File file : filelist) {
                Locale locale = parseLocaleFromFilename(file.getName());
                if (locale == null) {
                    Bukkit.getLogger().warning("- " + "Skipping invalid language file: " + file.getName());
                    continue;
                }

                Bukkit.getLogger().info(generateHorizontalGradientText("- " + locale.toString()));
                try (FileInputStream fis = new FileInputStream(file);
                     InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {

                    ResourceBundle bundle = new PropertyResourceBundle(reader);
                    store.registerAll(locale, bundle, true);
                    hasLanguages = true;

                } catch (IOException e) {
                    Bukkit.getLogger().warning(" -> Failed to load language file: " + file.getName());
                }
            }
        }

        if (!hasLanguages) {
            Bukkit.getLogger().info(generateHorizontalGradientText("No valid locals found, using en_us as default!"));
            // 加载默认的en_us
            try (InputStream is = getResource("lang/lang_en_us.properties")) {
                if (is != null) {
                    ResourceBundle bundle = new PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
                    store.registerAll(new Locale("en", "us"), bundle, true);
                }
            } catch (IOException e) {
                Bukkit.getLogger().warning("Failed to load default language file");
            }
        }

        GlobalTranslator.translator().addSource(store);
    }

    private Locale parseLocaleFromFilename(String filename) {
        try {
            String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));
            String[] parts = nameWithoutExt.split("_");

            if (parts.length >= 3) {
                String language = parts[1].toLowerCase();
                String country = parts[2].toUpperCase();
                return new Locale(language, country);
            } else if (parts.length == 2) {
                return new Locale(parts[1].toLowerCase());
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to parse locale from filename: " + filename);
        }
        return null;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /** 紫砂 */
        if (label.equalsIgnoreCase("suicide")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
//                Component suicideMessage = Component.text("💀")
//                        .append(Component.text(player.getName())
//                                .color(TextColor.color(255, 255, 0))) // 设置玩家名字为黄色
//                        .append(Component.text("紫砂了!"));

                Component suicideMessage = Component.translatable("player.suicide.msg",Component.text(player.getName()).color(TextColor.color(255, 255, 0)));

                player.getServer().broadcast(suicideMessage);
                // 获取所有在线玩家
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    // 为每个在线玩家播放音效
                    onlinePlayer.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5f, 5f);
                }
                player.addScoreboardTag("suicide");
                player.setHealth(0);
                return true;
            }
        }
        /** TPA */
        if (label.equalsIgnoreCase("tpa")) {

            Tpa.TpaConfig tpaConfig = loadTpa();

            if (tpaConfig.isEnableTpa()) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    // 检查参数数量
                    if (args.length > 2 || args.length < 1) {
                        player.sendMessage(Component.translatable("command.error.tpa")
                                .color(TextColor.color(255, 0, 0)));
                        return true;
                    }

                    String subCommand;
                    String targetName;
                    Player targetPlayer;
                    UUID playercome; // 请求发起者
                    UUID playerto;   // 请求目标

                    switch (args.length) {
                        case 1:
                            subCommand = args[0];
                            if (subCommand.equalsIgnoreCase("home")) {
                                playercome = player.getUniqueId();
                                if (isOnCoolDown(playercome.toString(), tpaConfig.getCooldown(), tpaConfig.getReply()) == -1){
                                    tpa(playercome.toString(),playercome.toString());
                                    return true;
                                }
                                else return true;
                            }
                            else{
                                player.sendMessage(Component.translatable("command.error.tpa")
                                        .color(TextColor.color(255, 0, 0)));
                                return true;
                            }
                        case 2:
                            subCommand = args[0];
                            targetName = args[1];
                            targetPlayer = Bukkit.getPlayerExact(targetName); // 精确查找玩家
                            // 检查目标玩家是否在线
                            if (targetPlayer == null || !targetPlayer.isOnline()) {
                                player.sendMessage(Component.translatable("command.error.playernotfound", Component.text(targetName))
                                        .color(TextColor.color(255, 0, 0)));
                                return true;
                            }
                            if (subCommand.equalsIgnoreCase("to") && player != targetPlayer) {
                                playercome = player.getUniqueId();
                                playerto = targetPlayer.getUniqueId();

                                if (isOnCoolDown(playercome.toString(), tpaConfig.getCooldown(), tpaConfig.getReply()) == -1) {

                                    if(tpaConfig.isConfirm()){
                                        // 发送提示信息
                                        player.sendMessage(Component.translatable("command.send.tpa.to", Component.text(targetName)));
                                        Component requestMessage = Component.translatable("command.send.tpa.to.confirm", Component.text(targetPlayer.getName(), TextColor.color(255, 225, 0)));
                                        targetPlayer.sendMessage(requestMessage);

                                        UUID finalPlayercome1 = playercome;
                                        Component responseButtons = Component.text()
                                                .append(Component.translatable("command.send.tpa.accept").color(TextColor.color(0, 255, 0))
                                                        .clickEvent(ClickEvent.callback(clicker -> {
                                                            // 接受请求后的消息
                                                            Component acceptTargetMsg = Component.translatable("command.send.tpa.accept.to.from", Component.text(player.getName(), TextColor.color(255, 255, 0)));

                                                            Component acceptPlayerMsg = Component.translatable("command.send.tpa.accept.to.to", Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0)));

                                                            tpa(finalPlayercome1.toString(),playerto.toString());

                                                            targetPlayer.sendMessage(acceptTargetMsg);
                                                            player.sendMessage(acceptPlayerMsg);
                                                        }))
                                                        .append(Component.text(" "))
                                                        .append(Component.translatable("command.send.tpa.refuse").color(TextColor.color(255, 0, 0))
                                                                .clickEvent(ClickEvent.callback(clicker -> {
                                                                    // 拒绝请求后的消息
                                                                    Component rejectTargetMsg = Component.translatable("command.send.tpa.refuse.to.from", Component.text(player.getName(), TextColor.color(255, 255, 0)));

                                                                    Component rejectPlayerMsg = Component.translatable("command.send.tpa.refuse.to.to", Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0)));

                                                                    targetPlayer.sendMessage(rejectTargetMsg);
                                                                    player.sendMessage(rejectPlayerMsg);
                                                                }))))
                                                .build();

                                        targetPlayer.sendMessage(responseButtons);
                                    }
                                    else {
                                        Component acceptTargetMsg = Component.translatable("command.send.tpa.accept.to.from", Component.text(player.getName(), TextColor.color(255, 255, 0)));
                                        Component acceptPlayerMsg = Component.translatable("command.send.tpa.accept.to.no.confirm", Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0)));

                                        tpa(playercome.toString(),playerto.toString());

                                        targetPlayer.sendMessage(acceptTargetMsg);
                                        player.sendMessage(acceptPlayerMsg);
                                    }

                                }

                            }
                            else if (subCommand.equalsIgnoreCase("come") && player != targetPlayer) {
                                playerto = player.getUniqueId();
                                playercome = targetPlayer.getUniqueId();
                                if(tpaConfig.isConfirm()){
                                    // 发送提示信息
                                    player.sendMessage(Component.translatable("command.send.tpa.come", Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0))));
                                    // 构建请求消息
                                    Component requestMessage = Component.translatable("command.send.tpa.come.confirm",Component.text(player.getName(), TextColor.color(255, 255, 0)));

                                    targetPlayer.sendMessage(requestMessage);
                                    // 构建交互按钮
                                    UUID finalPlayercome = playercome;
                                    Component responseButtons = Component.text()
                                            .append(Component.translatable("command.send.tpa.accept").color(TextColor.color(0, 255, 0))
                                                    .clickEvent(ClickEvent.callback(clicker -> {
                                                        // 接受请求后的消息
                                                        if (isOnCoolDown(finalPlayercome.toString(), tpaConfig.getCooldown(), tpaConfig.getReply()) == -1){
                                                            Component acceptTargetMsg = Component.translatable("command.send.tpa.accept.come.from", Component.text(player.getName(), TextColor.color(255, 255, 0)));

                                                            Component acceptPlayerMsg = Component.translatable("command.send.tpa.accept.come.to",Component.text(targetPlayer.getName()).color(TextColor.color(255, 255, 0)));

                                                            // 执行传送（对方传送到自己）
                                                            tpa(finalPlayercome.toString(), playerto.toString());
                                                            targetPlayer.sendMessage(acceptTargetMsg);
                                                            player.sendMessage(acceptPlayerMsg);
                                                        }
                                                    }))
                                                    .append(Component.text(" "))
                                                    .append(Component.translatable("command.send.tpa.refuse").color(TextColor.color(255, 0, 0))
                                                            .clickEvent(ClickEvent.callback(clicker -> {
                                                                // 拒绝请求后的消息
                                                                Component rejectTargetMsg = Component.translatable("command.send.tpa.refuse.come.from", Component.text(player.getName(), TextColor.color(255, 255, 0)));

                                                                Component rejectPlayerMsg = Component.translatable("command.send.tpa.refuse.come.to", Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0)));
                                                                targetPlayer.sendMessage(rejectTargetMsg);
                                                                player.sendMessage(rejectPlayerMsg);
                                                            }))))
                                            .build();
                                    targetPlayer.sendMessage(responseButtons);
                                }
                                else {
                                    long timerest = isOnCoolDown(playercome.toString(), tpaConfig.getCooldown(), tpaConfig.getReply());
                                    if (timerest == -1){
                                        Component acceptTargetMsg = Component.translatable("command.send.tpa.accept.come.from", Component.text(player.getName(), TextColor.color(255, 255, 0)));
                                        Component acceptPlayerMsg = Component.translatable("command.send.tpa.accept.come.no.confirm", Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0)));
                                        // 执行传送（对方传送到自己）
                                        tpa(playercome.toString(), playerto.toString());
                                        targetPlayer.sendMessage(acceptTargetMsg);
                                        player.sendMessage(acceptPlayerMsg);
                                    }
                                    else {
                                        Component acceptTargetMsg1 = Component.translatable("command.send.tpa.come.from.cooldown", Component.text(player.getName(), TextColor.color(255, 255, 0)));
                                        player.sendMessage(Component.translatable("command.send.tpa.come.to.cooldown",Component.text(timerest + "s").color(TextColor.color(225,0,0))));
                                        targetPlayer.sendMessage(acceptTargetMsg1);
                                    }

                                }
                            }
                            else if (player == targetPlayer){
                                playercome = player.getUniqueId();
                                if (isOnCoolDown(playercome.toString(), tpaConfig.getCooldown(), tpaConfig.getReply()) == -1){
                                    tpa(playercome.toString(),playercome.toString());
                                    return true;
                                }
                                else return true;
                            }
                            else {
                                player.sendMessage(Component.translatable("command.error.tpa")
                                        .color(TextColor.color(255, 0, 0)));
                                return true;
                            }
                            return true;
                    }

                }
            } else return false;
        }
        if (label.equalsIgnoreCase("sethome")){
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c Only players can use this command!");
                return true;
            }
            Player player = (Player) sender;
            if (args.length != 0){
                player.sendMessage(Component.translatable("command.error.sethome")
                        .color(TextColor.color(255, 0, 0)));
                return true;
            }
            else {
                Tpa.sethome(player);
                return true;
            }
        }
        return false;
    }
}
