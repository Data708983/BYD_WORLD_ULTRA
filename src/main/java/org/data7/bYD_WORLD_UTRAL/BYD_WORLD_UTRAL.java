package org.data7.bYD_WORLD_UTRAL;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.data7.bYD_WORLD_UTRAL.Placeholder.HolderExpansions;
import org.data7.bYD_WORLD_UTRAL.advs.AdvancementTabNamespaces;
import org.data7.bYD_WORLD_UTRAL.advs.tab0.Bread2;
import org.data7.bYD_WORLD_UTRAL.advs.tab0.Ender_pearl1;
import org.data7.bYD_WORLD_UTRAL.advs.tab0.Oak_sapling3;

import java.io.File;
import java.util.UUID;

import static org.data7.bYD_WORLD_UTRAL.Tpa.*;

public final class BYD_WORLD_UTRAL extends JavaPlugin {

    public static AdvancementTab advancementTab;
    private RootAdvancement root;
    private UltimateAdvancementAPI api;

    // 从左到右的渐变生成方法
    private String generateHorizontalGradientText(String text) {
        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();

        // 起始颜色 (#f12eff) 和结束颜色 (#00fbff) 的RGB值
        int[] startRGB = {241, 46, 255};   // #f12eff
        int[] endRGB = {0, 251, 255};      // #00fbff

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
            result.append("\u001B[0m\n"); // 重置颜色并换行
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
                "\n ____  _  _  ____        __  __  ____  ____    __    __   \n" +
                        "(  _ \\( \\/ )(  _ \\      (  )(  )(_  _)(  _ \\  /__\\  (  )  \n" +
                        " ) _ < \\  /  )(_) ) ___  )(__)(   )(   )   / /(__)\\  )(__ \n" +
                        "(____/ (__) (____/ (___)(______) (__) (_)\\_)(__)(__)(____)"
        ));

        getServer().getLogger().info("BYD_WORLD_UTRAL is enabled!");
        String version = getDescription().getVersion();
        getServer().getLogger().info("Plugin version:" + "\033[1;34m" + version + "\033[0m");

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

