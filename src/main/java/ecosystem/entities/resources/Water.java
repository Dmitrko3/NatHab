package ecosystem.entities.resources;

import ecosystem.core.*;
import ecosystem.interfaces.*;
/**
 * A water source on the grid.
 *
 * <p>Water can be consumed for energy, but it does not disappear.
 */
public class Water extends Resource implements Consumable {

    public Water(Position position) {
        super(position, '~');
    }

    @Override
    public double getNutritionValue() {
        return 100;
    }

    /** Consuming water does not remove it from the grid. */
    @Override
    public boolean onConsumed() {
        // intentionally empty
        return true;
    }

    /** All eaters can consume water (drinking) for energy. */
    @Override
    public boolean isEdibleBy(ecosystem.interfaces.Eater consumer) {
        return consumer != null; // or more refined logic if you want
    }

    @Override
    public String toString() {
        return "Water " + position;
    }
}
