package ecosystem.entities.animals;

import ecosystem.behaviors.EscapeMovement;
import ecosystem.behaviors.HerbivoreBehavior;
import ecosystem.core.Position;
import ecosystem.interfaces.EdibleByCarnivore;

/**
 * A Deer — herbivore that tries to flee from carnivores.
 *
 * <ul>
 *   <li>Symbol: {@code 'D'}</li>
 *   <li>Starting energy: 70 | Max energy: 120</li>
 *   <li>Movement: {@link EscapeMovement} (flees from non-prey animals)</li>
 *   <li>Feeding:  {@link HerbivoreBehavior}</li>
 *   <li>Marker:   {@link EdibleByCarnivore}</li>
 * </ul>
 */
public class Deer extends Animal implements EdibleByCarnivore {

    public Deer(Position position) {
        super(position, 'D', 70, 120,
              new EscapeMovement(),
              new HerbivoreBehavior());
    }
}
