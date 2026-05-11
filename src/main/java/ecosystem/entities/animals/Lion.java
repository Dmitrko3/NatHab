package ecosystem.entities.animals;

import ecosystem.behaviors.CarnivoreBehavior;
import ecosystem.behaviors.ChaseMovement;
import ecosystem.core.Position;

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
}
