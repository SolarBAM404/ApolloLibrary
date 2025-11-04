package me.solar.apolloLibrary.config;

import me.solar.apolloLibrary.world.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListNestedConfigTest {

    private static final Path TEMP_FILE = Paths.get("temp-list.yml");

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(TEMP_FILE);

        // initialize defaults
        TestListConfig.maps = new ArrayList<>();

        MockBukkit.mock();
        World world = Mockito.mock(World.class);
        Mockito.when(world.getName()).thenReturn("world");

        GameMap gm = new GameMap();
        gm.name = "Arena";
        gm.region = new CuboidRegion();
        gm.region.setPointA(new Location(world, 1.0, 2.0, 3.0, 0f, 0f));
        gm.region.setPointB(new Location(world, 4.0, 5.0, 6.0, 0f, 0f));

        GameMap gm2 = new GameMap();
        gm2.name = "Arena";
        gm2.region = new CuboidRegion();
        gm2.region.setPointA(new Location(world, 1.0, 2.0, 3.0, 0f, 0f));
        gm2.region.setPointB(new Location(world, 4.0, 5.0, 6.0, 0f, 0f));

        TestListConfig.maps.add(gm);
        TestListConfig.maps.add(gm2);
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(TEMP_FILE);
        MockBukkit.unmock();
    }

    @Test
    void testSaveListYamlExactMatch() throws Exception {
        StaticConfigHandler.saveConfig(TestListConfig.class, TEMP_FILE);

        assertTrue(Files.exists(TEMP_FILE));
        String yamlContent = Files.readString(TEMP_FILE);

        Path expectedPath = Paths.get("src", "test", "resources", "expected-list.yml");
        String expected = Files.readString(expectedPath).replace("\r\n", "\n").trim();
        String actual = yamlContent.replace("\r\n", "\n").trim();
        assertEquals(expected, actual, "Saved YAML must exactly match expected YAML (no extra text).");
    }

    @Test
    void testLoadListYamlRestoresValues() throws Exception {
        // write known state
        StaticConfigHandler.saveConfig(TestListConfig.class, TEMP_FILE);

        // change values to non-defaults
        TestListConfig.maps = new ArrayList<>();

        // load from file and ensure values restored
        StaticConfigHandler.loadConfig(TestListConfig.class, TEMP_FILE);

        assertNotNull(TestListConfig.maps);
        assertEquals(2, TestListConfig.maps.size());

        GameMap gm = TestListConfig.maps.getFirst();
        assertEquals("Arena", gm.name);

        assertNotNull(gm.region);
        assertNotNull(gm.region.getPointA());
        assertNotNull(gm.region.getPointB());

        assertEquals(1.0, gm.region.getPointA().getX(), 1e-6);
        assertEquals(2.0, gm.region.getPointA().getY(), 1e-6);
        assertEquals(3.0, gm.region.getPointA().getZ(), 1e-6);

        assertEquals(4.0, gm.region.getPointB().getX(), 1e-6);
        assertEquals(5.0, gm.region.getPointB().getY(), 1e-6);
        assertEquals(6.0, gm.region.getPointB().getZ(), 1e-6);
    }

    @ConfigFormat(FormatType.YAML)
    static class TestListConfig {
        @ConfigKey(value = "maps")
        public static List<GameMap> maps;
    }

    @ConfigFormat(FormatType.YAML)
    static class GameMap {
        @ConfigKey(value = "name")
        public String name;

        @ConfigKey(value = "region")
        public CuboidRegion region;

        // no-arg constructor required for deserialization
        public GameMap() {}
    }
}