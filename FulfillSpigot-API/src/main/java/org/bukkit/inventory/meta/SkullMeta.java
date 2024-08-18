package org.bukkit.inventory.meta;

import org.bukkit.Material;

/**
 * Represents a skull ({@link Material#SKULL_ITEM}) that can have an owner.
 */
public interface SkullMeta extends ItemMeta {

    /**
     * Gets the owner of the skull.
     *
     * @return the owner if the skull
     */
    String getOwner();

    /**
     * Checks to see if the skull has an owner.
     *
     * @return true if the skull has an owner
     */
    boolean hasOwner();

    /**
     * Sets the owner of the skull.
     * <p>
     * Plugins should check that hasOwner() returns true before calling this
     * plugin.
     *
     * @param owner the new owner of the skull
     * @return true if the owner was successfully set
     */
    boolean setOwner(String owner);

    // PandaSpigot start - PlayerProfile API
    /**
     * Sets this skull to use the supplied Player Profile, which can include textures already prefilled.
     * @param profile The profile to set this Skull to use, or null to clear owner
     */
    void setPlayerProfile(com.destroystokyo.paper.profile.PlayerProfile profile);
    
    /**
     * If the skull has an owner, per {@link #hasOwner()}, return the owners {@link com.destroystokyo.paper.profile.PlayerProfile}
     * @return The profile of the owner, if set
     */
    com.destroystokyo.paper.profile.PlayerProfile getPlayerProfile();
    // PandaSpigot end

    SkullMeta clone();
}
