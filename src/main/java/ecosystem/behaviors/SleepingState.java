package ecosystem.behaviors;

import ecosystem.behaviors.EntityState;
import ecosystem.core.Environment;
import ecosystem.entities.LivingEntity;
import ecosystem.entities.animals.Animal;

public class SleepingState implements EntityState {
    private static final int SLEEP_DURATION = 5;
    private int ticksAsleep = 0;

    @Override
    public void doAction(LivingEntity e, Environment env) {
        e.setEnergy(e.getEnergy() + 2); // Recovers energy while resting
        ticksAsleep++;

        if (ticksAsleep >= SLEEP_DURATION) {
            if (e instanceof Animal) {
                ((Animal) e).setState(new IdleState());
            }
        }
    }
}