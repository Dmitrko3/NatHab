package ecosystem.entities.resources;

import ecosystem.core.Position;
import ecosystem.interfaces.Consumable;

/**
 * A permanent water source on the grid.
 *
 * <p>Water is {@link Consumable} (yields 100 energy) but its
 * {@link #onConsumed()} is a no-op — the water tile never disappears.
 *
 * <ul>
 *   <li>Symbol: {@code '~'}</li>
 *   <li>Nutrition value: 100</li>
 * </ul>
 */
public class Water extends Resource implements Consumable {

    public Water(Position position) {
        super(position, '~');
    }

    @Override
    public double getNutritionValue() {
        return 100;
    }

    /** Water replenishes itself — consuming it has no effect on the tile. */
    @Override
    public void onConsumed() {
        // intentionally empty
    }

    @Override
    public String toString() {
        return "Water " + position;
    }
}
