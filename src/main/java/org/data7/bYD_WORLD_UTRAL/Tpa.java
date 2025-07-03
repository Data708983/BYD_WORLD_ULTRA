package org.data7.bYD_WORLD_UTRAL;

import java.sql.*;

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
            System.out.println("表创建成功（如果不存在）");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // 插入数据的SQL语句
        String insertSql = "INSERT INTO TPA (uuidfrom, uuidto, timestemp, available) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/BYD_WORLD_UTRAL/database.db");
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            // 设置插入的数据
            pstmt.setString(1, "asdasdasdasd");
            pstmt.setString(2, "zxczxczxczcx");
            pstmt.setInt(3, 114514);
            pstmt.setInt(4, 1);

            /*用于测试*/
            // 执行插入操作
            pstmt.executeUpdate();
            System.out.println("数据插入成功");

            // 读取数据的SQL语句
            String readSql = "SELECT * FROM TPA";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(readSql)) {
                // 输出读取的数据
                while (rs.next()) {
                    System.out.println("id: " + rs.getInt("id"));
                    System.out.println("uuidfrom: " + rs.getString("uuidfrom"));
                    System.out.println("uuidto: " + rs.getString("uuidto"));
                    System.out.println("timestemp: " + rs.getInt("timestemp"));
                    System.out.println("available: " + rs.getInt("available"));
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
