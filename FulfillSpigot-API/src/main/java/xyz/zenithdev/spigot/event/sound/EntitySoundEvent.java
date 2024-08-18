package xyz.zenithdev.spigot.event.sound;

import org.bukkit.entity.Entity;

/**
 * Called when a sound is about to be played.
 */
public class EntitySoundEvent extends SoundEvent {
    private final Entity entity;
    
    public EntitySoundEvent(Entity entity, String sound, float volume, float pitch) {
        super(entity.getLocation(), sound, volume, pitch);
        this.entity = entity;
    }
    
    /**
     * Get the entity playing the sound.
     *
     * @return The entity.
     */
    public Entity getEntity() {
        return this.entity;
    }
}
