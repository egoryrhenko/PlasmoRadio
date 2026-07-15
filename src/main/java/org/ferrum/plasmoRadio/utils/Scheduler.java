package org.ferrum.plasmoRadio.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.ferrum.plasmoRadio.PlasmoRadio;

public final class Scheduler {

    private static final boolean IS_FOLIA;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        IS_FOLIA = folia;
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    // -------------------
    // SYNC RUN
    // -------------------

    public static void runAtChunk(Location location, Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().execute( PlasmoRadio.plugin, location, runnable);
        } else {
            run(runnable);  // sync
        }
    }


    public static void run(Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().execute(PlasmoRadio.plugin, runnable);
        } else {
            Bukkit.getScheduler().runTask(PlasmoRadio.plugin, runnable);
        }
    }

    // -------------------
    // ASYNC RUN
    // -------------------

    public static void runAsync(Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runNow(PlasmoRadio.plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(PlasmoRadio.plugin, runnable);
        }
    }

    // -------------------
    // DELAYED TASK — Runnable
    // -------------------

    public static Task runLater(Runnable runnable, long delay) {
        if (IS_FOLIA) {
            return new Task(Bukkit.getGlobalRegionScheduler()
                    .runDelayed(PlasmoRadio.plugin, t -> runnable.run(), delay));
        } else {
            return new Task(Bukkit.getScheduler()
                    .runTaskLater(PlasmoRadio.plugin, runnable, delay));
        }
    }

    // -------------------
    // DELAYED TASK — BukkitRunnable
    // -------------------

    public static Task runLater(BukkitRunnable runnable, long delay) {
        if (!IS_FOLIA) {
            return new Task(runnable.runTaskLater(PlasmoRadio.plugin, delay));
        }

        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runDelayed(
                PlasmoRadio.plugin,
                t -> runnable.run(),
                delay
        );

        return new Task(task);
    }

    // -------------------
    // TIMER TASK — Runnable
    // -------------------

    public static Task runTimer(Runnable runnable, long delay, long period) {
        if (IS_FOLIA) {
            return new Task(Bukkit.getGlobalRegionScheduler()
                    .runAtFixedRate(PlasmoRadio.plugin, t -> runnable.run(),
                            Math.max(1, delay), period));
        } else {
            return new Task(Bukkit.getScheduler()
                    .runTaskTimer(PlasmoRadio.plugin, runnable, delay, period));
        }
    }

    // -------------------
    // TIMER TASK — BukkitRunnable
    // -------------------

    public static Task runTimer(BukkitRunnable runnable, long delay, long period) {
        if (!IS_FOLIA) {
            return new Task(runnable.runTaskTimer(PlasmoRadio.plugin, delay, period));
        }

        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                PlasmoRadio.plugin,
                t -> runnable.run(),
                Math.max(1, delay),
                period
        );

        return new Task(task);
    }

    // -------------------
    // WRAPPER
    // -------------------

    public static class Task {
        private final ScheduledTask foliaTask;
        private final BukkitTask bukkitTask;

        Task(ScheduledTask foliaTask) {
            this.foliaTask = foliaTask;
            this.bukkitTask = null;
        }

        Task(BukkitTask bukkitTask) {
            this.foliaTask = null;
            this.bukkitTask = bukkitTask;
        }

        public void cancel() {
            if (foliaTask != null) {
                foliaTask.cancel();
            } else if (bukkitTask != null) {
                bukkitTask.cancel();
            }
        }

        public boolean isCancelled() {
            if (foliaTask != null) return foliaTask.isCancelled();
            if (bukkitTask != null) return bukkitTask.isCancelled();
            return true;
        }
    }
}
