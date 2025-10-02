package me.solar.apolloLibrary.menus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

class MenuTest {
    // Provide a concrete subclass for testing
    static class TestMenu extends me.solar.apolloLibrary.menus.Menu {
        TestMenu(String name, int size) {
            super(name, size);
        }
        @Override
        protected void initialize(Player player) {
            // Example init logic: can be left empty for now
        }
    }

    private Player player;
    private Inventory inventory;
    private static @NotNull ServerMock server;

    @BeforeAll
    static void startMockServer() {
        server = MockBukkit.mock();
    }
    @AfterAll
    static void stopMockServer() {
        MockBukkit.unmock();
    }


    @BeforeEach
    void setup() {
        // Mock Bukkit API objects
        player = mock(Player.class);
        inventory = mock(Inventory.class);
    }

    @Test
    void testMenuRegistry_AddRemove_HasMenu() {
        Menu menu = mock(TestMenu.class);
        me.solar.apolloLibrary.menus.Menu.addMenu(menu, player);
        assertTrue(me.solar.apolloLibrary.menus.Menu.hasMenu(player));
        assertEquals(menu, me.solar.apolloLibrary.menus.Menu.getMenu(player));
        me.solar.apolloLibrary.menus.Menu.removeMenu(player);
        assertFalse(me.solar.apolloLibrary.menus.Menu.hasMenu(player));
    }

    @Test
    void testMenuItemLifecycle() {
        TestMenu menu = new TestMenu("Test", 9);
        MenuItem menuItem = mock(MenuItem.class);

        assertNull(menu.getMenuItem(2));

        menu.setItem(2, menuItem);
        assertEquals(menuItem, menu.getMenuItem(2));
    }

    @Test
    void testOpenAndCloseCallsExpectedMethods() {
        TestMenu menu = spy(new TestMenu("Test", 9));

        menu.open(player);
        assertTrue(me.solar.apolloLibrary.menus.Menu.hasMenu(player));

        menu.close(player);
        assertFalse(me.solar.apolloLibrary.menus.Menu.hasMenu(player)); // Should deregister
    }

    @Test
    void testUpdateCallsInitializeAndPlayerUpdateInventory() {
        TestMenu menu = spy(new TestMenu("Test", 18));
        menu.update(player);
        verify(menu).initialize(player);
        verify(player).updateInventory();
    }

    @AfterEach
    void cleanup() {
        // Clear static registry state
        Menu.getMenus().clear();
    }
}