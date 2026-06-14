package ecosystem.entities.animals;

import ecosystem.behaviors.diet.HerbivoreBehavior;
import ecosystem.behaviors.movement.RandomMovement;
import ecosystem.engine.Environment;
import ecosystem.engine.Position;
import ecosystem.entities.base.AbstractEntity;
import ecosystem.traits.EdibleByCarnivore;
import ecosystem.traits.Reproducible;

import java.util.List;
import java.util.Random;


/**
 * A rabbit on the grid.
 *
 * <p>Rabbits are Carnivores. They move randomly, not eat plants,
 * and can create new rabbits when they have enough energy.
 */
public class Rabbit extends Animal implements EdibleByCarnivore, Reproducible {
    private static final double BABY_STARTING_ENERGY = 30.0;

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
    public boolean act(Environment environment) {
        super.act(environment);         // move + eat (plus energy drain / aging)
        if (!alive) return false;

        if (energy > 30 && RANDOM.nextDouble() <= 0.30) {
            reproduce(environment);
        }
        return true;
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

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        return true;
    }
}