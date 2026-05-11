package ecosystem.entities.plants;

import ecosystem.core.Environment;
import ecosystem.core.Position;

import java.util.Random;

/**
 * A Tree — large, slow-growing plant that reproduces into immediately adjacent
 * (distance = 1) cells.
 *
 * <ul>
 *   <li>Symbol: {@code 'T'}</li>
 *   <li>Initial energy: 80 | Max energy: 150 | Growth rate: 5 / tick</li>
 *   <li>Reproduction chance: 10 % per tick; one sapling at distance 1</li>
 * </ul>
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
     * With 10 % probability, attempts to place a sapling in the first free
     * adjacent (distance-1) cell.
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
}
