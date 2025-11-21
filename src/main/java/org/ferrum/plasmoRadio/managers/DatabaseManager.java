package org.ferrum.plasmoRadio.managers;

import com.google.gson.Gson;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.blocks.Locator;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.RadioBlockType;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;

import java.io.File;
import java.sql.*;
import java.util.Map;

public class DatabaseManager {

    private static File dbFile;
    private static Connection connection;

    public static void init() {
        initRadioLocation();
    }

    private static void initRadioLocation() {
        dbFile = new File(PlasmoRadio.plugin.getDataFolder(), "radioData.db");

        try {
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS radio_data (
                            world TEXT,
                            x INTEGER,
                            y INTEGER,
                            z INTEGER,
                            block_type TINYINT,
                            frequency REAL,
                            options TEXT,
                            PRIMARY KEY (world, x, y, z)
                        );
                """);
            }

        } catch (Exception e) {
            PlasmoRadio.log("Error: " + e.getMessage());
        }
    }

    // -------------------------- SAVE --------------------------
    public static void saveBlock(Location loc, byte blockType, Map<String, String> options) {
        String sql = """
            REPLACE INTO radio_data (world, x, y, z, block_type, options)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());
            stmt.setByte(5, blockType);

            String json = new Gson().toJson(options);
            stmt.setString(6, json);

            stmt.executeUpdate();
        } catch (Exception e) {
            PlasmoRadio.log("Error: " + e.getMessage());
        }
    }

    public static void updateOptions(Location loc, Map<String, String> options) {
        String sql = "UPDATE radio_data SET options = ? WHERE world = ? AND x = ? AND y = ? AND z = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, new Gson().toJson(options));
            stmt.setString(2, loc.getWorld().getName());
            stmt.setInt(3, loc.getBlockX());
            stmt.setInt(4, loc.getBlockY());
            stmt.setInt(5, loc.getBlockZ());
            stmt.executeUpdate();
        } catch (SQLException e) {
            PlasmoRadio.log("Error: " + e.getMessage());
        }
    }

    // -------------------------- UPDATE --------------------------

//    public static void updateFrequency(Location loc, Float newFrequency) {
//        String sql = "UPDATE radio_data SET frequency = ? WHERE world = ? AND x = ? AND y = ? AND z = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setFloat(1, newFrequency);
//            stmt.setString(2, loc.getWorld().getName());
//            stmt.setInt(3, loc.getBlockX());
//            stmt.setInt(4, loc.getBlockY());
//            stmt.setInt(5, loc.getBlockZ());
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            PlasmoRadio.log("Error: " + e.getMessage());
//        }
//    }
//
//    public static void updatePassword(Location loc, String newPassword) {
//        String sql = "UPDATE radio_data SET password = ? WHERE world = ? AND x = ? AND y = ? AND z = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, newPassword);
//            stmt.setString(2, loc.getWorld().getName());
//            stmt.setInt(3, loc.getBlockX());
//            stmt.setInt(4, loc.getBlockY());
//            stmt.setInt(5, loc.getBlockZ());
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            PlasmoRadio.log("Error: " + e.getMessage());
//        }
//    }
//
//    public static void updateUsePassword(Location loc, Boolean usePassword) {
//        String sql = "UPDATE radio_data SET use_password = ? WHERE world = ? AND x = ? AND y = ? AND z = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setBoolean(1, usePassword);
//            stmt.setString(2, loc.getWorld().getName());
//            stmt.setInt(3, loc.getBlockX());
//            stmt.setInt(4, loc.getBlockY());
//            stmt.setInt(5, loc.getBlockZ());
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            PlasmoRadio.log("Error: " + e.getMessage());
//        }
//    }


    // -------------------------- REMOVE --------------------------
    public static void removeBlock(Location loc) {
        String sql = "DELETE FROM radio_data WHERE world = ? AND x = ? AND y = ? AND z = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());

            stmt.executeUpdate();
            PlasmoRadio.log("Удалён блок " + loc);

        } catch (SQLException e) {
            PlasmoRadio.log("Error: " + e.getMessage());
        }
    }

    // -------------------------- LOAD --------------------------
    public static void loadBlocksInChunk(Chunk chunk) {
        String sql = "SELECT x, y, z, block_type, options FROM radio_data " +
                "WHERE world = ? AND x BETWEEN ? AND ? AND z BETWEEN ? AND ?";

        int minX = chunk.getX() << 4;
        int maxX = minX + 15;
        int minZ = chunk.getZ() << 4;
        int maxZ = minZ + 15;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, chunk.getWorld().getName());
            stmt.setInt(2, minX);
            stmt.setInt(3, maxX);
            stmt.setInt(4, minZ);
            stmt.setInt(5, maxZ);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    Map<String, String> options = new Gson().fromJson(rs.getString("options"), Map.class);

                    Location loc = new Location(chunk.getWorld(), x, y, z);
                    RadioBlockType blockType = RadioBlockType.fromId((byte) rs.getInt("block_type"));

                    switch (blockType) {
                        case Speaker -> RadioDeviceRegistry.register(loc, new Speaker(loc, options));
                        case Microphone -> RadioDeviceRegistry.register(loc, new Microphone(loc, options));
                        case Locator -> RadioDeviceRegistry.register(loc, new Locator(loc, options));
                    }
                }
            }

        } catch (SQLException e) {
            PlasmoRadio.log("Error: " + e.getMessage());
        }
    }
}
