package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.entities.animals.Animal;

/**
 * Strategy interface for animal movement.
 *
 * <p>Implementations decide <em>where</em> an animal moves each tick and are
 * responsible for calling both {@link Animal#setPosition} and
 * {@link Environment#updateEntityPosition} to keep the world consistent.
 */
public interface MovementStrategy {
    /**
     * Moves {@code animal} within {@code environment}.
     *
     * @return {@code true} if the animal successfully changed cell
     */
    boolean move(Animal animal, Environment environment);
}
