package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.entities.animals.Animal;
/**
 * Entity sleeps in a corner to recover energy.
 */
public class SleepingState implements EntityState {
    private static final int SLEEP_DURATION = 5;
    private int ticksAsleep = 0;

    @Override
    public boolean doAction(Animal animal, Environment env) {
        if (animal == null || !animal.isAlive()) return false;

        animal.setEnergy(animal.getEnergy() + 2); // Recovers energy while resting
        ticksAsleep++;

        if (ticksAsleep >= SLEEP_DURATION) {
            animal.setState(new IdleState());
        }
        return true;
    }
}