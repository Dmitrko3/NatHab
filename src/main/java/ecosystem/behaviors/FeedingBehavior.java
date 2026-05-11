package ecosystem.behaviors;

import ecosystem.entities.AbstractEntity;
import ecosystem.entities.animals.Animal;

import java.util.List;

/**
 * Strategy interface for animal feeding.
 *
 * <p>Receives the pre-move snapshot of nearby entities so implementations can
 * pick the best target and delegate to {@link Animal#eat}.
 */
public interface FeedingBehavior {
    /**
     * Attempts to eat the most appropriate target from {@code nearby}.
     *
     * @param animal the animal that is eating
     * @param nearby entities perceived by the animal before its move this tick
     * @return {@code true} if something was successfully consumed
     */
    boolean eat(Animal animal, java.util.List<AbstractEntity> nearby);
}
