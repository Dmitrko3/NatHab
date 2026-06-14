package ecosystem.traits;

import ecosystem.engine.*;
import ecosystem.entities.base.AbstractEntity;

import java.util.List;

/**
 * Contract for entities that can perceive nearby entities within their vision
 * range (Manhattan distance ≤ 2).
 */
public interface Sensory {
    /**
     * Returns all living entities within perception range.
     *
     * @param environment the world context
     * @return immutable snapshot of nearby entities (distance ≤ 2, excluding self)
     */
    List<AbstractEntity> sense(Environment environment);
}
