package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.animals.Animal;
import ecosystem.behaviors.RandomMovement;

public class IdleState implements EntityState {
    @Override
    public boolean doAction(Animal animal, Environment env) {
        if (animal == null || !animal.isAlive()) return false;

        animal.setEnergy(animal.getEnergy() - 1); // Loses 1 energy

        // Wanders randomly using movement strategy
        new RandomMovement().move(animal, env);

        // Transitions
        if (animal.getEnergy() < animal.getMaxEnergy() * 0.3) {
            animal.setState(new HungryState());
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