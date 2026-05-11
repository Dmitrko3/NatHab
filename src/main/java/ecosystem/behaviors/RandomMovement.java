package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.animals.Animal;

import java.util.Random;

/**
 * Moves an animal to a random free nearby cell.
 *
 * <p>The animal checks the four main directions in random order.
 * If there is no free cell, it stays where it is.
 */
public class RandomMovement implements MovementStrategy {

    private static final Random RANDOM = new Random();
    private static final int[][] OFFSETS = {{0,1},{0,-1},{1,0},{-1,0}};

    @Override
    public boolean move(Animal animal, Environment environment) {
        int[] order = shuffledIndices();
        for (int idx : order) {
            Position candidate = new Position(
                    animal.getPosition().getX() + OFFSETS[idx][0],
                    animal.getPosition().getY() + OFFSETS[idx][1]);
            if (environment.isPositionFree(candidate)) {
                applyMove(animal, environment, candidate);
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Shared helper used by subclasses
    // -------------------------------------------------------------------------

    static void applyMove(Animal animal, Environment environment, Position newPos) {
        Position oldPos = animal.getPosition();
        animal.setPosition(newPos);
        environment.updateEntityPosition(animal, oldPos, newPos);
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------

    private static int[] shuffledIndices() {
        int[] indices = {0, 1, 2, 3};
        for (int i = 3; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            int tmp = indices[i]; indices[i] = indices[j]; indices[j] = tmp;
        }
        return indices;
    }
}
