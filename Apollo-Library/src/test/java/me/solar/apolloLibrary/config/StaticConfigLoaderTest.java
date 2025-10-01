package me.solar.apolloLibrary.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class StaticConfigLoaderTest {

    private static final Path TEMP_FILE = Paths.get("temp.yml");

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(TEMP_FILE);

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
        StaticConfigLoader.saveConfig(TestConfig.class, TEMP_FILE);

        // Verify the file exists with correct contents
        assertTrue(Files.exists(TEMP_FILE));
        String yamlContent = Files.readString(TEMP_FILE);
        System.out.println(yamlContent);

        assertTrue(yamlContent.contains("number: 42"));
        assertTrue(yamlContent.contains("name: Apollo"));
        assertTrue(yamlContent.contains("inner:"));
        assertTrue(yamlContent.contains("flag: false"));

        // Verify changes were saved
        TestConfig.number = 0;
        TestConfig.name = "";
        TestConfig.Inner.flag = true;
        StaticConfigLoader.saveConfig(TestConfig.class, TEMP_FILE);

        yamlContent = Files.readString(TEMP_FILE);
        System.out.println(yamlContent);
        assertTrue(yamlContent.contains("number: 0"));
        assertTrue(yamlContent.contains("name: ''"));
        assertTrue(yamlContent.contains("inner:"));
        assertTrue(yamlContent.contains("flag: true"));


        // Verify comments are present
        assertTrue(yamlContent.contains("# A number"));
        assertTrue(yamlContent.contains("# A string name"));
        assertTrue(yamlContent.contains("# An inner class"));
        assertTrue(yamlContent.contains("# An inner flag"));
    }

    @Test
    void testLoadYamlConfig() throws Exception {
        StaticConfigLoader.saveConfig(TestConfig.class, TEMP_FILE);
        TestConfig.number = 0;
        TestConfig.name = "";
        TestConfig.Inner.flag = true;
        StaticConfigLoader.loadConfig(TestConfig.class, TEMP_FILE);
        assertEquals(42, TestConfig.number);
        assertEquals("Apollo", TestConfig.name);
        assertTrue(TestConfig.Inner.flag);
    }

}
