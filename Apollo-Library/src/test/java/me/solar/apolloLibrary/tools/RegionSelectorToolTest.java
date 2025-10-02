package me.solar.apolloLibrary.tools;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import me.solar.apolloLibrary.tools.RegionSelectorTool;
import me.solar.apolloLibrary.world.CuboidRegion;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

class RegionSelectorToolTest {

    private JavaPlugin pluginMock;
    private RegionSelectorTool tool;

    @BeforeAll
    static void initServer() {
        MockBukkit.mock();
    }

    @AfterAll
    static void tearDownServer() {
        MockBukkit.unmock();
    }

    @BeforeEach
    void setup() {
        pluginMock = mock(JavaPlugin.class);  // Use Mockito for external dependencies
        tool = new RegionSelectorTool(pluginMock);
    }

    @Test
    void testConstructorInitializesFields() {
        assertNotNull(tool);
        assertEquals("Region Selector", tool.toolName);
        assertNotNull(tool.toolLore);
    }

    @Test
    void testGetCuboidRegionReturnsNullForMissingUUID() {
        assertNull(tool.getCuboidRegion(UUID.randomUUID()));
    }

    @Test
    void testGetCuboidRegionReturnsCorrectRegion() {
        UUID uuid = UUID.randomUUID();
        CuboidRegion regionMock = mock(CuboidRegion.class);
        tool.regions.put(uuid, regionMock);
        assertEquals(regionMock, tool.getCuboidRegion(uuid));
    }

    @Test
    void testGetRegionStaticDelegatesCorrectly() {
        // Static calls may require dependency injection or other arrangements for full isolation.
        // Here, just verify no unexpected exceptions for demonstration:
        try {
            RegionSelectorTool.getRegion(UUID.randomUUID());
        } catch (Exception ex) {
            fail("getRegion() should not throw an exception: " + ex.getMessage());
        }
    }

    @Test
    void testGetItemProperties() {
        ItemStack item = tool.getItem();
        assertNotNull(item, "getItem() should not return null");
        // Further assertions on item meta, display name, lore, material, etc. as needed
    }

    @Test
    void testToolMaterialAndNameConsistency() {
        assertNotNull(tool.toolMaterial, "Tool material should not be null");
        assertEquals("Region Selector", tool.toolName);
    }
}