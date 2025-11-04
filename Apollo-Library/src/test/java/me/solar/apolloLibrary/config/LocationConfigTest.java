// java
package me.solar.apolloLibrary.config;

import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class LocationConfigTest {

    private static final Path TEMP_FILE = Paths.get("temp-location.yml");

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(TEMP_FILE);

        // initialize defaults
        TestLocationConfig.location = new Location(null, 1.5, 2.5, -3.5, 45f, 10f);
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(TEMP_FILE);
    }

    @Test
    void testSaveLocationYaml() throws Exception {
        StaticConfigHandler.saveConfig(TestLocationConfig.class, TEMP_FILE);

        assertTrue(Files.exists(TEMP_FILE));
        String yamlContent = Files.readString(TEMP_FILE);

        Path expectedPath = Paths.get("src", "test", "resources", "expected-location.yml");
        String expected = Files.readString(expectedPath).replace("\r\n", "\n").trim();
        String actual = yamlContent.replace("\r\n", "\n").trim();
        assertEquals(expected, actual, "Saved YAML must exactly match expected YAML (no extra text).");
    }

    @Test
    void testLoadLocationYaml() throws Exception {
        // write known state
        StaticConfigHandler.saveConfig(TestLocationConfig.class, TEMP_FILE);

        // change values to non-defaults
        TestLocationConfig.location = new Location(null, 0.0, 0.0, 0.0, 0f, 0f);

        // load from file and ensure values restored
        StaticConfigHandler.loadConfig(TestLocationConfig.class, TEMP_FILE);
        System.out.println(TestLocationConfig.location);

        Location loc = TestLocationConfig.location;
        assertNotNull(loc);
        assertEquals(1.5, loc.getX(), 1e-6);
        assertEquals(2.5, loc.getY(), 1e-6);
        assertEquals(-3.5, loc.getZ(), 1e-6);
        assertEquals(45.0f, loc.getYaw(), 1e-6);
        assertEquals(10.0f, loc.getPitch(), 1e-6);
    }

    @ConfigFormat(FormatType.YAML)
    static class TestLocationConfig {
        @ConfigKey(value = "location", comment = "A location")
        public static Location location;
    }
}