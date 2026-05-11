package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.animals.Animal;
import ecosystem.interfaces.EdibleByCarnivore;

import java.util.List;
import java.util.Random;

/**
 * Moves an animal one step toward the nearest {@link EdibleByCarnivore} in its
 * perception range.  Falls back to {@link RandomMovement} when no prey is
 * visible.
 *
 * <p>Used by {@link ecosystem.entities.animals.Lion}.
 */
public class ChaseMovement implements MovementStrategy {

    private static final Random RANDOM  = new Random();
    private static final int[][] OFFSETS = {{0,1},{0,-1},{1,0},{-1,0}};

    @Override
    public boolean move(Animal animal, Environment environment) {
        List<AbstractEntity> nearby = environment.getNearbyEntities(animal.getPosition());

        // Find the nearest live prey
        AbstractEntity target = null;
        int minDist = Integer.MAX_VALUE;
        for (AbstractEntity e : nearby) {
            if (e instanceof EdibleByCarnivore && e.isAlive()) {
                int dist = animal.getPosition().distanceTo(e.getPosition());
                if (dist < minDist) {
                    minDist = dist;
                    target  = e;
                }
            }
        }

        if (target != null) {
            return moveToward(animal, environment, target.getPosition());
        }
        return randomFallback(animal, environment);
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------

    /**
     * Moves one step toward {@code goal} by choosing the free neighbour that
     * reduces Manhattan distance the most.
     */
    private boolean moveToward(Animal animal, Environment environment, Position goal) {
        Position cur = animal.getPosition();
        int dx = Integer.signum(goal.getX() - cur.getX());
        int dy = Integer.signum(goal.getY() - cur.getY());

        // Priority: horizontal/vertical toward goal first, then sideways, then back
        Position[] candidates = {
            new Position(cur.getX() + dx, cur.getY()),
            new Position(cur.getX(),       cur.getY() + dy),
            new Position(cur.getX() - dy,  cur.getY() + dx), // perpendicular
            new Position(cur.getX() + dy,  cur.getY() - dx)  // perpendicular
        };

        for (Position candidate : candidates) {
            if (environment.isPositionFree(candidate)) {
                RandomMovement.applyMove(animal, environment, candidate);
                return true;
            }
        }
        return randomFallback(animal, environment);
    }

    private boolean randomFallback(Animal animal, Environment environment) {
        int[] order = {0, 1, 2, 3};
        for (int i = 3; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            int tmp = order[i]; order[i] = order[j]; order[j] = tmp;
        }
        for (int idx : order) {
            Position candidate = new Position(
                    animal.getPosition().getX() + OFFSETS[idx][0],
                    animal.getPosition().getY() + OFFSETS[idx][1]);
            if (environment.isPositionFree(candidate)) {
                RandomMovement.applyMove(animal, environment, candidate);
                return true;
            }
        }
        return false;
    }
}
