package ecosystem.network;

import ecosystem.engine.Environment;

/**
 * Command created from network data. Its execute() runs on the engine thread
 * (it receives the Environment to modify).
 */
public interface NetworkCommand {
    /**
     * Execute this command on the given environment.
     * @return true on success.
     */
    boolean execute(Environment env);
}