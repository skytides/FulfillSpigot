package xyz.swift.spigot.event.sound;

import org.bukkit.entity.Player;

/**
 * Called when a sound is about to be played.
 */
public class PlayerSoundEvent extends EntitySoundEvent {
    public PlayerSoundEvent(Player player, String sound, float volume, float pitch) {
        super(player, sound, volume, pitch);
    }
    
    /**
     * Get the player playing the sound.
     *
     * @return The player.
     */
    public Player getPlayer() {
        return (Player) super.getEntity();
    }
}
