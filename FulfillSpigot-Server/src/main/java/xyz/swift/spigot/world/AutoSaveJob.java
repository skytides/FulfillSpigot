package xyz.swift.spigot.world;

import co.aikar.timings.SpigotTimings;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.event.world.WorldSaveEvent;

public class AutoSaveJob {

    public enum JobDetail {
        WORLD_SAVE,
        WORLD_SAVEEVENT,
    }

    private WorldServer worldserver;
    private JobDetail jobDetail;

    public AutoSaveJob(JobDetail detail, WorldServer worldserver) {
        this.jobDetail = detail;
        this.worldserver = worldserver;
    }

    /**
     * @return true if the job shall be removed from the autosave queue
     * @throws ExceptionWorldConflict
     */

    public boolean process() throws ExceptionWorldConflict {
        if (this.isJob(JobDetail.WORLD_SAVE) && this.worldserver != null) {
            SpigotTimings.worldSaveTimer.startTiming();
            MinecraftServer.getServer().info("[AutoSave] Saving world " + this.worldserver.getWorld().getName());
            this.worldserver.save(true, null);
            FileIOThread.a().setNoDelay(true);
            SpigotTimings.worldSaveTimer.stopTiming();

        } else if (this.isJob(JobDetail.WORLD_SAVEEVENT) && this.worldserver != null) {
            if (FileIOThread.a().isDone()) {
                SpigotTimings.worldSaveTimer.startTiming();
                FileIOThread.a().setNoDelay(false);
                RegionFileCache.a();
                Bukkit.getPluginManager().callEvent(new WorldSaveEvent(this.worldserver.getWorld()));
                SpigotTimings.worldSaveTimer.stopTiming();
            } else {
                return false;
            }
        }
        this.worldserver = null;
        return true;
    }

    private boolean isJob(JobDetail detail) {
        if (this.jobDetail != null) {
            return this.jobDetail.equals(detail);
        }
        return false;
    }
}
