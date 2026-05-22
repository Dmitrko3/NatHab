package ecosystem.entities.animals;

import ecosystem.behaviors.HerbivoreBehavior;
import ecosystem.behaviors.FeedingBehavior;
import ecosystem.behaviors.RandomMovement;
import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.interfaces.EdibleByCarnivore;
import ecosystem.interfaces.Reproducible;

import java.util.Random;


/**
 * A rabbit on the grid.
 *
 * <p>Rabbits are Carnivores. They move randomly, not eat plants,
 * and can create new rabbits when they have enough energy.
 */
public class Rabbit extends Animal implements EdibleByCarnivore, Reproducible {
    private static final double BABY_STARTING_ENERGY = 20.0;

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
     * Creates a baby rabbit in a nearby free cell.
     *
     * @return {@code true} if the baby rabbit was added successfully
     */
    @Override
    public boolean reproduce(Environment environment) {
        for (int[] offset : ADJACENT) {
            Position candidate = new Position(
                    position.getX() + offset[0],
                    position.getY() + offset[1]);
            if (environment.isPositionFree(candidate)) {
                Rabbit baby = new Rabbit(candidate);
                baby.setEnergy(BABY_STARTING_ENERGY);
                return environment.addEntity(baby);
            }
        }
        return false;   // all adjacent cells occupied
    }
}