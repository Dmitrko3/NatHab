package ecosystem.interfaces;

import ecosystem.core.*;

/**
 * Marker contract for entities that can perform an action each simulation tick.
 */
public interface Actable {
    /**
     * Execute this entity's behaviour for one simulation tick.
     *
     * @param environment the shared world the entity lives in
     */
    void act(Environment environment);
}
