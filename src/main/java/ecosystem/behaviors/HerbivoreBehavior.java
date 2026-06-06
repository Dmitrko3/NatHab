package ecosystem.behaviors;

import ecosystem.entities.*;
import ecosystem.entities.animals.Animal;
import ecosystem.core.Environment;
import ecosystem.interfaces.Consumable;
import ecosystem.interfaces.EdibleByHerbivore;

import java.util.List;

public class HerbivoreBehavior implements FeedingBehavior {
    private static final long WAIT_TIMEOUT_MS = 300; // tune as appropriate
    private static final long LOCK_TIMEOUT_MS = 50;

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        // Try to find and consume the nearest edible target. If none is found,
        // wait briefly on the environment's resource monitor and re-check once.
        AbstractEntity target = findTarget(animal, nearby);
        if (target != null) {
            return tryConsume(animal, target, environment);
        }

        // No immediate target — wait for notification about new resources.
        Object monitor = environment.getResourceMonitor();
        try {
            synchronized (monitor) {
                monitor.wait(WAIT_TIMEOUT_MS);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }

        // Re-check nearby entities after waking up
        List<AbstractEntity> refreshed = environment.getNearbyEntities(animal.getPosition());
        target = findTarget(animal, refreshed);
        if (target != null) {
            return tryConsume(animal, target, environment);
        }
        return false;
    }
    @Override
    public boolean isHerbivore() { return true; }

    private AbstractEntity findTarget(Animal animal, List<AbstractEntity> nearby) {
        AbstractEntity best = null;
        int minDist = Integer.MAX_VALUE;
        if (nearby == null) return null;
        for (AbstractEntity e : nearby) {
            // Replaced the messy instanceof Consumable/EdibleByHerbivore
            if (e != null && e.isAlive() && e.isEdibleBy(animal)) {
                int d = animal.getPosition().distanceTo(e.getPosition());
                if (d < minDist) { minDist = d; best = e; }
            }
        }
        return best;
    }

    private boolean tryConsume(Animal animal, AbstractEntity target, Environment environment) {
        boolean locked = environment.tryLockEntity(target, LOCK_TIMEOUT_MS);
        if (!locked) return false;
        try {
            if (!target.isAlive() || !target.isEdibleBy(animal)) return false;
            return animal.eat((Animal)target);
        } finally {
            environment.unlockEntity(target);
        }
    }
}