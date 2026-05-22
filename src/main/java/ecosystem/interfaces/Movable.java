package ecosystem.interfaces;

import ecosystem.core.*;

/**
 * Contract for entities capable of changing their grid position.
 */
public interface Movable {
    /**
     * Attempt to move within the environment.
     *
     * @param environment the world context
     * @return {@code true} if the entity successfully moved
     */
    boolean move(Environment environment);
}
