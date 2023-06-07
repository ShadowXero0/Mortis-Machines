package me.none030.mortismachines.machines.idcard;

import me.none030.mortismachines.MortisMachines;
import me.none030.mortismachines.data.MachineData;
import me.none030.mortismachines.machines.Machine;
import me.none030.mortismachines.machines.Manager;
import me.none030.mortismachines.menu.MenuItems;
import me.none030.mortismachines.structures.Structure;
import me.none030.mortismachines.utils.MachineType;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class IdCardManager extends Manager {

    private final MortisMachines plugin = MortisMachines.getInstance();
    private final MenuItems menuItems;
    private final HashMap<UUID, IdCardData> addEditorByPlayer;
    private final HashMap<UUID, IdCardData> removeEditorByPlayer;

    public IdCardManager(MenuItems menuItems) {
        super(MachineType.ID_CARD, true, false);
        this.menuItems = menuItems;
        addEditorByPlayer = new HashMap<>();
        removeEditorByPlayer = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(new IdCardListener(this), plugin);
        check();
    }

    private void check() {
        IdCardManager idCardManager = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                getPlayersInMenuCoolDown().clear();
                for (int i = 0; i < getCores().size(); i++) {
                    Location core = getCores().get(i);
                    if (core == null) {
                        continue;
                    }
                    IdCardData data = new IdCardData(core);
                    if (!data.isMachine()) {
                        delete(core);
                        continue;
                    }
                    Machine machine = getMachineById().get(data.getId());
                    if (!(machine instanceof IdCardMachine)) {
                        delete(data);
                        continue;
                    }
                    Structure structure = machine.getStructure(data.getStructureId());
                    if (structure == null) {
                        delete(data);
                        continue;
                    }
                    if (!structure.isStructure(core, false)) {
                        delete(data, structure.getCore().getData());
                        continue;
                    }
                    for (Player player : core.getNearbyPlayers(128)) {
                        player.sendBlockChange(core, structure.getCore().getData());
                    }
                    getDataByCore().put(core, structure.getCore().getData());
                    if (getActivatedMachines().contains(core)) {
                        continue;
                    }
                    if (data.getInsertKeyCard() != null) {
                        ((IdCardMachine) machine).activate(idCardManager, data);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void delete(IdCardData data) {
        data.empty(data.getCore());
        delete(data.getCore());
        data.delete();
    }

    public void delete(IdCardData data, BlockData blockData) {
        data.empty(data.getCore());
        delete(data.getCore(), blockData);
        data.delete();
    }

    public MenuItems getMenuItems() {
        return menuItems;
    }

    public HashMap<UUID, IdCardData> getAddEditorByPlayer() {
        return addEditorByPlayer;
    }

    public HashMap<UUID, IdCardData> getRemoveEditorByPlayer() {
        return removeEditorByPlayer;
    }
}
