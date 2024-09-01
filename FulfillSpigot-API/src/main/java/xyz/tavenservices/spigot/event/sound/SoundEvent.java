package xyz.tavenservices.spigot.event.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a sound is about to be played.
 */
public class SoundEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Location location;
    private String sound;
    private float volume;
    private float pitch;
    private boolean cancelled;
    
    public SoundEvent(Location location, String sound, float volume, float pitch) {
        this.location = location;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }
    
    /**
     * @return The location the sound will be played at.
     */
    public Location getLocation() {
        return this.location;
    }
    
    /**
     * Set the location the sound will be played at.
     *
     * @param location The sound's new location.
     */
    public void setLocation(Location location) {
        this.location = location;
    }
    
    /**
     * @return The sound that will be played.
     * @see org.bukkit.entity.Player#playSound(Location, Sound, float, float)
     */
    public String getSound() {
        return this.sound;
    }
    
    /**
     * Set the sound that will be played.
     *
     * @param sound The new sound.
     */
    public void setSound(String sound) {
        this.sound = sound;
    }
    
    /**
     * @return The sound's volume.
     */
    public float getVolume() {
        return this.volume;
    }
    
    /**
     * Set the sound's volume.
     *
     * @param volume The sound's new volume.
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }
    
    /**
     * @return The sound's pitch.
     */
    public float getPitch() {
        return this.pitch;
    }
    
    /**
     * Set the sound's pitch.
     *
     * @param pitch The sound's new pitch.
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
