package ecosystem.behaviors;

import ecosystem.behaviors.EntityState;
import ecosystem.behaviors.HungryState;
import ecosystem.behaviors.SleepingState;
import ecosystem.core.Environment;
import ecosystem.entities.LivingEntity;
import ecosystem.entities.animals.Animal;
import ecosystem.behaviors.RandomMovement;

public class IdleState implements EntityState {
    @Override
    public void doAction(LivingEntity e, Environment env) {
        e.setEnergy(e.getEnergy() - 1); // Loses 1 energy

        if (e instanceof Animal) {
            // Wanders randomly
            new RandomMovement().move((Animal) e, env);
        }

        // Transitions
        if (e.getEnergy() < e.getMaxEnergy() * 0.3) {
            if (e instanceof Animal) ((Animal) e).setState(new HungryState());
        } else if (isAtCorner(e, env)) {
            if (e instanceof Animal) ((Animal) e).setState(new SleepingState());
        }
    }

    private boolean isAtCorner(LivingEntity e, Environment env) {
        int x = e.getPosition().getX();
        int y = e.getPosition().getY();
        return (x == 0 && y == 0) ||
                (x == 0 && y == Environment.HEIGHT - 1) ||
                (x == Environment.WIDTH - 1 && y == 0) ||
                (x == Environment.WIDTH - 1 && y == Environment.HEIGHT - 1);
    }
}