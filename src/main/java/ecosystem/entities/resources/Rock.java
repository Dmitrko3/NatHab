package ecosystem.entities.resources;

import ecosystem.core.*;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.animals.Animal;

import java.util.List;

/**
 * A rock on the grid.
 *
 * <p>It blocks movement, so other entities cannot move onto its cell.
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

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        return false;
    }
}
