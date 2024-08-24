package xyz.zenithdev.spigot.console;

import net.minecraft.server.DedicatedServer;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.nio.file.Paths;

public class FulfillConsole extends SimpleTerminalConsole {
    private final DedicatedServer server;

    public FulfillConsole(DedicatedServer server) {
        this.server = server;
    }

    @Override
    protected boolean isRunning() {
        return !this.server.isStopped() && this.server.isRunning();
    }

    @Override
    protected void runCommand(String command) {
        this.server.issueCommand(command, this.server);
    }

    @Override
    protected void shutdown() {
        this.server.safeShutdown();
    }

    @Override
    protected LineReader buildReader(@NotNull LineReaderBuilder builder) {
        return super.buildReader(builder
            .appName("PandaSpigot")
            .variable(LineReader.HISTORY_FILE, Paths.get(".console_history"))
            .completer(new FulfillConsoleCompleter(this.server)));
    }
}
