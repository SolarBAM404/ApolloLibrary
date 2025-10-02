package me.solar.apolloLibrary.menus;

import lombok.Generated;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class PaginatedMenu extends Menu {
    private int currentPage;
    private final int maxItemsPerPage;

    protected PaginatedMenu(String name, int size) {
        this((String)name, size, 5);
    }

    protected PaginatedMenu(Component name, int size) {
        this((Component)name, size, 5);
    }

    protected PaginatedMenu(String name, int size, int maxItemsPerPage) {
        super(name, size);
        this.currentPage = 0;
        this.maxItemsPerPage = maxItemsPerPage;
    }

    protected PaginatedMenu(Component name, int size, int maxItemsPerPage) {
        super(name, size);
        this.currentPage = 0;
        this.maxItemsPerPage = maxItemsPerPage;
    }

    protected abstract int getTotalItems();

    public abstract void update(Player var1);

    public void clearPage() {
        this.inventory.clear();
        this.setNextPageItem();
        this.setPreviousPageItem();
    }

    public void nextPage(Player player) {
        if ((this.currentPage + 1) * this.maxItemsPerPage < this.getTotalItems()) {
            ++this.currentPage;
            this.clearPage();
            this.update(player);
        }
    }

    public void previousPage(Player player) {
        if (this.currentPage > 0) {
            --this.currentPage;
            this.clearPage();
            this.update(player);
        }
    }

    public void open(Player player) {
        this.currentPage = 0;
        this.setNextPageItem();
        this.setPreviousPageItem();
        super.open(player);
    }

    protected MenuItem nextPageItem() {
        MenuItem nextPageItem = new MenuItem(Material.ARROW, "Next Page", new String[0]);
        nextPageItem.withOnClick((event) -> this.nextPage(event.getPlayer()));
        return nextPageItem;
    }

    protected MenuItem previousPageItem() {
        MenuItem previousPageItem = new MenuItem(Material.ARROW, "Previous Page", new String[0]);
        previousPageItem.withOnClick((event) -> this.previousPage(event.getPlayer()));
        return previousPageItem;
    }

    protected void setNextPageItem() {
        int slot = this.inventory.getSize() - 1;
        this.setItem(slot, this.nextPageItem());
    }

    protected void setPreviousPageItem() {
        int slot = this.inventory.getSize() - 9;
        this.setItem(slot, this.previousPageItem());
    }

    @Generated
    public int getCurrentPage() {
        return this.currentPage;
    }

    @Generated
    public int getMaxItemsPerPage() {
        return this.maxItemsPerPage;
    }
}

