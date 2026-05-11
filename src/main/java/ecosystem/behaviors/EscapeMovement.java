package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.animals.Animal;
import ecosystem.interfaces.EdibleByCarnivore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Moves an animal <em>away</em> from the nearest predator in its perception
 * range.  Falls back to {@link RandomMovement} when no threat is detected.
 *
 * <p>A "predator" is defined as any living {@link Animal} that does <em>not</em>
 * implement {@link EdibleByCarnivore} — i.e. not itself prey.
 *
 * <p>Used by {@link ecosystem.entities.animals.Deer}.
 */
public class EscapeMovement implements MovementStrategy {

    private static final Random  RANDOM  = new Random();
    private static final int[][] OFFSETS = {{0,1},{0,-1},{1,0},{-1,0}};

    @Override
    public boolean move(Animal animal, Environment environment) {
        List<AbstractEntity> nearby = environment.getNearbyEntities(animal.getPosition());

        // Identify threatening animals (predators) — alive animals that are NOT prey
        List<Animal> predators = new ArrayList<>();
        for (AbstractEntity e : nearby) {
            if (e instanceof Animal && !(e instanceof EdibleByCarnivore) && e.isAlive()) {
                predators.add((Animal) e);
            }
        }

        if (!predators.isEmpty()) {
            Animal threat = closestOf(predators, animal.getPosition());
            return moveAway(animal, environment, threat.getPosition());
        }
        return randomFallback(animal, environment);
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------

    private static Animal closestOf(List<Animal> animals, Position from) {
        Animal closest = animals.get(0);
        int    minDist = from.distanceTo(closest.getPosition());
        for (Animal a : animals) {
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
