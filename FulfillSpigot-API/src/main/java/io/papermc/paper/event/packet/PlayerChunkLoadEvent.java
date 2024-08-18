package io.papermc.paper.event.packet;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.ChunkEvent;

/**
 * Is called when a {@link org.bukkit.entity.Player} receives a {@link org.bukkit.Chunk}
 * <p>
 * Can for example be used for spawning a fake entity when the player receives a chunk.
 *
 * Should only be used for packet/clientside related stuff.
 * Not intended for modifying server side state.
 */
public class PlayerChunkLoadEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public PlayerChunkLoadEvent(Chunk chunk, Player player) {
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
