package ecosystem.entities.animals;

import ecosystem.behaviors.CarnivoreBehavior;
import ecosystem.behaviors.ChaseMovement;
import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.AbstractEntity;

import java.util.List;

/**
 * A lion on the grid.
 *
 * <p>Lions are carnivores. They chase and eat prey.
 */
public class Lion extends Animal {

    public Lion(Position position) {
        super(position, 'L', 100, 150,
              new ChaseMovement(),
              new CarnivoreBehavior());
    }

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        return false;
    }
}
