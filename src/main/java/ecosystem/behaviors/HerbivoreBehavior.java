package ecosystem.behaviors;

import ecosystem.entities.*;
import ecosystem.entities.animals.*;
import ecosystem.core.Environment;

import java.util.List;

/**
 * Feeding behavior for animals.
 *
 * <p>Uses the nearby entities to choose what the animal should try to eat.
 */
public interface FeedingBehavior {
    /**
     * Tries to eat the best available target nearby.
     *
     * @param animal the animal that is eating
     * @param nearby nearby entities the animal can see
     * @param environment the environment (used for locking / validation)
     * @return {@code true} if the animal ate something
     */
    boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment);
}