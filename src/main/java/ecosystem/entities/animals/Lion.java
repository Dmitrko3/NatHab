package ecosystem.entities.animals;

import ecosystem.behaviors.CarnivoreBehavior;
import ecosystem.behaviors.ChaseMovement;
import ecosystem.core.Position;

/**
 * A Lion — apex predator that chases and eats {@link EdibleByCarnivore} prey.
 *
 * <ul>
 *   <li>Symbol: {@code 'L'}</li>
 *   <li>Starting energy: 100 | Max energy: 150</li>
 *   <li>Movement: {@link ChaseMovement} (hunts nearest prey)</li>
 *   <li>Feeding:  {@link CarnivoreBehavior}</li>
 * </ul>
 */
public class Lion extends Animal {

    public Lion(Position position) {
        super(position, 'L', 100, 150,
              new ChaseMovement(),
              new CarnivoreBehavior());
    }
}
