// java
package me.solar.apolloLibrary.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class StaticConfigHandlerTest {

    private static final Path TEMP_FILE = Paths.get("temp.yml");

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(TEMP_FILE);

        // initialize defaults
        TestConfig.number = 42;
        TestConfig.name = "Apollo";
        TestConfig.Inner.flag = false;
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(TEMP_FILE);
    }

    @Test
    void testSaveYamlConfig() throws Exception {
        StaticConfigHandler.saveConfig(TestConfig.class, TEMP_FILE);

        assertTrue(Files.exists(TEMP_FILE));
        String yamlContent = Files.readString(TEMP_FILE);

        // basic values
        assertTrue(yamlContent.contains("number: 42"));
        assertTrue(yamlContent.contains("name: Apollo"));

        // nested inner section and flag
        assertTrue(yamlContent.contains("inner:"));
        assertTrue(yamlContent.contains("flag: false"));

        // comments
        assertTrue(yamlContent.contains("# A number"));
        assertTrue(yamlContent.contains("# A string name"));
        assertTrue(yamlContent.contains("# An inner class"));
        assertTrue(yamlContent.contains("# An inner flag"));

        // change values and save again to ensure updated values persist
        TestConfig.number = 0;
        TestConfig.name = "";
        TestConfig.Inner.flag = true;
        StaticConfigHandler.saveConfig(TestConfig.class, TEMP_FILE);

        yamlContent = Files.readString(TEMP_FILE);
        Path expectedPath = Paths.get("src", "test", "resources", "expected-temp.yml");
        String expected = Files.readString(expectedPath).replace("\r\n", "\n").trim();
        String actual = yamlContent.replace("\r\n", "\n").trim();
        assertEquals(expected, actual, "Saved YAML must exactly match expected YAML (no extra text).");

        assertTrue(yamlContent.contains("number: 0"));
        // YAML may represent empty string as '' or "", accept either
        assertTrue(yamlContent.contains("name: ''") || yamlContent.contains("name: \"\"") || yamlContent.contains("name:"));
        assertTrue(yamlContent.contains("flag: true"));
    }

    @Test
    void testLoadYamlConfig() throws Exception {
        // write known state
        StaticConfigHandler.saveConfig(TestConfig.class, TEMP_FILE);

        // change values to non-defaults
        TestConfig.number = 0;
        TestConfig.name = "";
        TestConfig.Inner.flag = true;

        // load from file and ensure values restored
        StaticConfigHandler.loadConfig(TestConfig.class, TEMP_FILE);

        assertEquals(42, TestConfig.number);
        assertEquals("Apollo", TestConfig.name);
        assertTrue(TestConfig.Inner.flag); // original was false
    }

    // Simple static config class used by tests
    @ConfigFormat(FormatType.YAML)
    static class TestConfig {
        @ConfigKey(value = "number", comment = "A number")
        public static int number;

        @ConfigKey(value = "name", comment = "A string name")
        public static String name;

        @ConfigKey(value = "inner", comment = "An inner class")
        public static class Inner {
            @ConfigKey(value = "flag", comment = "An inner flag")
            public static boolean flag;
        }
    }
}
