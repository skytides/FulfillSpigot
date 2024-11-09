package org.spigotmc;

import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class TicksPerSecondCommand extends Command {

    public TicksPerSecondCommand(String name) {
        super(name);
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission("bukkit.command.tps");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        final double[] tps = Bukkit.spigot().getTPS();
        final double tpsNow = Bukkit.spigot().getTPS()[0];
        final double roundTPS = Math.round(tpsNow * 100.0) / 100.0;
        final String[] tpsAvg = new String[tps.length];
        final String[] tps2 = new String[3];

        for (int i = 0; i < tps2.length; ++i) {
            tps2[i] = format(tps[i]);
        }

        for (int i = 0; i < tps.length; ++i) {
            tpsAvg[i] = format(tps[i]);
        }

        int totalEntities = 0;
        for (final World world : Bukkit.getServer().getWorlds()) {
            totalEntities += world.getEntities().size();
        }

        final double lag = (double)Math.round((1.0 - tpsNow / 20.0) * 100.0);
        final long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 1048576L;
        final long allocatedMemory = Runtime.getRuntime().totalMemory() / 1048576L;
        final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        final World world2 = (sender instanceof Player) ? ((Player)sender).getWorld() : Bukkit.getWorlds().get(0);
        final Chunk[] loadedChunks = world2.getLoadedChunks();
        int coreCount = Runtime.getRuntime().availableProcessors();
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeHours = (uptimeMillis / (1000 * 60 * 60)) % 24;
        long uptimeMinutes = (uptimeMillis / (1000 * 60)) % 60;
        long uptimeSeconds = (uptimeMillis / 1000) % 60;

        sender.sendMessage("");
        sender.sendMessage(ChatColor.BOLD + "§bPerformance §f:");
        sender.sendMessage("");
        sender.sendMessage("§b§lUptime: §f" + uptimeHours + "h " + uptimeMinutes + "m " + uptimeSeconds + "s");
        sender.sendMessage("§b§lCores: §f" + coreCount);
        sender.sendMessage("§b§lTPS: §f" + format(tpsNow));
        sender.sendMessage("§b§lEntities: §f" + totalEntities);
        sender.sendMessage("§b§lChunks: §f" + loadedChunks.length);
        sender.sendMessage("§b§lMemory: §f" + usedMemory + "/" + allocatedMemory + " MB");
        sender.sendMessage("");

        return true;
    }

    private static String format(double tps) {
        return (((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED))
            + (((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0));
    }
}
