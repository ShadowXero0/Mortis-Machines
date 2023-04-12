package me.none030.mortismachines.machines.autocrafter.menus;

import me.none030.mortismachines.MortisMachines;
import me.none030.mortismachines.machines.autocrafter.AutoCrafterData;
import me.none030.mortismachines.machines.autocrafter.AutoCrafterManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoCrafterProgressMenu implements InventoryHolder {

    private final MortisMachines plugin = MortisMachines.getInstance();
    private final AutoCrafterManager autoCrafterManager;
    private AutoCrafterData data;
    private Inventory menu;
    private final int resultSlot = 49;
    private final List<Integer> defaultGridSlots = List.of(12,13,14,21,22,23,30,31,32);
    private final List<Integer> maxGridSlots = List.of(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43);
    private final List<Integer> animationSlots = List.of(0,8,45,53);
    private long animation = 0;

    public AutoCrafterProgressMenu(AutoCrafterManager autoCrafterManager, AutoCrafterData data) {
        this.autoCrafterManager = autoCrafterManager;
        this.data = data;
        create();
    }

    private void create() {
        menu = Bukkit.createInventory(this, 54, Component.text(autoCrafterManager.getMenuItems().getTitle()));
        update(data);
    }

    public void update(AutoCrafterData data) {
        setData(data);
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, autoCrafterManager.getRecipeMenuItems().getItem("FILTER"));
        }
        for (int slot : maxGridSlots) {
            menu.setItem(slot, new ItemStack(Material.AIR));
        }
        int size;
        if (data.isMax()) {
            size = maxGridSlots.size();
        }else {
            size = defaultGridSlots.size();
        }
        ItemStack[] grid = data.getGrid();
        if (grid != null) {
            for (int i = 0; i < size; i++) {
                if (data.isMax()) {
                    menu.setItem(maxGridSlots.get(i), grid[i]);
                } else {
                    menu.setItem(defaultGridSlots.get(i), grid[i]);
                }
            }
        }else {
            for (int i = 0; i < size; i++) {
                if (data.isMax()) {
                    menu.setItem(maxGridSlots.get(i), new ItemStack(Material.AIR));
                } else {
                    menu.setItem(defaultGridSlots.get(i), new ItemStack(Material.AIR));
                }
            }
        }
        if (data.getResult() != null) {
            menu.setItem(resultSlot, data.getResult());
        }else {
            menu.setItem(resultSlot, new ItemStack(Material.AIR));
        }
        if (animation == 0) {
            for (int slot : animationSlots) {
                menu.setItem(slot, autoCrafterManager.getProgressMenuItems().getItem("ANIMATION"));
            }
        }
        if (animation == 1) {
            for (int slot : animationSlots) {
                menu.setItem(slot, autoCrafterManager.getProgressMenuItems().getItem("ANIMATION_2"));
            }
        }
        if (animation == 2) {
            for (int slot : animationSlots) {
                menu.setItem(slot, autoCrafterManager.getProgressMenuItems().getItem("ANIMATION_3"));
            }
        }
    }

    public ItemStack click(int slot, ItemStack cursor) {
        if (cursor != null && !cursor.getType().equals(Material.AIR)) {
            return cursor;
        }
        if (slot == resultSlot) {
            if (data.getResult() != null) {
                ItemStack result = data.getResult();
                data.setResult(null);
                update(data);
                return result;
            }
        }
        if (cursor != null && !cursor.getType().equals(Material.AIR)) {
            return cursor;
        }
        if (data.isMax()) {
            if (maxGridSlots.contains(slot)) {
                int index = maxGridSlots.indexOf(slot);
                ItemStack[] grid = data.getGrid();
                ItemStack item = grid[index];
                if (item != null && !item.getType().equals(Material.AIR)) {
                    grid[index] = new ItemStack(Material.AIR);
                    data.setGrid(grid);
                    update(data);
                    return item;
                }
            }
        }else {
            if (defaultGridSlots.contains(slot)) {
                int index = defaultGridSlots.indexOf(slot);
                ItemStack[] grid = data.getGrid();
                ItemStack item = grid[index];
                if (item != null && !item.getType().equals(Material.AIR)) {
                    grid[index] = new ItemStack(Material.AIR);
                    data.setGrid(grid);
                    update(data);
                    return item;
                }
            }
        }
        return cursor;
    }

    public void animate(long time) {
        new BukkitRunnable() {
            long seconds;
            final long runTime = time / 3;
            final long runTime2 = time / 2;
            @Override
            public void run() {
                seconds ++;
                if (seconds > runTime) {
                    if (seconds > runTime2) {
                        setAnimation(1);
                        update(data);
                    }else {
                        setAnimation(0);
                        update(data);
                    }
                }
                if (seconds > time) {
                    setAnimation(2);
                    update(data);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void open(Player player) {
        player.openInventory(menu);
    }

    public void close(Player player) {
        player.closeInventory();
    }

    public AutoCrafterManager getAutoCrafterManager() {
        return autoCrafterManager;
    }

    public AutoCrafterData getData() {
        return data;
    }

    public void setData(AutoCrafterData data) {
        this.data = data;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }

    public int getResultSlot() {
        return resultSlot;
    }

    public List<Integer> getDefaultGridSlots() {
        return defaultGridSlots;
    }

    public List<Integer> getMaxGridSlots() {
        return maxGridSlots;
    }

    public List<Integer> getAnimationSlots() {
        return animationSlots;
    }

    public long getAnimation() {
        return animation;
    }

    public void setAnimation(long animation) {
        this.animation = animation;
    }
}