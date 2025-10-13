package org.data7.bYD_WORLD_ULTRA;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class Tpa {
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
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
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
             Statement stmt = conn.createStatement()) {
            // 执行SQL语句
            stmt.execute(sql);
//            System.out.println("表创建成功（如果不存在）");
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
        }

        String sqlofHome = "CREATE TABLE IF NOT EXISTS HOME " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "uuid TEXT NOT NULL UNIQUE, " +
                "location TEXT NOT NULL, " +
                "timestemp INTEGER);"; // location form :"123,123,123"
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
             Statement stmt = conn.createStatement()) {
            // 执行SQL语句
            stmt.execute(sqlofHome);
//            System.out.println("表创建成功（如果不存在）");
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
        }

        loadTpa();
//        // 插入数据的SQL语句
//        String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";
//
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
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
        private long homecooldown;

        // 构造函数
        public TpaConfig(boolean enableTpa, boolean debug, long cooldown, int reply, boolean confirm, String type, int record, long homecooldown) {
            this.enableTpa = enableTpa;
            this.debug = debug;
            this.cooldown = cooldown;
            this.reply = reply;
            this.confirm = confirm;
            this.type = type;
            this.record = record;
            this.homecooldown = homecooldown;
        }

        // Getter 方法
        public boolean isEnableTpa() { return enableTpa; }
        public boolean isDebug() { return debug; }
        public long getCooldown() { return cooldown; }
        public int getReply() { return reply; }
        public boolean isConfirm() { return confirm; }
        public String getType() { return type; }
        public int getRecord() { return record; }
        public long getHomecooldown() { return homecooldown; }
    }

    public static TpaConfig loadTpa() {
        BYD_WORLD_ULTRA plugin = BYD_WORLD_ULTRA.getPlugin(BYD_WORLD_ULTRA.class);
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
        long homecooldown = tpaConfig.getLong("homecooldown",86400);

        // 在debug模式下打印数据库记录
        if(false) {
            String readSql = "SELECT * FROM TPA";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(readSql)) {

                while (rs.next()) {
                    Bukkit.getServer().getLogger().info("id: " + rs.getInt("id"));
                    Bukkit.getServer().getLogger().info("uuidfrom: " + rs.getString("uuidfrom"));
                    Bukkit.getServer().getLogger().info("uuidto: " + rs.getString("uuidto"));
                    Bukkit.getServer().getLogger().info("timestemp: " + rs.getLong("timestemp"));
                    Bukkit.getServer().getLogger().info("available: " + rs.getInt("available"));
                    Bukkit.getServer().getLogger().info("record: " + rs.getInt("record"));
                }
            } catch (SQLException e) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "数据库查询错误", e);
            }
        }

        // 返回包含所有配置值的对象
        return new TpaConfig(enableTpa, debug, cooldown, reply, confirm, type, record, homecooldown);
    }

    public static long isOnCoolDown(String playeruuid, long cooldown, int reply) {
        // 获取现在的时间戳
        long now_time = System.currentTimeMillis();
        // 修改SQL以获取最近的一条记录（按id降序）
        String readSql = "SELECT * FROM TPA WHERE uuidfrom = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
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
        Player toPlayer = Bukkit.getPlayer(UUID.fromString(playerto)); // if tpa home => toPlayer == fromPlayer
        // 获取配置类型
        String type = loadTpa().getType();

        if(toPlayer != fromPlayer){
            // 根据类型执行不同的传送方式
            if (type.equalsIgnoreCase("absolute")) {
                // 完全复制目标位置（包括朝向）
                Location targetLocation = toPlayer.getLocation().clone();

                for (int i = 0; i < 500; i++) {
                    double x = fromPlayer.getLocation().getX() + Math.random() * 2 - 1;
                    double y = fromPlayer.getLocation().getY() + Math.random() * 2;
                    double z = fromPlayer.getLocation().getZ() + Math.random() * 2 - 1;
                    new ParticleBuilder(Particle.ENCHANT)
                            .location(fromPlayer.getLocation())
                            .count(1)
                            .spawn();
                }
                // 获取所有在线玩家
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    // 为每个在线玩家播放音效
                    onlinePlayer.playSound(fromPlayer.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 5f, 5f);
                }
                // 发光
                PotionEffect joinEffect = new PotionEffect(PotionEffectType.GLOWING,10,1,false,false);
                fromPlayer.addPotionEffect(joinEffect);

                fromPlayer.teleport(targetLocation);
                String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
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
            }
            else if (type.equalsIgnoreCase("near")) {
                // 完全复制目标位置（包括朝向）
                Location targetLocation = toPlayer.getLocation().clone();
                int centerX = (int) targetLocation.getX();
                int centerY = (int) targetLocation.getY();
                int centerZ = (int) targetLocation.getZ();
                Map<String, Boolean> pos = new HashMap<>();
                List<String> safePositions = new ArrayList<>();

                // 遍历 5×5×3 区域 (x和z方向各±2，y方向各±1)
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -2; dz <= 2; dz++) {
                            int checkX = centerX + dx;
                            int checkY = centerY + dy;
                            int checkZ = centerZ + dz;

                            int[] coordinates = {checkX, checkY, checkZ};
                            boolean isSafe = is_safe(toPlayer, coordinates);

                            // 使用坐标字符串作为键，避免数组引用问题
                            String key = checkX + "," + checkY + "," + checkZ;
                            pos.put(key, isSafe);

                            // 将安全位置添加到列表中（排除基准位置）
                            if (isSafe && (dx != 0 || dy != 0 || dz != 0)) {
                                safePositions.add(key);
                            }

                            if (loadTpa().isDebug()) {
                                Bukkit.getServer().getLogger().info("Checking: " + key + " - Safe: " + isSafe);
                            }
                        }
                    }
                }
                if (loadTpa().isDebug()) {
                    Bukkit.getServer().getLogger().info("Target Location: " + centerX + ", " + centerY + ", " + centerZ);
                    Bukkit.getServer().getLogger().info("Center is Safe? " + is_safe(toPlayer, new int[]{centerX, centerY, centerZ}));
                    Bukkit.getServer().getLogger().info("Total positions checked: " + pos.size());
                    // 统计安全位置数量
                    long safeCount = pos.values().stream().filter(Boolean::booleanValue).count();
                    Bukkit.getServer().getLogger().info("Safe positions: " + safeCount + "/" + pos.size());
                }
                // 选择安全位置
                Location selectedLocation = null;
                if (!safePositions.isEmpty()) {
                    // 随机选择一个安全位置
                    Random random = new Random();
                    String selectedKey = safePositions.get(random.nextInt(safePositions.size()));

                    // 解析坐标字符串
                    String[] coords = selectedKey.split(",");
                    int selectedX = Integer.parseInt(coords[0]);
                    int selectedY = Integer.parseInt(coords[1]);
                    int selectedZ = Integer.parseInt(coords[2]);

                    // 创建Location对象
                    selectedLocation = new Location(
                            targetLocation.getWorld(),
                            selectedX + 0.5, // 中心点
                            selectedY,
                            selectedZ + 0.5,
                            targetLocation.getYaw(),
                            targetLocation.getPitch()
                    );

                    if (loadTpa().isDebug()) {
                        Bukkit.getServer().getLogger().info("Selected safe location: " + selectedKey);
                    }

                    for (int i = 0; i < 500; i++) {
                        double x = fromPlayer.getLocation().getX() + Math.random() * 2 - 1;
                        double y = fromPlayer.getLocation().getY() + Math.random() * 2;
                        double z = fromPlayer.getLocation().getZ() + Math.random() * 2 - 1;
                        new ParticleBuilder(Particle.ENCHANT)
                                .location(fromPlayer.getLocation())
                                .count(1)
                                .spawn();
                    }
                    // 获取所有在线玩家
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        // 为每个在线玩家播放音效
                        onlinePlayer.playSound(fromPlayer.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 5f, 5f);
                    }
                    // 发光
                    PotionEffect joinEffect = new PotionEffect(PotionEffectType.GLOWING,10,1,false,false);
                    fromPlayer.addPotionEffect(joinEffect);

                    fromPlayer.teleport(selectedLocation);

                    String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";
                    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
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
                } else {
                    // 没有找到安全位置
                    if (loadTpa().isDebug()) {
                        Bukkit.getServer().getLogger().info("No safe location found around player");
                    }
                    Component message = Component.text("❎由于周围没有安全的位置而失败了!", TextColor.color(255, 0, 0));
                    fromPlayer.sendMessage(message);
                    toPlayer.sendMessage(message);
                }
                if (loadTpa().isDebug()) {
                    Bukkit.getServer().getLogger().info("Safe positions (excluding center): " + safePositions.size());

                    // 输出所有安全位置
                    if (!safePositions.isEmpty()) {
                        Bukkit.getServer().getLogger().info("Safe positions: " + String.join("; ", safePositions));
                    }
                }
            } else {
                Bukkit.getServer().getLogger().info("Tpa ERROR:配置文件传送方式设置错误！");
            }
            // 播放传送音效
            fromPlayer.playSound(fromPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            toPlayer.playSound(toPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            return true;
        }
        else{
            //tpa home

            Location homelocation;

            String uuid = fromPlayer.getUniqueId().toString();
            String url = "jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db";

            String selectSQL = "SELECT location, timestemp FROM HOME WHERE uuid = ?";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

                pstmt.setString(1, uuid);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String locationStr = rs.getString("location");
                    long timestamp = rs.getLong("timestemp");

                    // 解析位置字符串
                    Location location = parseLocationString(locationStr, fromPlayer);

                    HomeData home = new HomeData(location, timestamp);
                    homelocation = home.getLocation();
//                    settime = home.getTimestamp();

                } else {
                    fromPlayer.sendMessage(Component.text("❎你还没有设置家的位置！请使用/sethome设置！")
                            .color(TextColor.color(255, 0, 0)));
                    return true;
                }

            } catch (SQLException e) {
                fromPlayer.sendMessage(Component.text("❎查询家的位置时出现错误！")
                        .color(TextColor.color(255, 0, 0)));
                return true;
            }

            Location targetLocation = homelocation;
            Component PlayerMsg = Component.text("🏠你回到了家中!", TextColor.color(255, 255, 225));
            fromPlayer.sendMessage(PlayerMsg);

            for (int i = 0; i < 500; i++) {
                double x = fromPlayer.getLocation().getX() + Math.random() * 2 - 1;
                double y = fromPlayer.getLocation().getY() + Math.random() * 2;
                double z = fromPlayer.getLocation().getZ() + Math.random() * 2 - 1;
                new ParticleBuilder(Particle.ENCHANT)
                        .location(fromPlayer.getLocation())
                        .count(1)
                        .spawn();
            }
            // 获取所有在线玩家
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                // 为每个在线玩家播放音效
                onlinePlayer.playSound(fromPlayer.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 5f, 5f);
            }
            // 发光
            PotionEffect joinEffect = new PotionEffect(PotionEffectType.GLOWING,10,1,false,false);
            fromPlayer.addPotionEffect(joinEffect);

            fromPlayer.teleport(targetLocation);
            String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
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
            return true;
        }
    }
    public static boolean sethome(Player player){
        String uuid = player.getUniqueId().toString();

        // 检查冷却时间
        long homecooldown = loadTpa().getHomecooldown() * 1000; // 转换为毫秒
        long currentTime = System.currentTimeMillis();

        // 查询玩家最近一次设置家的时间
        String checkSQL = "SELECT timestemp FROM HOME WHERE uuid = ?";
        String url = "jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(checkSQL)) {

            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                long lastSetTime = rs.getLong("timestemp");
                long timeDifference = currentTime - lastSetTime;

                if (timeDifference < homecooldown) {
                    long remainingTime = (homecooldown - timeDifference) / 1000; // 转换为秒

                    String timeFormatted = formatTime(remainingTime);

                    Component message = Component.text("❎设置家的冷却时间还未到！剩余时间: ", TextColor.color(255, 0, 0))
                            .append(Component.text(timeFormatted, TextColor.color(255, 255, 0)));
                    player.sendMessage(message);
                    return false;
                }
            }

        } catch (SQLException e) {
            // 如果查询出错，仍然允许设置家
            if (loadTpa().isDebug()) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "查询家的冷却时间时出错", e);
            }
        }

        // 通过冷却检查
        Location loc = player.getLocation();
        int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();
        String locationStr = x + "," + y + "," + z;
        long timestamp = currentTime;

        String insertSQL = "INSERT INTO HOME (uuid, location, timestemp) VALUES (?, ?, ?) " +
                "ON CONFLICT(uuid) DO UPDATE SET location = excluded.location, timestemp = excluded.timestemp";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, uuid);
            pstmt.setString(2, locationStr);
            pstmt.setLong(3, timestamp);

            pstmt.executeUpdate();
            Component message = Component.text("🏡家已设置!: ", TextColor.color(255, 255, 255))
                    .append(Component.text("(" + x + "," + y + "," + z + ")", TextColor.color(0, 255, 0)));
            player.sendMessage(message);

            // 如果是debug模式，记录设置时间
            if (loadTpa().isDebug()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeStr = sdf.format(new Date(timestamp));
                Bukkit.getServer().getLogger().info("玩家 " + player.getName() + " 在 " + timeStr + " 设置家");
            }

            return true;
        } catch (SQLException e) {
            Component message = Component.text("❎设置家时出现错误！", TextColor.color(255, 0, 0));
            player.sendMessage(message);
            if (loadTpa().isDebug()) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "设置家时数据库错误", e);
            }
            return false;
        }
    }
    private static String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + "秒";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes + "分" + (remainingSeconds > 0 ? remainingSeconds + "秒" : "");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "小时" + (minutes > 0 ? minutes + "分" : "");
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            return days + "天" + (hours > 0 ? hours + "小时" : "");
        }
    }
    private static boolean is_safe(Player toplayer, int[] position){
        World world = toplayer.getLocation().getWorld();
        Location location = new Location(world, position[0], position[1]-1, position[2]);
        Block block = world.getBlockAt(location);
        Material type = block.getType();

        if (isDangerousBlock(type)) {
            return false;
        }

        if(type.isSolid() || type == Material.WATER){
            Material upperBlock = world.getBlockAt(location.add(0, 1, 0)).getType();
            Material upperupperBlock = world.getBlockAt(location.add(0, 2, 0)).getType();
            if(upperBlock == Material.AIR || upperBlock ==Material.CAVE_AIR || upperBlock == Material.VOID_AIR || upperBlock == Material.WATER){
                return upperupperBlock == Material.AIR || upperupperBlock == Material.CAVE_AIR || upperupperBlock == Material.VOID_AIR || upperupperBlock == Material.WATER;
            }
        }
        else {
            return false;
        }
        return false;
    }
    private static boolean isDangerousBlock(Material material) {
        // 所有危险方块的类型
        return material == Material.LAVA ||
                material == Material.MAGMA_BLOCK ||
                material == Material.CACTUS ||
                material == Material.FIRE ||
                material == Material.SOUL_FIRE ||
                material == Material.CAMPFIRE ||
                material == Material.SOUL_CAMPFIRE ||
                material == Material.END_PORTAL ||
                material == Material.END_PORTAL_FRAME || // 虽然框架不是危险，但通常我们可能也排除，根据需求调整
                material == Material.NETHER_PORTAL ||
                material == Material.WITHER_ROSE ||
                material == Material.SWEET_BERRY_BUSH ||
                material == Material.POWDER_SNOW || // 陷进去的方块
                material == Material.LAVA_CAULDRON;
    }
    private static Location parseLocationString(String locationStr, Player player) {
        try {
            // 假设格式为 "world:x,y,z" 或 "x,y,z"
            String[] parts = locationStr.split(":");
            World world;
            String coordsStr;

            if (parts.length == 2) {
                // 格式为 "world:x,y,z"
                String worldName = parts[0];
                world = Bukkit.getWorld(worldName);
                coordsStr = parts[1];
            } else {
                // 格式为 "x,y,z" - 使用玩家当前世界
                world = player.getWorld();
                coordsStr = locationStr;
            }

            if (world == null) {
                world = player.getWorld(); // 如果世界不存在，使用玩家当前世界
            }

            // 解析坐标
            String[] coords = coordsStr.split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            double z = Double.parseDouble(coords[2]);

            return new Location(world, x, y, z);

        } catch (Exception e) {
//            System.out.println("解析位置字符串时出错: " + e.getMessage());
            return null;
        }
    }
}
class HomeData {
    private Location location;
    private long timestamp;

    public HomeData(Location location, long timestamp) {
        this.location = location;
        this.timestamp = timestamp;
    }

    // Getters
    public Location getLocation() { return location; }
    public long getTimestamp() { return timestamp; }

    // 获取格式化的时间字符串
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
}