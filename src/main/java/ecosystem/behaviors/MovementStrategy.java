package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.entities.animals.Animal;

/**
 * Movement behavior for animals.
 *
 * <p>Decides where an animal moves during each turn and updates its position
 * in the environment.
 */
public interface MovementStrategy {
    /**
     * Moves the animal in the environment.
     *
     * @return {@code true} if the animal moved to another cell
     */
    boolean move(Animal animal, Environment environment);
}
