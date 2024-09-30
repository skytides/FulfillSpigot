package xyz.tavenservices.spigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadedChunksCommand extends Command {

    private static final int CHUNKS_PER_PAGE = 10;

    public LoadedChunksCommand(String name) {
        super(name);
        this.description = "Shows the list of all the loaded chunks in the server or unloads a specific chunk";
        this.usageMessage = "/loadedchunks [unload <world> <x> <z>]";
        this.setPermission("fulfillspigot.command.loadedchunks");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        Player player = (Player) sender;

        if (!player.hasPermission("fulfillspigot.command.loadedchunks")) {
            player.sendMessage(ChatColor.WHITE + "No permission :(");
            return true;
        }

        if (args.length == 0) {
            listLoadedChunks(player, 1);
        } else if (args.length >= 4 && args[0].equalsIgnoreCase("unload")) {
            String worldName = args[1];
            int chunkX, chunkZ;
            try {
                chunkX = Integer.parseInt(args[2]);
                chunkZ = Integer.parseInt(args[3]);
                unloadChunk(player, worldName, chunkX, chunkZ);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.WHITE + "Invalid chunk coordinates.");
            }
        } else {
            player.sendMessage(ChatColor.WHITE + "Usage: /loadedchunks or /loadedchunks unload <world> <x> <z>");
        }

        return true;
    }

    private void listLoadedChunks(Player player, int page) {
        List<Chunk> allChunks = getAllLoadedChunks();
        int totalPages = (int) Math.ceil((double) allChunks.size() / CHUNKS_PER_PAGE);

        if (page < 1 || page > totalPages) {
            player.sendMessage(ChatColor.WHITE + "Invalid page number.");
            return;
        }

        player.sendMessage(ChatColor.AQUA + "=== Loaded Chunks - Page " + page + "/" + totalPages + " ===");
        player.sendMessage(ChatColor.WHITE + "Total Loaded Chunks: " + allChunks.size() + "\n");

        int startIndex = (page - 1) * CHUNKS_PER_PAGE;
        int endIndex = Math.min(startIndex + CHUNKS_PER_PAGE, allChunks.size());

        for (int i = startIndex; i < endIndex; i++) {
            Chunk chunk = allChunks.get(i);
            player.sendMessage(ChatColor.AQUA + "- World: " + chunk.getWorld().getName() + " X: " + chunk.getX() + " Z: " + chunk.getZ());
        }
    }

    private List<Chunk> getAllLoadedChunks() {
        List<Chunk> allChunks = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            allChunks.addAll(Arrays.asList(world.getLoadedChunks()));
        }
        return allChunks;
    }

    private void unloadChunk(Player player, String worldName, int chunkX, int chunkZ) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Chunk chunk = world.getChunkAt(chunkX, chunkZ);
            if (world.isChunkLoaded(chunkX, chunkZ)) {
                world.unloadChunk(chunkX, chunkZ);
                player.sendMessage(ChatColor.AQUA + "Unloaded chunk (" + chunkX + ", " + chunkZ + ") in world " + ChatColor.WHITE + worldName + ".");
            } else {
                player.sendMessage(ChatColor.AQUA + "Chunk (" + chunkX + ", " + chunkZ + ") is not loaded in world " + ChatColor.WHITE + worldName + ".");
            }
        } else {
            player.sendMessage(ChatColor.AQUA + "World " + ChatColor.WHITE + worldName + " does not exist.");
        }
    }
}
