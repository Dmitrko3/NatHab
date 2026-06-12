package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.animals.Animal;

import java.util.List;
/**
 * Entity loses energy rapidly and seeks food.
 */
public class HungryState implements EntityState {
    @Override
    public boolean doAction(Animal animal, Environment env) {
        if (animal == null || !animal.isAlive()) return false;

        animal.setEnergy(animal.getEnergy() - 5); // Loses 5 energy

        // animal methods are now directly available (no instanceof)
        List<AbstractEntity> nearby = animal.sense(env);
        animal.getMovementStrategy().move(animal, env);
        animal.getFeedingBehavior().eat(animal, nearby, env);

        // State transitions
        if (animal.getEnergy() > animal.getMaxEnergy() * 0.8) {
            animal.setState(new IdleState());
        } else if (isAtCorner(animal, env)) {
            animal.setState(new SleepingState());
        }
        return true;
    }

    private boolean isAtCorner(Animal a, Environment env) {
        int x = a.getPosition().getX();
        int y = a.getPosition().getY();
        return (x == 0 && y == 0) ||
                (x == 0 && y == Environment.HEIGHT - 1) ||
                (x == Environment.WIDTH - 1 && y == 0) ||
                (x == Environment.WIDTH - 1 && y == Environment.HEIGHT - 1);
    }
}