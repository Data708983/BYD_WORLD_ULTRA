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
            stmt.execute(sql);
        } catch (SQLException e) {
        }
        String sqlofHome = "CREATE TABLE IF NOT EXISTS HOME " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "uuid TEXT NOT NULL UNIQUE, " +
                "world TEXT NOT NULL, " +    // 新增世界字段
                "x REAL NOT NULL, " +        // x坐标
                "y REAL NOT NULL, " +        // y坐标
                "z REAL NOT NULL, " +        // z坐标
                "yaw REAL, " +               // 朝向yaw
                "pitch REAL, " +             // 朝向pitch
                "timestemp INTEGER);";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlofHome);
        } catch (SQLException e) {
        }

        loadTpa();
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

        if (!tpaFile.exists()) {
            plugin.saveResource("tpa.yml", false);
            tpaConfig = YamlConfiguration.loadConfiguration(tpaFile);
        }

        boolean enableTpa = tpaConfig.getBoolean("enable", true);
        boolean debug = tpaConfig.getBoolean("debug", false);
        long cooldown = tpaConfig.getLong("cooldown", 60);
        int reply = tpaConfig.getInt("reply", 60);
        boolean confirm = tpaConfig.getBoolean("confirm", true);
        String type = tpaConfig.getString("type", "teleport");
        int record = tpaConfig.getInt("record", 20);
        long homecooldown = tpaConfig.getLong("homecooldown",86400);

        return new TpaConfig(enableTpa, debug, cooldown, reply, confirm, type, record, homecooldown);
    }

    public static long isOnCoolDown(String playeruuid, long cooldown, int reply) {
        long now_time = System.currentTimeMillis();
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
        long now_time = System.currentTimeMillis();
        Player fromPlayer = Bukkit.getPlayer(UUID.fromString(playerfrom));
        Player toPlayer = Bukkit.getPlayer(UUID.fromString(playerto));
        String type = loadTpa().getType();

        if(toPlayer != fromPlayer){
            if (type.equalsIgnoreCase("absolute")) {
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
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.playSound(fromPlayer.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 5f, 5f);
                }
                PotionEffect joinEffect = new PotionEffect(PotionEffectType.GLOWING,10,1,false,false);
                fromPlayer.addPotionEffect(joinEffect);

                fromPlayer.teleport(targetLocation);
                String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
                     PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
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
                Location targetLocation = toPlayer.getLocation().clone();
                int centerX = (int) targetLocation.getX();
                int centerY = (int) targetLocation.getY();
                int centerZ = (int) targetLocation.getZ();
                Map<String, Boolean> pos = new HashMap<>();
                List<String> safePositions = new ArrayList<>();

                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -2; dz <= 2; dz++) {
                            int checkX = centerX + dx;
                            int checkY = centerY + dy;
                            int checkZ = centerZ + dz;

                            int[] coordinates = {checkX, checkY, checkZ};
                            boolean isSafe = is_safe(toPlayer, coordinates);

                            String key = checkX + "," + checkY + "," + checkZ;
                            pos.put(key, isSafe);

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
                    long safeCount = pos.values().stream().filter(Boolean::booleanValue).count();
                    Bukkit.getServer().getLogger().info("Safe positions: " + safeCount + "/" + pos.size());
                }

                Location selectedLocation = null;
                if (!safePositions.isEmpty()) {
                    Random random = new Random();
                    String selectedKey = safePositions.get(random.nextInt(safePositions.size()));

                    String[] coords = selectedKey.split(",");
                    int selectedX = Integer.parseInt(coords[0]);
                    int selectedY = Integer.parseInt(coords[1]);
                    int selectedZ = Integer.parseInt(coords[2]);

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
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.playSound(fromPlayer.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 5f, 5f);
                    }
                    PotionEffect joinEffect = new PotionEffect(PotionEffectType.GLOWING,10,1,false,false);
                    fromPlayer.addPotionEffect(joinEffect);

                    fromPlayer.teleport(selectedLocation);

                    String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";
                    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
                         PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        pstmt.setString(1, playerfrom);
                        pstmt.setString(2, playerto);
                        pstmt.setLong(3, now_time);
                        pstmt.setInt(4, 1);
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    if (loadTpa().isDebug()) {
                        Bukkit.getServer().getLogger().info("No safe location found around player");
                    }
                    Component message = Component.text("❎由于周围没有安全的位置而失败了!", TextColor.color(255, 0, 0));
                    fromPlayer.sendMessage(message);
                    toPlayer.sendMessage(message);
                }
                if (loadTpa().isDebug()) {
                    Bukkit.getServer().getLogger().info("Safe positions (excluding center): " + safePositions.size());
                    if (!safePositions.isEmpty()) {
                        Bukkit.getServer().getLogger().info("Safe positions: " + String.join("; ", safePositions));
                    }
                }
            } else {
                Bukkit.getServer().getLogger().info("Tpa ERROR:配置文件传送方式设置错误！");
            }
            fromPlayer.playSound(fromPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            toPlayer.playSound(toPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            return true;
        }
        else{
            //tpa home

            Location homelocation;

            String uuid = fromPlayer.getUniqueId().toString();
            String url = "jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db";
            String selectSQL = "SELECT world, x, y, z, yaw, pitch FROM HOME WHERE uuid = ?";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

                pstmt.setString(1, uuid);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String worldName = rs.getString("world");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    float yaw = rs.getFloat("yaw");
                    float pitch = rs.getFloat("pitch");

                    // 获取世界对象
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        fromPlayer.sendMessage(Component.text("❎家的世界不存在或未加载！")
                                .color(TextColor.color(255, 0, 0)));
                        return false;
                    }

                    // 创建完整的位置对象
                    homelocation = new Location(world, x, y, z, yaw, pitch);

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
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(fromPlayer.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 5f, 5f);
            }
            PotionEffect joinEffect = new PotionEffect(PotionEffectType.GLOWING,10,1,false,false);
            fromPlayer.addPotionEffect(joinEffect);

            fromPlayer.teleport(targetLocation);
            String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_ULTRA/database.db");
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
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

        long homecooldown = loadTpa().getHomecooldown() * 1000;
        long currentTime = System.currentTimeMillis();

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
                    long remainingTime = (homecooldown - timeDifference) / 1000;
                    String timeFormatted = formatTime(remainingTime);
                    Component message = Component.text("❎设置家的冷却时间还未到！剩余时间: ", TextColor.color(255, 0, 0))
                            .append(Component.text(timeFormatted, TextColor.color(255, 255, 0)));
                    player.sendMessage(message);
                    return false;
                }
            }
        } catch (SQLException e) {
            if (loadTpa().isDebug()) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "查询家的冷却时间时出错", e);
            }
        }

        Location loc = player.getLocation();
        String worldName = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        long timestamp = currentTime;

        // 修改插入语句，包含所有位置字段
        String insertSQL = "INSERT INTO HOME (uuid, world, x, y, z, yaw, pitch, timestemp) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(uuid) DO UPDATE SET world = excluded.world, x = excluded.x, y = excluded.y, z = excluded.z, yaw = excluded.yaw, pitch = excluded.pitch, timestemp = excluded.timestemp";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, uuid);
            pstmt.setString(2, worldName);
            pstmt.setDouble(3, x);
            pstmt.setDouble(4, y);
            pstmt.setDouble(5, z);
            pstmt.setFloat(6, yaw);
            pstmt.setFloat(7, pitch);
            pstmt.setLong(8, timestamp);

            pstmt.executeUpdate();
            Component message = Component.text("🏡家已设置!: ", TextColor.color(255, 255, 255))
                    .append(Component.text("世界: " + worldName + " (" + (int)x + "," + (int)y + "," + (int)z + ")", TextColor.color(0, 255, 0)));
            player.sendMessage(message);

            if (loadTpa().isDebug()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeStr = sdf.format(new Date(timestamp));
                Bukkit.getServer().getLogger().info(player.getName() + " 在 " + timeStr + " 设置家");
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
        return material == Material.LAVA ||
                material == Material.MAGMA_BLOCK ||
                material == Material.CACTUS ||
                material == Material.FIRE ||
                material == Material.SOUL_FIRE ||
                material == Material.CAMPFIRE ||
                material == Material.SOUL_CAMPFIRE ||
                material == Material.END_PORTAL ||
                material == Material.END_PORTAL_FRAME ||
                material == Material.NETHER_PORTAL ||
                material == Material.WITHER_ROSE ||
                material == Material.SWEET_BERRY_BUSH ||
                material == Material.POWDER_SNOW ||
                material == Material.LAVA_CAULDRON;
    }
}
class HomeData {
    private Location location;
    private long timestamp;

    public HomeData(Location location, long timestamp) {
        this.location = location;
        this.timestamp = timestamp;
    }

    public Location getLocation() { return location; }
    public long getTimestamp() { return timestamp; }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
}