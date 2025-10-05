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
        if (event.getClickType() != null) {
            MenuAction action = this.getClickAction(event.getClickType());
            if (action != null) {
                action.execute(event);
            }

        }
    }

    public MenuAction getClickAction(MenuClickType clickType) {
        MenuAction var10000;
        switch (clickType) {
            case LEFT_CLICK:
                var10000 = this.onLeftClick;
                break;
            case RIGHT_CLICK:
                var10000 = this.onRightClick;
                break;
            case MIDDLE_CLICK:
                var10000 = this.onMiddleClick;
                break;
            case SHIFT_LEFT_CLICK:
                var10000 = this.onShiftLeftClick;
                break;
            case SHIFT_RIGHT_CLICK:
                var10000 = this.onShiftRightClick;
                break;
            case null:
            default:
                var10000 = null;
        }

        return var10000;
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
        return Objects.hash(new Object[]{super.hashCode(), this.onLeftClick, this.onRightClick, this.onMiddleClick, this.onShiftLeftClick, this.onShiftRightClick});
    }

}
