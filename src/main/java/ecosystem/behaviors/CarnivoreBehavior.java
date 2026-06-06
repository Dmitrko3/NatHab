package ecosystem.behaviors;

import ecosystem.entities.*;
import ecosystem.entities.animals.Animal;
import ecosystem.core.Environment;
import ecosystem.interfaces.Consumable;

import java.util.List;

public class CarnivoreBehavior implements FeedingBehavior {
    private static final long WAIT_TIMEOUT_MS = 300;
    private static final long LOCK_TIMEOUT_MS = 50;
    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        AbstractEntity target = findTarget(animal, nearby);
        if (target != null) {
            return tryConsume(animal, target, environment);
        }

        // Wait for a newly-added resource (prey) and re-check once.
        Object monitor = environment.getResourceMonitor();
        try {
            synchronized (monitor) {
                monitor.wait(WAIT_TIMEOUT_MS);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }

        List<AbstractEntity> refreshed = environment.getNearbyEntities(animal.getPosition());
        target = findTarget(animal, refreshed);
        if (target != null) {
            return tryConsume(animal, target, environment);
        }
        return false;
    }
    @Override
    public boolean isCarnivore() { return true; } // For HerbivoreBehavior use: isHerbivore() { return true; }

    private AbstractEntity findTarget(Animal animal, List<AbstractEntity> nearby) {
        AbstractEntity best = null;
        int minDist = Integer.MAX_VALUE;
        if (nearby == null) return null;
        for (AbstractEntity e : nearby) {
            // NO instanceof! Let the entity figure out if it's edible.
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
            return animal.eat(target); // No casting! target is natively a Consumable
        } finally {
            environment.unlockEntity(target);
        }}
}