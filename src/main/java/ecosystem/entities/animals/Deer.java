package ecosystem.entities.animals;

import ecosystem.behaviors.diet.HerbivoreBehavior;
import ecosystem.behaviors.movement.RandomMovement;
import ecosystem.engine.Environment;
import ecosystem.engine.Position;
import ecosystem.entities.base.AbstractEntity;
import ecosystem.traits.EdibleByCarnivore;

import java.util.List;
import java.util.Random;
/**
 * A deer on the grid.
 *
 * <p>Deer are herbivores. They eat plants and try to run away from carnivores.
 */
public class Deer extends Animal implements EdibleByCarnivore {
    private static final double BABY_STARTING_ENERGY = 40;
    private static final int[][] ADJACENT = {{0,1},{0,-1},{1,0},{-1,0}};
    private static final Random RANDOM = new Random();

    public Deer(Position position) {
        super(position, 'D', 50, 100,
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

        if (energy > 60 && RANDOM.nextDouble() <= 0.45) {
            reproduce(environment);
        }
        return true;
    }

    // -------------------------------------------------------------------------
    // Reproducible
    // -------------------------------------------------------------------------

    /**
     * Creates a baby deer in a nearby free cell.
     *
     * @return {@code true} if the baby rabbit was added successfully
     */
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
