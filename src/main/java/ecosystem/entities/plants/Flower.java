package ecosystem.entities.plants;

import ecosystem.engine.Environment;
import ecosystem.engine.Position;
import ecosystem.entities.base.AbstractEntity;
import ecosystem.entities.animals.Animal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A flower on the grid.
 *
 * <p>Flowers grow quickly and can spread seeds to nearby cells.
 */
public class Flower extends Plant {

    private static final Random RANDOM = new Random();

    public Flower(Position position) {
        super(position, 'F', 30, 60, 3, 0.20);
    }

    // -------------------------------------------------------------------------
    // Reproducible
    // -------------------------------------------------------------------------

    /**
     * Sometimes creates new flowers in nearby free cells.
     */
    @Override
    public boolean reproduce(Environment environment) {
        if (RANDOM.nextDouble() > reproductionChance) return false;

        int desiredOffspring = 1 + RANDOM.nextInt(3); // 1, 2, or 3

        // Collect all candidate positions at distance 1 or 2
        List<Position> candidates = new ArrayList<>();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int dist = Math.abs(dx) + Math.abs(dy);
                if (dist >= 1 && dist <= 2) {
                    candidates.add(new Position(
                            position.getX() + dx,
                            position.getY() + dy));
                }
            }
        }

        Collections.shuffle(candidates);

        int spawned = 0;
        for (Position candidate : candidates) {
            if (spawned >= desiredOffspring) break;
            if (environment.isPositionFree(candidate)) {
                if (environment.addEntity(new Flower(candidate))) {
                    spawned++;
                }
            }
        }
        return spawned > 0;
    }

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        return false;
    }
}
