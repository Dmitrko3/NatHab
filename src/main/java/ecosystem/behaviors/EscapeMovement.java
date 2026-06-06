package ecosystem.behaviors;

import ecosystem.core.*;
import ecosystem.entities.*;
import ecosystem.entities.animals.*;
import ecosystem.interfaces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Moves an animal one step toward the nearest prey.
 *
 * <p>If there is no prey nearby, the animal moves randomly.
 *
 * <p>Used by {@link ecosystem.entities.animals.Lion}.
 */
public class EscapeMovement implements MovementStrategy {

    private static final Random  RANDOM  = new Random();
    private static final int[][] OFFSETS = {{0,1},{0,-1},{1,0},{-1,0}};

    @Override
    public boolean move(Animal animal, Environment environment) {
        List<AbstractEntity> nearby = environment.getNearbyEntities(animal.getPosition());

        // Identify threatening animals (predators) — alive animals that are NOT prey
        List<AbstractEntity> predators = new ArrayList<>();
        for (AbstractEntity e : nearby) {
            // If the nearby entity is alive and considers ME edible, it's a threat!
            if (e != null && e.isAlive() && animal.isEdibleBy(e)) {
                predators.add(e);
            }
        }
        if (!predators.isEmpty()) {
            AbstractEntity threat = closestOf(predators, animal.getPosition());
            return moveAway(animal, environment, threat.getPosition());
        }
        return randomFallback(animal, environment);
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------

    private static AbstractEntity closestOf(List<AbstractEntity> animals, Position from) {
        AbstractEntity closest = animals.get(0);
        int    minDist = from.distanceTo(closest.getPosition());
        for (AbstractEntity a : animals) {
            int d = from.distanceTo(a.getPosition());
            if (d < minDist) { minDist = d; closest = a; }
        }
        return closest;
    }

    /** Picks the free neighbour that maximises distance from {@code threatPos}. */
    private boolean moveAway(Animal animal, Environment environment, Position threatPos) {
        Position cur = animal.getPosition();
        int dx = Integer.signum(cur.getX() - threatPos.getX()); // direction AWAY
        int dy = Integer.signum(cur.getY() - threatPos.getY());

        // Primary escape direction, then perpendicular options
        Position[] candidates = {
            new Position(cur.getX() + dx, cur.getY() + dy),
            new Position(cur.getX() + dx, cur.getY()),
            new Position(cur.getX(),       cur.getY() + dy),
            new Position(cur.getX() - dx,  cur.getY()),      // sideways
            new Position(cur.getX(),        cur.getY() - dy) // sideways
        };

        for (Position candidate : candidates) {
            if (environment.isPositionFree(candidate)) {
                // Create action and submit instead of direct move
                SimulationAction moveAction = new ecosystem.core.MoveAction(animal, candidate, 50);
                boolean queued = environment.submitAction(moveAction);

                return queued;
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
                // Create action and submit instead of direct move
                SimulationAction moveAction = new ecosystem.core.MoveAction(animal, candidate, 50);
                return environment.submitAction(moveAction);
            }
        }
        return false;
    }
}
