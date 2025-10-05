package me.solar.apolloLibrary.menus;

import me.solar.apolloLibrary.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class MenuItem extends ItemStack{

    private MenuAction onLeftClick;
    private MenuAction onRightClick;
    private MenuAction onMiddleClick;
    private MenuAction onShiftLeftClick;
    private MenuAction onShiftRightClick;

    public MenuItem(Material material, String name, String... lore) {
        super(new ItemStack(material));
        ItemStackUtils.setDisplayName(this, name);
        ItemStackUtils.setLore(this, lore);
    }

    public MenuItem(ItemStack itemStack) {
        super(itemStack);
    }

    public void withOnClick(MenuAction onLeftClick) {
        this.onLeftClick = onLeftClick;
    }

    public void withOnRightClick(MenuAction onRightClick) {
        this.onRightClick = onRightClick;
    }

    public void withOnMiddleClick(MenuAction onMiddleClick) {
        this.onMiddleClick = onMiddleClick;
    }

    public void withOnShiftLeftClick(MenuAction onShiftLeftClick) {
        this.onShiftLeftClick = onShiftLeftClick;
    }

    public void withOnShiftRightClick(MenuAction onShiftRightClick) {
        this.onShiftRightClick = onShiftRightClick;
    }

    public void execute(MenuEvent event) {
        if (event.clickType() != null) {
            MenuAction action = this.getClickAction(event.clickType());
            if (action != null) {
                action.execute(event);
            }

        }
    }

    public MenuAction getClickAction(MenuClickType clickType) {
        MenuAction action = switch (clickType) {
            case LEFT_CLICK -> this.onLeftClick;
            case RIGHT_CLICK -> this.onRightClick;
            case MIDDLE_CLICK -> this.onMiddleClick;
            case SHIFT_LEFT_CLICK -> this.onShiftLeftClick;
            case SHIFT_RIGHT_CLICK -> this.onShiftRightClick;
            case null, default -> null;
        };

        return action;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            if (!super.equals(o)) {
                return false;
            } else {
                MenuItem menuItem = (MenuItem)o;
                return Objects.equals(this.onLeftClick, menuItem.onLeftClick) && Objects.equals(this.onRightClick, menuItem.onRightClick) && Objects.equals(this.onMiddleClick, menuItem.onMiddleClick) && Objects.equals(this.onShiftLeftClick, menuItem.onShiftLeftClick) && Objects.equals(this.onShiftRightClick, menuItem.onShiftRightClick);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), this.onLeftClick, this.onRightClick, this.onMiddleClick, this.onShiftLeftClick, this.onShiftRightClick);
    }

}
