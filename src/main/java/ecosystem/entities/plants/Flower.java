package ecosystem.entities.plants;

import ecosystem.core.Environment;
import ecosystem.core.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A Flower — small, fast-spreading plant that can scatter 1–3 seeds into cells
 * at Manhattan distance 1 or 2 each tick.
 *
 * <ul>
 *   <li>Symbol: {@code 'F'}</li>
 *   <li>Initial energy: 30 | Max energy: 60 | Growth rate: 3 / tick</li>
 *   <li>Reproduction chance: 20 % per tick; 1–3 offspring at distance ≤ 2</li>
 * </ul>
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
     * With 20 % probability, spawns 1–3 new flowers in randomly chosen free
     * cells at Manhattan distance 1 or 2.
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
}
