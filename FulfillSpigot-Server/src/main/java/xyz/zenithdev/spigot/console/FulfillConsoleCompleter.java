package xyz.zenithdev.spigot.console;

import net.minecraft.server.DedicatedServer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.server.TabCompleteEvent;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class FulfillConsoleCompleter implements Completer {
    private final DedicatedServer server;

    public FulfillConsoleCompleter(DedicatedServer server) {
        this.server = server;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        // Async Tab Completion
        com.destroystokyo.paper.event.server.AsyncTabCompleteEvent event;
        List<String> completions = new java.util.ArrayList<>();
        event = new com.destroystokyo.paper.event.server.AsyncTabCompleteEvent(server.server.getConsoleSender(), completions, line.line(), true, null);
        event.callEvent();
        completions = event.isCancelled() ? com.google.common.collect.ImmutableList.of() : event.getCompletions();

        if (event.isCancelled() || event.isHandled()) {
            // Still fire sync event with the provided completions, if someone is listening
            if (!event.isCancelled() && TabCompleteEvent.getHandlerList().getRegisteredListeners().length > 0) {
                List<String> finalCompletions = completions;
                Waitable<List<String>> syncCompletions = new Waitable<List<String>>() {
                    @Override
                    protected List<String> evaluate() {
                        TabCompleteEvent syncEvent = new TabCompleteEvent(server.server.getConsoleSender(), line.line(), finalCompletions);
                        return syncEvent.callEvent() ? syncEvent.getCompletions() : com.google.common.collect.ImmutableList.of();
                    }
                };
                server.processQueue.add(syncCompletions);
                try {
                    completions = syncCompletions.get();
                } catch (InterruptedException | ExecutionException e1) {
                    e1.printStackTrace();
                }
            }

            if (!completions.isEmpty()) {
                candidates.addAll(completions.stream().map(Candidate::new).collect(java.util.stream.Collectors.toList()));
            }
            return;
        }

        CompletionWaiter waiter = new CompletionWaiter(line.line());
        this.server.processQueue.add(waiter);

        try {
            List<String> offers = waiter.get(); // wait until completions get processed on main thread

            for (String offer : offers) {
                if (offer.isEmpty()) continue;

                candidates.add(new Candidate(offer));
            }
        } catch (ExecutionException e) {
            this.server.server.getLogger().log(Level.WARNING, "Unhandled exception when tab completing", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private class CompletionWaiter extends Waitable<List<String>> {
        private final String buffer;

        private CompletionWaiter(String buffer) {
            this.buffer = buffer;
        }

        @Override
        protected List<String> evaluate() {
            final CraftServer server = FulfillConsoleCompleter.this.server.server;
            final List<String> offers = server.getCommandMap().tabComplete(server.getConsoleSender(), buffer);

            TabCompleteEvent tabEvent = new TabCompleteEvent(server.getConsoleSender(), buffer, offers == null ? Collections.emptyList() : offers);
            server.getPluginManager().callEvent(tabEvent);

            return tabEvent.isCancelled() ? Collections.emptyList() : tabEvent.getCompletions();
        }
    }
}
