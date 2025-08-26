package org.ferrum.plasmoRadio;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.blocks.Locator;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.RadioBlockType;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;

import java.io.File;
import java.sql.*;

public class DatabaseManager {

    private static File dbFile;
    private static Connection connection;

    public static void init() {
        initRadioLocation();
    }

    private static void initRadioLocation() {
        dbFile = new File(PlasmoRadio.plugin.getDataFolder(), "radioLocations.db");

        try {
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS radio_location (
                        world TEXT,
                        x INTEGER,
                        y INTEGER,
                        z INTEGER,
                        block_type TINYINT,
                        frequency REAL,
                        PRIMARY KEY (world, x, y, z)
                    );
                """);
            }

        } catch (Exception e) {
            PlasmoRadio.log("Error: " + e.getMessage());
        }
    }

    public static void saveBlock(Location loc, byte blockType, float frequency) {
        String sql = "REPLACE INTO radio_location (world, x, y, z, block_type, frequency) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());
            stmt.setByte(5, blockType);
            stmt.setFloat(6, frequency);

            stmt.executeUpdate();

            PlasmoRadio.log("Сохранил " + blockType);

        } catch (SQLException e) {
            PlasmoRadio.log("Error: " + e.getMessage());
        }
    }


    public static void removeBlock(Location loc) {
        String sql = "DELETE FROM radio_location WHERE world = ? AND x = ? AND y = ? AND z = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());

            PlasmoRadio.log("Удалил ");

            stmt.executeUpdate();
        } catch (SQLException e) {
            PlasmoRadio.log("Error: " + e.getMessage());
        }
    }

    public static void loadBlocksInChunk(Chunk chunk) {

        String sql = "SELECT x, y, z, block_type, frequency FROM radio_location WHERE world = ? AND x BETWEEN ? AND ? AND z BETWEEN ? AND ?";
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

                    Location loc = new Location(chunk.getWorld(), x, y, z);
                    RadioBlockType blockType = RadioBlockType.fromId((byte) rs.getInt("block_type"));


                    switch (blockType) {
                        case Speaker -> {
                            RadioDeviceRegistry.register(loc, new Speaker(loc, rs.getFloat("frequency")));
                            //PlasmoRadio.log("загружен спик");
                        }
                        case Microphone -> {
                            RadioDeviceRegistry.register(loc, new Microphone(loc, rs.getFloat("frequency")));
                            //PlasmoRadio.log("загружен мик");
                        }
                        case Locator -> {
                            RadioDeviceRegistry.register(loc, new Locator(loc, rs.getFloat("frequency")));
                            //PlasmoRadio.log("загружен locator");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            PlasmoRadio.log("Error: " + e.getMessage());
        }
    }
}
