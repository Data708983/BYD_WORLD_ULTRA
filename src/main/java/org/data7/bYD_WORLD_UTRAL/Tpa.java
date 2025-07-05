package org.data7.bYD_WORLD_UTRAL;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Tpa {
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_UTRAL/database.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void InitialDB(){
        String sql = "CREATE TABLE IF NOT EXISTS TPA " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "uuidfrom TEXT NOT NULL, " +
                "uuidto TEXT NOT NULL, " +
                "timestemp INTEGER, " +
                "available INTEGER);";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_UTRAL/database.db");
             Statement stmt = conn.createStatement()) {
            // 执行SQL语句
            stmt.execute(sql);
//            System.out.println("表创建成功（如果不存在）");
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
        }

        loadTpa();
//        // 插入数据的SQL语句
//        String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";
//
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_UTRAL/database.db");
//             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
//            // 设置插入的数据
//            pstmt.setString(1, "e4037d10-e436-35e4-9c45-594a12ff44d0");
//            pstmt.setString(2, "e4037d10-e436-35e4-9c45-594a12ff44d0");
//            pstmt.setLong(3, 114514);
//            pstmt.setInt(4, 1);
//
//            /*用于测试 */
//            // 执行插入操作
//            pstmt.executeUpdate();
//            System.out.println("数据插入成功");
//
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
    }
    /** 加载 TPA 传送模块: tpa.yml */
    public static class TpaConfig {
        private boolean enableTpa;
        private boolean debug;
        private long cooldown;
        private int reply;
        private boolean confirm;
        private String type;
        private int record;

        // 构造函数
        public TpaConfig(boolean enableTpa, boolean debug, long cooldown, int reply, boolean confirm, String type, int record) {
            this.enableTpa = enableTpa;
            this.debug = debug;
            this.cooldown = cooldown;
            this.reply = reply;
            this.confirm = confirm;
            this.type = type;
            this.record = record;
        }

        // Getter 方法
        public boolean isEnableTpa() { return enableTpa; }
        public boolean isDebug() { return debug; }
        public long getCooldown() { return cooldown; }
        public int getReply() { return reply; }
        public boolean isConfirm() { return confirm; }
        public String getType() { return type; }
        public int getRecord() { return record; }
    }

    public static TpaConfig loadTpa() {
        BYD_WORLD_UTRAL plugin = BYD_WORLD_UTRAL.getPlugin(BYD_WORLD_UTRAL.class);
        File tpaFile = new File(plugin.getDataFolder(), "tpa.yml");
        YamlConfiguration tpaConfig = YamlConfiguration.loadConfiguration(tpaFile);

        // 如果文件不存在，创建默认配置
        if (!tpaFile.exists()) {
            // 保存默认配置（注意：这里应该保存 tpa.yml 而不是 config.yml）
            plugin.saveResource("tpa.yml", false);
            tpaConfig = YamlConfiguration.loadConfiguration(tpaFile);
        }

        // 读取所有配置值
        boolean enableTpa = tpaConfig.getBoolean("enable", true);
        boolean debug = tpaConfig.getBoolean("debug", false);
        long cooldown = tpaConfig.getLong("cooldown", 60); // 默认30秒
        int reply = tpaConfig.getInt("reply", 60); // 默认60秒
        boolean confirm = tpaConfig.getBoolean("confirm", true);
        String type = tpaConfig.getString("type", "teleport");
        int record = tpaConfig.getInt("record", 20); // 默认记录20条

        // 在debug模式下打印数据库记录
        if(debug) {
            String readSql = "SELECT * FROM TPA";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_UTRAL/database.db");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(readSql)) {

                while (rs.next()) {
                    Bukkit.getServer().getLogger().info("id: " + rs.getInt("id"));
                    Bukkit.getServer().getLogger().info("uuidfrom: " + rs.getString("uuidfrom"));
                    Bukkit.getServer().getLogger().info("uuidto: " + rs.getString("uuidto"));
                    Bukkit.getServer().getLogger().info("timestemp: " + rs.getLong("timestemp"));
                    Bukkit.getServer().getLogger().info("available: " + rs.getInt("available"));
                }
            } catch (SQLException e) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "数据库查询错误", e);
            }
        }

        // 返回包含所有配置值的对象
        return new TpaConfig(enableTpa, debug, cooldown, reply, confirm, type, record);
    }

    public static long isOnCoolDown(String playeruuid, long cooldown, int reply) {
        // 获取现在的时间戳
        long now_time = System.currentTimeMillis();
        // 修改SQL以获取最近的一条记录（按id降序）
        String readSql = "SELECT * FROM TPA WHERE uuidfrom = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_UTRAL/database.db");
             PreparedStatement pstmt = conn.prepareStatement(readSql)) {

            pstmt.setString(1, playeruuid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                long recordTimestamp = rs.getLong("timestemp");
                long timeDifference = now_time - recordTimestamp;
                if(loadTpa().isDebug()){
                    Bukkit.getServer().getLogger().info("查询到最近记录:");
                    Bukkit.getServer().getLogger().info("id: " + rs.getInt("id"));
                    Bukkit.getServer().getLogger().info("uuidfrom: " + rs.getString("uuidfrom"));
                    Bukkit.getServer().getLogger().info("uuidto: " + rs.getString("uuidto"));
                    Bukkit.getServer().getLogger().info("timestemp: " + recordTimestamp);
                    Bukkit.getServer().getLogger().info("available: " + rs.getInt("available"));
                    Bukkit.getServer().getLogger().info("与当前时间差: " + timeDifference + "ms");
                }
                // 检查时间差是否小于冷却时间
                if (timeDifference < cooldown*1000) {
                    long remainingTime = (cooldown * 1000 - timeDifference)/1000;
                    Player fromPlayer = Bukkit.getPlayer(UUID.fromString(playeruuid));
                    fromPlayer.sendMessage(Component.text("❎仍在冷却中，剩余时间: ",TextColor.color(255,0,0)).append(Component.text(remainingTime + "s")));
                    return remainingTime;
                }
            }
        } catch (SQLException e) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "数据库查询错误", e);
        }
        if(loadTpa().isDebug()) Bukkit.getServer().getLogger().info("无冷却限制");
        return -1;
    }
    public static boolean tpa(String playerfrom,String playerto){
        // 获取现在的时间戳
        long now_time = System.currentTimeMillis();
        // 获取玩家对象
        Player fromPlayer = Bukkit.getPlayer(UUID.fromString(playerfrom));
        Player toPlayer = Bukkit.getPlayer(UUID.fromString(playerto));
        // 获取配置类型
        String type = loadTpa().getType();

        // 根据类型执行不同的传送方式
        if (type.equalsIgnoreCase("absolute")) {
            // 完全复制目标位置（包括朝向）
            Location targetLocation = toPlayer.getLocation().clone();
            fromPlayer.teleport(targetLocation);
            String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_UTRAL/database.db");
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            // 设置插入的数据
            pstmt.setString(1, playerfrom);
            pstmt.setString(2, playerto);
            pstmt.setLong(3, now_time);
            pstmt.setInt(4, 1);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        } else if (type.equalsIgnoreCase("near")) {

        } else {

        }
        // 播放传送音效
        fromPlayer.playSound(fromPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        toPlayer.playSound(toPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        return true;
    }
}
