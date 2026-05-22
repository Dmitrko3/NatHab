package ecosystem.behaviors;

import ecosystem.entities.*;
import ecosystem.entities.animals.*;
import ecosystem.interfaces.*;

import java.util.List;

/**
 * Feeding behavior for herbivores.
 *
 * <p>Looks through nearby entities and finds the closest living thing
 * that a herbivore can eat. If one is found, the animal eats it.
 *
 * <p>Used by {@link ecosystem.entities.animals.Deer} and
 * {@link ecosystem.entities.animals.Rabbit}.
 */
public class HerbivoreBehavior implements FeedingBehavior {

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby) {
        AbstractEntity target = null;
        int minDist = Integer.MAX_VALUE;

        for (AbstractEntity e : nearby) {
            if (e instanceof EdibleByHerbivore
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
