package com.spider.cpu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
CREATE TABLE cpu_info (
    cpuId INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    performanceRank VARCHAR(255),
    score VARCHAR(255),
    tdp VARCHAR(255),
    tdpDown VARCHAR(255),
    slotType VARCHAR(255),
    coreNumber VARCHAR(255),
    threadNumber VARCHAR(255),
    frequency VARCHAR(255),
    turboFrequency VARCHAR(255),
    released VARCHAR(255),
    otherName VARCHAR(255),
    initialPrice VARCHAR(255),
    historyLowPrice VARCHAR(255),
    latestPrice VARCHAR(255)
);

 */
public class CpuDAO {

    // 数据库连接信息
    private static final String URL = "jdbc:mysql://localhost:3306/root?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // 创建新的CPU记录
    public void create(CpuInfoBean cpu) throws SQLException {
        String sql = "INSERT INTO cpus (cpu_id, name, performance_rank) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cpu.getCpuId());
            pstmt.setString(2, cpu.getName());
            pstmt.setString(3, cpu.getPerformanceRank());
            pstmt.executeUpdate();
        }
    }

    // 根据CPU ID读取CPU记录
    public CpuInfoBean read(String cpuId) throws SQLException {
        String sql = "SELECT * FROM cpus WHERE cpu_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new CpuInfoBean(
                        rs.getInt("cpu_id"),
                        rs.getString("name"),
                        rs.getString("performance_rank")
                    );
                }
            }
        }
        return null;
    }

    // 更新现有CPU记录
    public boolean update(CpuInfoBean cpu) throws SQLException {
        String sql = "UPDATE cpus SET name = ?, performance_rank = ? WHERE cpu_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpu.getName());
            pstmt.setString(2, cpu.getPerformanceRank());
            pstmt.setInt(3, cpu.getCpuId());
            return pstmt.executeUpdate() > 0;
        }
    }

    // 删除CPU记录
    public boolean delete(String cpuId) throws SQLException {
        String sql = "DELETE FROM cpus WHERE cpu_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpuId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // 获取所有CPU记录
    public List<CpuInfoBean> findAll() throws SQLException {
        String sql = "SELECT * FROM cpus";
        List<CpuInfoBean> cpus = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cpus.add(new CpuInfoBean(
                    rs.getInt("cpu_id"),
                    rs.getString("name"),
                    rs.getString("performance_rank")
                ));
            }
        }
        return cpus;
    }
}