package ecosystem.behaviors;

import ecosystem.entities.*;
import ecosystem.entities.animals.*;
import ecosystem.interfaces.*;

import java.util.List;

/**
 * Feeding behavior for carnivores.
 *
 * <p>Looks through the nearby entities and finds the closest living entity
 * that a carnivore can eat. If a valid target is found, the animal eats it.
 *
 * <p>Used by {@link ecosystem.entities.animals.Lion}.
 */
public class CarnivoreBehavior implements FeedingBehavior {

@Override
@Override
public boolean eat(Animal animal, List<AbstractEntity> nearby, ecosystem.core.Environment environment) {
    AbstractEntity target = null;
    int minDist = Integer.MAX_VALUE;

    for (AbstractEntity e : nearby) {
        if (e instanceof Consumable
                && ((Consumable) e).isEdibleBy(animal)
                && e.isAlive()) {
            int dist = animal.getPosition().distanceTo(e.getPosition());
            if (dist < minDist) {
                minDist = dist;
                target  = e;
            }
        }
    }

    if (target != null) {
        boolean locked = environment.tryLockEntity(target, 50);
        if (!locked) return false;
        try {
            if (!target.isAlive()) return false;
            if (!(target instanceof Consumable)) return false;
            Consumable consumable = (Consumable) target;
            if (!consumable.isEdibleBy(animal)) return false;
            return animal.eat(consumable);
        } finally {
            environment.unlockEntity(target);
        }
    }
    return false;
}
}
