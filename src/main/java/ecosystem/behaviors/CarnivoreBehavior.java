package ecosystem.behaviors;

import ecosystem.entities.AbstractEntity;
import ecosystem.entities.animals.Animal;
import ecosystem.interfaces.Consumable;
import ecosystem.interfaces.EdibleByCarnivore;

import java.util.List;

/**
 * Feeding strategy for carnivores.
 *
 * <p>Scans the pre-move nearby list for the closest live
 * {@link EdibleByCarnivore} entity that is also {@link Consumable}, then
 * delegates consumption to {@link Animal#eat}.
 *
 * <p>Used by {@link ecosystem.entities.animals.Lion}.
 */
public class CarnivoreBehavior implements FeedingBehavior {

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby) {
        AbstractEntity target = null;
        int minDist = Integer.MAX_VALUE;

        for (AbstractEntity e : nearby) {
            if (e instanceof EdibleByCarnivore
                    && e instanceof Consumable
                    && e.isAlive()) {
                int dist = animal.getPosition().distanceTo(e.getPosition());
                if (dist < minDist) {
                    minDist = dist;
                    target  = e;
                }
            }
        }

        if (target != null) {
            return animal.eat((Consumable) target);
        }
        return false;
    }
}