//        //自定义成就有关
//
//        api = UltimateAdvancementAPI.getInstance((Plugin) this);
//
//        advancementTab = api.createAdvancementTab("your_tab_name");
//
//        AdvancementDisplay rootDisplay = new AdvancementDisplay(Material.GRASS_BLOCK, "Example root", AdvancementFrameType.TASK, true, true, 0, 0, "description");
//
//        root = new RootAdvancement(advancementTab, "root", rootDisplay, "textures/block/stone.png");
//
//        advancementTab.registerAdvancements(root);
//
//        Bukkit.getPluginManager().registerEvents(new CustomeAdvancements(), this);
        initializeTabs();

        //PlaceHolder
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new HolderExpansions(this).register();
        }

    }

    public AdvancementTab tab0;

    public void initializeTabs() {
        api = UltimateAdvancementAPI.getInstance(this);
        tab0 = api.createAdvancementTab(AdvancementTabNamespaces.tab0_NAMESPACE);
        RootAdvancement oak_sapling0 = new RootAdvancement(tab0, "oak_sapling0", new AdvancementDisplay(Material.OAK_SAPLING, "oak_sapling0", AdvancementFrameType.TASK, true, true, 0f, 0f ),"textures/block/nether_portal.png",1);
        Ender_pearl1 ender_pearl1 = new Ender_pearl1(oak_sapling0);
        Bread2 bread2 = new Bread2(oak_sapling0);
        Oak_sapling3 oak_sapling3 = new Oak_sapling3(ender_pearl1,bread2);
        tab0.registerAdvancements(oak_sapling0 ,ender_pearl1 ,bread2 ,oak_sapling3 );
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
        /**
         * 紫砂
         */
        if (label.equalsIgnoreCase("suicide")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Component suicideMessage = Component.text("💀")
                        .append(Component.text(player.getName())
                                .color(TextColor.color(255, 255, 0))) // 设置玩家名字为黄色
                        .append(Component.text("紫砂了!"));
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
                    if (args.length != 2) {
                        player.sendMessage(Component.text("❎命令用法错误！正确格式: /tpa to <玩家名> 或 /tpa come <玩家名>")
                                .color(TextColor.color(255, 0, 0)));
                        return true;
                    }

                    String subCommand = args[0];
                    String targetName = args[1];
                    Player targetPlayer = Bukkit.getPlayerExact(targetName); // 精确查找玩家

                    // 检查目标玩家是否在线
                    if (targetPlayer == null || !targetPlayer.isOnline()) {
                        player.sendMessage(Component.text("❎找不到玩家: " + targetName)
                                .color(TextColor.color(255, 0, 0)));
                        return true;
                    }

                    UUID playercome; // 请求发起者
                    UUID playerto;   // 请求目标

                    if (subCommand.equalsIgnoreCase("to")) {
                        playercome = player.getUniqueId();
                        playerto = targetPlayer.getUniqueId();

                        if (isOnCoolDown(playercome.toString(), tpaConfig.getCooldown(), tpaConfig.getReply()) == -1) {

                            if(tpaConfig.isConfirm()){
                                // 发送提示信息
                                player.sendMessage(Component.text("已向 " + targetName + " 发送传送请求（你将传送到对方）"));
                                Component requestMessage = Component.text()
                                        .append(Component.text(player.getName(), TextColor.color(255, 255, 0))
                                                .append(Component.text(" 请求传送到你这里!", TextColor.color(255, 255, 225))))
                                        .build();
                                targetPlayer.sendMessage(requestMessage);

                                Component responseButtons = Component.text()
                                        .append(Component.text("[接受] ", TextColor.color(0, 255, 0))
                                                .clickEvent(ClickEvent.callback(clicker -> {
                                                    // 接受请求后的消息
                                                    Component acceptTargetMsg = Component.text()
                                                            .append(Component.text(player.getName(), TextColor.color(255, 255, 0))
                                                                    .append(Component.text(" 传送过来了!", TextColor.color(255, 255, 225))))
                                                            .build();

                                                    Component acceptPlayerMsg = Component.text()
                                                            .append(Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0))
                                                                    .append(Component.text(" 同意了你的请求!", TextColor.color(255, 255, 225))))
                                                            .build();
                                                    tpa(playercome.toString(),playerto.toString());

                                                    targetPlayer.sendMessage(acceptTargetMsg);
                                                    player.sendMessage(acceptPlayerMsg);
                                                }))
                                                .append(Component.text(" "))
                                                .append(Component.text("[拒绝]", TextColor.color(255, 0, 0))
                                                        .clickEvent(ClickEvent.callback(clicker -> {
                                                            // 拒绝请求后的消息
                                                            Component rejectTargetMsg = Component.text()
                                                                    .append(Component.text("你拒绝了 ", TextColor.color(255, 255, 225)))
                                                                    .append(Component.text(player.getName(), TextColor.color(255, 255, 0))
                                                                            .append(Component.text(" 的传送请求!", TextColor.color(255, 255, 225))))
                                                                    .build();

                                                            Component rejectPlayerMsg = Component.text()
                                                                    .append(Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0))
                                                                            .append(Component.text(" 拒绝了你的请求!", TextColor.color(255, 255, 225))))
                                                                    .build();

                                                            targetPlayer.sendMessage(rejectTargetMsg);
                                                            player.sendMessage(rejectPlayerMsg);
                                                        }))))
                                        .build();

                                targetPlayer.sendMessage(responseButtons);
                            }
                            else {
                                Component acceptTargetMsg = Component.text()
                                        .append(Component.text(player.getName(), TextColor.color(255, 255, 0))
                                                .append(Component.text(" 传送过来了!", TextColor.color(255, 255, 225))))
                                        .build();

                                Component acceptPlayerMsg = Component.text()
                                        .append(Component.text("你传送到了 ", TextColor.color(255, 255, 225)))
                                                .append(Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0)))
                                        .build();
                                tpa(playercome.toString(),playerto.toString());

                                targetPlayer.sendMessage(acceptTargetMsg);
                                player.sendMessage(acceptPlayerMsg);
                            }

                        }

                    }
                    else if (subCommand.equalsIgnoreCase("come")) {
                        playerto = player.getUniqueId();
                        playercome = targetPlayer.getUniqueId();
                        if(tpaConfig.isConfirm()){
                            // 发送提示信息
                            player.sendMessage(Component.text("已向 " + targetName + " 发送传送请求（请求对方传送到你）"));
                            // 构建请求消息
                            Component requestMessage = Component.text()
                                    .append(Component.text(player.getName(), TextColor.color(255, 255, 0)))
                                    .append(Component.text(" 请求你传送到TA那里!", TextColor.color(255, 255, 225)))
                                    .build();
                            targetPlayer.sendMessage(requestMessage);
                            // 构建交互按钮
                            Component responseButtons = Component.text()
                                    .append(Component.text("[接受] ", TextColor.color(0, 255, 0))
                                            .clickEvent(ClickEvent.callback(clicker -> {
                                                // 接受请求后的消息
                                                if (isOnCoolDown(playercome.toString(), tpaConfig.getCooldown(), tpaConfig.getReply()) == -1){
                                                    Component acceptTargetMsg = Component.text()
                                                            .append(Component.text("你已传送到 ", TextColor.color(255, 255, 225)))
                                                            .append(Component.text(player.getName(), TextColor.color(255, 255, 0)))
                                                            .append(Component.text(" 的位置!", TextColor.color(255, 255, 225)))
                                                            .build();
                                                    Component acceptPlayerMsg = Component.text()
                                                            .append(Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0)))
                                                            .append(Component.text(" 已传送到你这里!", TextColor.color(255, 255, 225)))
                                                            .build();
                                                    // 执行传送（对方传送到自己）
                                                    tpa(playercome.toString(), playerto.toString());
                                                    targetPlayer.sendMessage(acceptTargetMsg);
                                                    player.sendMessage(acceptPlayerMsg);
                                                }
                                            }))
                                            .append(Component.text(" "))
                                            .append(Component.text("[拒绝]", TextColor.color(255, 0, 0))
                                                    .clickEvent(ClickEvent.callback(clicker -> {
                                                        // 拒绝请求后的消息
                                                        Component rejectTargetMsg = Component.text()
                                                                .append(Component.text("你拒绝了 ", TextColor.color(255, 255, 225)))
                                                                .append(Component.text(player.getName(), TextColor.color(255, 255, 0)))
                                                                .append(Component.text(" 的传送请求!", TextColor.color(255, 255, 225)))
                                                                .build();
                                                        Component rejectPlayerMsg = Component.text()
                                                                .append(Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0)))
                                                                .append(Component.text(" 拒绝了你的传送请求!", TextColor.color(255, 255, 225)))
                                                                .build();
                                                        targetPlayer.sendMessage(rejectTargetMsg);
                                                        player.sendMessage(rejectPlayerMsg);
                                                    }))))
                                    .build();
                            targetPlayer.sendMessage(responseButtons);
                        }
                        else {
                            long timerest = isOnCoolDown(playercome.toString(), tpaConfig.getCooldown(), tpaConfig.getReply());
                            if (timerest == -1){
                                Component acceptTargetMsg = Component.text()
                                        .append(Component.text("你已传送到 ", TextColor.color(255, 255, 225)))
                                        .append(Component.text(player.getName(), TextColor.color(255, 255, 0)))
                                        .append(Component.text(" 的位置!", TextColor.color(255, 255, 225)))
                                        .build();
                                Component acceptPlayerMsg = Component.text()
                                        .append(Component.text(targetPlayer.getName(), TextColor.color(255, 255, 0)))
                                        .append(Component.text(" 已传送到你这里!", TextColor.color(255, 255, 225)))
                                        .build();
                                // 执行传送（对方传送到自己）
                                tpa(playercome.toString(), playerto.toString());
                                targetPlayer.sendMessage(acceptTargetMsg);
                                player.sendMessage(acceptPlayerMsg);
                            }
                            else {
                                Component acceptTargetMsg1 = Component.text()
                                        .append(Component.text(player.getName(), TextColor.color(255, 255, 0)))
                                        .append(Component.text("请求你的援助! 但你还在冷却中~", TextColor.color(255, 255, 225)))
                                        .build();
                                player.sendMessage(Component.text("❎TA仍在冷却中，剩余时间: ",TextColor.color(255,0,0)).append(Component.text(timerest + "s")));
                                targetPlayer.sendMessage(acceptTargetMsg1);
                            }

                        }
                    }
                    else {
                        player.sendMessage(Component.text("❎命令用法错误！正确格式: /tpa to <玩家名> 或 /tpa come <玩家名>")
                                .color(TextColor.color(255, 0, 0)));
                        return true;
                    }
                    return true;
                }
            } else return false;

        }
        return false;
    }

}
