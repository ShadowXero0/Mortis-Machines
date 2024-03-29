package com.mortisdevelopment.mortismachines.machines.remotecontrol.remotes;

import com.mortisdevelopment.mortismachines.MortisMachines;
import com.mortisdevelopment.mortismachines.machines.remotecontrol.RemoteControlData;
import com.mortisdevelopment.mortismachines.machines.remotecontrol.RemoteControlMachine;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DeadmanRemote extends Remote {

    private final MortisMachines plugin = MortisMachines.getInstance();
    private final long time;

    public DeadmanRemote(String id, ItemStack remote, int range, long time) {
        super(id, remote, range);
        this.time = time;
    }

    public void activate(RemoteControlMachine machine, RemoteControlData data) {
        data.cancelTask();
        machine.stopRedstoneSignal(data);
        if (data.isPulse()) {
            new BukkitRunnable() {
                long ticks;
                boolean done;
                @Override
                public void run() {
                    if (!done) {
                        data.setTask(this.getTaskId());
                        done = true;
                    }
                    ticks += machine.getPulse();
                    if (data.isActivated()) {
                        machine.stopRedstoneSignal(data);
                    }else {
                        machine.sendRedstoneSignal(data);
                    }
                    if (ticks > (time * 20)) {
                        machine.stopRedstoneSignal(data);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, machine.getPulse());
        }else {
            machine.sendRedstoneSignal(data);
            new BukkitRunnable() {
                long ticks;
                boolean done;

                @Override
                public void run() {
                    if (!done) {
                        data.setTask(this.getTaskId());
                        done = true;
                    }
                    ticks += 20;
                    if (ticks > (time * 20)) {
                        machine.stopRedstoneSignal(data);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    public long getTime() {
        return time;
    }
}
