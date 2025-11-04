package me.solar.apolloLibrary.config;

import me.solar.apolloLibrary.world.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ConfigFormat(FormatType.YAML)
class SerializerNestedSerializerTest {

    private static final Path TEMP = Paths.get("temp-nested.yml");

    @BeforeEach
    void setup() throws Exception {
        Files.deleteIfExists(TEMP);

        ServerMock serverMock = MockBukkit.mock();
        World test = serverMock.createWorld(WorldCreator.name("test"));

        // create a region with Locations (world can be null for serialization of coords)
        Location min = new Location(test, 1.0, 2.0, 3.0);
        Location max = new Location(test, 4.0, 5.0, 6.0);
        TestConfig.region = new CuboidRegion(min, max);
    }

    @AfterEach
    void cleanup() throws Exception {
        Files.deleteIfExists(TEMP);
    }

    @Test
    void cuboidRegionSerializer_should_serialize_nested_locations_via_locationSerializer() throws Exception {
        StaticConfigHandler.saveConfig(TestConfig.class, TEMP);
        assertTrue(Files.exists(TEMP));

        String yaml = Files.readString(TEMP).replace("\r\n", "\n");

        // No Java/Bukkit tags should appear
        assertFalse(yaml.contains("!!"), "YAML contains Java type tags");
        assertFalse(yaml.contains("==:"), "YAML contains tag-style markers like '==:'");

        // Coordinates of nested locations should be present as plain map entries
        assertTrue(yaml.contains("x: 4.0"));
        assertTrue(yaml.contains("y: 5.0"));
        assertTrue(yaml.contains("z: 6.0"));
        assertTrue(yaml.contains("x: 1.0"));
        assertTrue(yaml.contains("y: 2.0"));
        assertTrue(yaml.contains("z: 3.0"));
    }

    @Test
    void loadConfig_should_deserialize_cuboidRegion_from_yaml() throws Exception {
        // First, save a known configuration to file
        ServerMock serverMock = MockBukkit.getOrCreateMock();
        World testWorld = serverMock.createWorld(WorldCreator.name("test"));

        Location min = new Location(testWorld, 10.0, 20.0, 30.0);
        Location max = new Location(testWorld, 40.0, 50.0, 60.0);
        TestConfig.region = new CuboidRegion(min, max);

        StaticConfigHandler.saveConfig(TestConfig.class, TEMP);

        // Now modify the static field to different values
        TestConfig.region = null;

        // Load the configuration back from file
        StaticConfigHandler.loadConfig(TestConfig.class, TEMP);

        // Verify the loaded values match what we originally saved
        assertNotNull(TestConfig.region, "Region should not be null after loading");

        Location loadedMin = TestConfig.region.getPointA();
        Location loadedMax = TestConfig.region.getPointB();

        assertNotNull(loadedMin, "Min location should not be null");
        assertNotNull(loadedMax, "Max location should not be null");

        // Check coordinates
        assertEquals(10.0, loadedMin.getX(), 0.001, "Min X coordinate mismatch");
        assertEquals(20.0, loadedMin.getY(), 0.001, "Min Y coordinate mismatch");
        assertEquals(30.0, loadedMin.getZ(), 0.001, "Min Z coordinate mismatch");

        assertEquals(40.0, loadedMax.getX(), 0.001, "Max X coordinate mismatch");
        assertEquals(50.0, loadedMax.getY(), 0.001, "Max Y coordinate mismatch");
        assertEquals(60.0, loadedMax.getZ(), 0.001, "Max Z coordinate mismatch");

        // Optionally verify world name if your serializer saves it
        if (loadedMin.getWorld() != null) {
            assertEquals("test", loadedMin.getWorld().getName(), "World name mismatch");
        }
    }

    // simple static config to exercise serializer chaining
    @ConfigFormat(FormatType.YAML)
    static class TestConfig {
        @ConfigKey(value = "region")
        public static CuboidRegion region = new CuboidRegion(new Location(null, 1.0, 2.0, 3.0),
                new Location(null, 4.0, 5.0, 6.0));
    }
}