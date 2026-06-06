package ecosystem.entities.plants;

import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.animals.Animal;

import java.util.List;
import java.util.Random;

/**
 * A tree on the grid.
 *
 * <p>Trees grow slowly and can create new trees in nearby cells.
 */
public class Tree extends Plant {

    private static final Random RANDOM   = new Random();
    private static final int[][] ADJACENT = {{0,1},{0,-1},{1,0},{-1,0}};

    public Tree(Position position) {
        super(position, 'T', 80, 150, 5, 0.10);
    }

    // -------------------------------------------------------------------------
    // Reproducible
    // -------------------------------------------------------------------------

    /**
     * Sometimes tries to grow a new tree in a nearby free cell.
     */
    @Override
    public boolean reproduce(Environment environment) {
        if (RANDOM.nextDouble() > reproductionChance) return false;

        for (int[] offset : ADJACENT) {
            Position candidate = new Position(
                    position.getX() + offset[0],
                    position.getY() + offset[1]);
            if (environment.isPositionFree(candidate)) {
                return environment.addEntity(new Tree(candidate));
            }
        }
        return false;
    }

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        return false;
    }
}
