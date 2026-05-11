package ecosystem.entities.resources;

import ecosystem.core.Position;

/**
 * An impassable boulder on the grid.
 *
 * <p>Because {@code Rock} is a living (alive = {@code true}) entity,
 * {@link ecosystem.core.Environment#isPositionFree} returns {@code false} for
 * any cell occupied by a Rock, effectively blocking all movement through it.
 *
 * <ul>
 *   <li>Symbol: {@code '#'}</li>
 *   <li>blocksMovement: {@code true}</li>
 * </ul>
 */
public class Rock extends Resource {

    /** Rocks permanently block movement. */
    private final boolean blocksMovement = true;

    public Rock(Position position) {
        super(position, '#');
    }

    public boolean isBlocksMovement() { return blocksMovement; }

    @Override
    public String toString() {
        return "Rock " + position;
    }
}
