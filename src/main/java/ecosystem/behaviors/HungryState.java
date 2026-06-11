package ecosystem.behaviors;

import ecosystem.behaviors.EntityState;
import ecosystem.core.Environment;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.LivingEntity;
import ecosystem.entities.animals.Animal;
import java.util.List;

public class HungryState implements EntityState {
    @Override
    public void doAction(LivingEntity e, Environment env) {
        e.setEnergy(e.getEnergy() - 5); // Loses 5 energy

        if (e instanceof Animal) {
            Animal animal = (Animal) e;
            List<AbstractEntity> nearby = animal.sense(env);
            animal.getMovementStrategy().move(animal, env);
            animal.getFeedingBehavior().eat(animal, nearby, env);
        }

        // Transitions
        if (e.getEnergy() > e.getMaxEnergy() * 0.8) {
            if (e instanceof Animal) ((Animal) e).setState(new ecosystem.behaviors.IdleState());
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