package me.solar.apolloLibrary.menus;

import org.bukkit.event.inventory.ClickType;

public enum MenuClickType {
    LEFT_CLICK,
    RIGHT_CLICK,
    SHIFT_LEFT_CLICK,
    SHIFT_RIGHT_CLICK,
    MIDDLE_CLICK,
    ALL;

    public static MenuClickType fromString(String string) {
        switch (string.toUpperCase()) {
            case "LEFT_CLICK" -> {
                return LEFT_CLICK;
            }
            case "RIGHT_CLICK" -> {
                return RIGHT_CLICK;
            }
            case "SHIFT_LEFT_CLICK" -> {
                return SHIFT_LEFT_CLICK;
            }
            case "SHIFT_RIGHT_CLICK" -> {
                return SHIFT_RIGHT_CLICK;
            }
            case "MIDDLE_CLICK" -> {
                return MIDDLE_CLICK;
            }
            default -> {
                return null;
            }
        }
    }

    public static MenuClickType fromClickType(ClickType clickType) {
        switch (clickType) {
            case LEFT -> {
                return LEFT_CLICK;
            }
            case RIGHT -> {
                return RIGHT_CLICK;
            }
            case SHIFT_LEFT -> {
                return SHIFT_LEFT_CLICK;
            }
            case SHIFT_RIGHT -> {
                return SHIFT_RIGHT_CLICK;
            }
            case MIDDLE -> {
                return MIDDLE_CLICK;
            }
            default -> {
                return null;
            }
        }
    }
}
