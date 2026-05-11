package ecosystem.entities.animals;

import ecosystem.behaviors.EscapeMovement;
import ecosystem.behaviors.HerbivoreBehavior;
import ecosystem.core.Position;
import ecosystem.interfaces.EdibleByCarnivore;

/**
 * A deer on the grid.
 *
 * <p>Deer are herbivores. They eat plants and try to run away from carnivores.
 */
public class Deer extends Animal implements EdibleByCarnivore {

    public Deer(Position position) {
        super(position, 'D', 70, 120,
              new EscapeMovement(),
              new HerbivoreBehavior());
    }
}
