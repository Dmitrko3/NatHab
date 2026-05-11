package ecosystem.entities.resources;

import ecosystem.core.Position;
import ecosystem.interfaces.Consumable;
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
    public void onConsumed() {
        // intentionally empty
    }

    @Override
    public String toString() {
        return "Water " + position;
    }
}
