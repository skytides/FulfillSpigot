package io.papermc.paper.event.packet;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.ChunkEvent;

/**
 * Is called when a {@link Player} receives a chunk unload packet.
 *
 * Should only be used for packet/clientside related stuff.
 * Not intended for modifying server side.
 */
public class PlayerChunkUnloadEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public PlayerChunkUnloadEvent(Chunk chunk, Player player) {
        super(chunk);
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
