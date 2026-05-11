package ecosystem.entities.animals;

import ecosystem.behaviors.HerbivoreBehavior;
import ecosystem.behaviors.RandomMovement;
import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.interfaces.EdibleByCarnivore;
import ecosystem.interfaces.Reproducible;

import java.util.Random;

/**
 * A Rabbit — fast-reproducing herbivore that wanders randomly.
 *
 * <ul>
 *   <li>Symbol: {@code 'R'}</li>
 *   <li>Starting energy: 50 | Max energy: 100</li>
 *   <li>Movement: {@link RandomMovement}</li>
 *   <li>Feeding:  {@link HerbivoreBehavior}</li>
 *   <li>Markers:  {@link EdibleByCarnivore}, {@link Reproducible}</li>
 * </ul>
 *
 * <p>Each tick, if energy > 30 there is a 30 % chance the rabbit spawns one
 * offspring in an adjacent free cell.
 */
public class Rabbit extends Animal implements EdibleByCarnivore, Reproducible {

    private static final Random RANDOM = new Random();

    /** Adjacent offsets (N, S, E, W). */
    private static final int[][] ADJACENT = {{0,1},{0,-1},{1,0},{-1,0}};

    public Rabbit(Position position) {
        super(position, 'R', 50, 100,
              new RandomMovement(),
              new HerbivoreBehavior());
    }

    // -------------------------------------------------------------------------
    // Actable  (extends Animal.act with reproduction)
    // -------------------------------------------------------------------------

    @Override
    public void act(Environment environment) {
        super.act(environment);         // move + eat (plus energy drain / aging)
        if (!alive) return;

        if (energy > 30 && RANDOM.nextDouble() <= 0.30) {
            reproduce(environment);
        }
    }

    // -------------------------------------------------------------------------
    // Reproducible
    // -------------------------------------------------------------------------

    /**
     * Places one newborn rabbit in the first free adjacent cell found.
     *
     * @return {@code true} if a baby was successfully placed
     */
    @Override
    public boolean reproduce(Environment environment) {
        for (int[] offset : ADJACENT) {
            Position candidate = new Position(
                    position.getX() + offset[0],
                    position.getY() + offset[1]);
            if (environment.isPositionFree(candidate)) {
                return environment.addEntity(new Rabbit(candidate));
            }
        }
        return false;   // all adjacent cells occupied
    }
}
