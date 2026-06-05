package ecosystem.behaviors;

import ecosystem.core.*;
import ecosystem.entities.animals.*;

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
        // Attempt an atomic move with a short timeout
        boolean moved = environment.tryMoveEntity(animal, candidate, 50);
        if (moved) return true;
    }
    return false;
}

/**
 * Legacy helper kept for compatibility — delegates to environment.tryMoveEntity.
 */
static boolean applyMove(Animal animal, Environment environment, Position newPos) {
    if (environment.isPositionFree(newPos)) {
        boolean ok = RandomMovement.applyMove(animal, environment, newPos);
        return ok;
    }
    return false;
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
