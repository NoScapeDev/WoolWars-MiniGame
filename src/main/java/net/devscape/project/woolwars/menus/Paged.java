package net.devscape.project.woolwars.menus;

import org.bukkit.Material;

public abstract class Paged extends Menu {

    protected int page = 0;
    protected int maxItems = 35;
    protected int index = 0;

    private int currentItemsOnPage;

    public Paged(MenuUtil menuUtil) {
        super(menuUtil);
    }

    public void applyLayout() {

        //inventory.setItem(48, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), back));

        inventory.setItem(49, makeItem(Material.IRON_DOOR, "&c&lClose"));

        // inventory.setItem(50, makeItem(Material.valueOf(Objects.requireNonNull(SupremeTags.getInstance().getConfig().getString("gui.layout.back-next-material")).toUpperCase()), next));

        for (int i = 36; i <= 44; i++) {
            inventory.setItem(i, makeItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }
    }

    protected int getPage() {
        return page + 1;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public int getCurrentItemsOnPage() {
        return currentItemsOnPage;
    }
}