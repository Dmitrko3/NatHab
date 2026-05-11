package ecosystem.interfaces;

import ecosystem.core.Environment;

/**
 * Contract for entities that can create offspring in the environment.
 */
public interface Reproducible {
    /**
     * Attempt to spawn offspring into the environment.
     *
     * @param environment the world context
     * @return {@code true} if at least one offspring was successfully placed
     */
    boolean reproduce(Environment environment);
}
