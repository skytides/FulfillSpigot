package org.bukkit.event.server;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Called when a {@link CommandSender} of any description (ie: player or
 * console) attempts to tab complete.
 */
public class TabCompleteEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    //
    private final CommandSender sender;
    private final String buffer;
    private List<String> completions;
    private final boolean isCommand;
    private final Location loc;
    private boolean cancelled;

    public TabCompleteEvent(CommandSender sender, String buffer, List<String> completions) {
        this(sender, buffer, completions, sender instanceof ConsoleCommandSender || buffer.startsWith("/"), null);
    }

    public TabCompleteEvent(CommandSender sender, String buffer, List<String> completions, boolean isCommand, Location location) {
        Validate.notNull(sender, "sender");
        Validate.notNull(buffer, "buffer");
        Validate.notNull(completions, "completions");

        this.sender = sender;
        this.buffer = buffer;
        this.completions = completions;
        this.isCommand = isCommand;
        this.loc = location;
    }

    /**
     * Get the sender completing this command.
     *
     * @return the {@link CommandSender} instance
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Return the entire buffer which formed the basis of this completion.
     *
     * @return command buffer, as entered
     */
    public String getBuffer() {
        return buffer;
    }

    /**
     * The list of completions which will be offered to the sender, in order.
     * This list is mutable and reflects what will be offered.
     *
     * @return a list of offered completions
     */
    public List<String> getCompletions() {
        return completions;
    }

    /**
     * Set the completions offered, overriding any already set.
     * <p>
     * The passed collection will be cloned to a new List. You must call {{@link #getCompletions()}} to mutate from here
     *
     * @param completions the new completions
     */
    public void setCompletions(List<String> completions) {
        Validate.notNull(completions);
        this.completions = new ArrayList<>(completions);
    }

    /**
     * @return True if it is a command being tab completed, false if it is a chat message.
     */
    public boolean isCommand() {
        return isCommand;
    }

    /**
     * @return The position looked at by the sender, or null if none
     */
    public org.bukkit.Location getLocation() {
        return loc;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
