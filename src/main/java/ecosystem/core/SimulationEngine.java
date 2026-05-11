package ecosystem.core;

import ecosystem.entities.AbstractEntity;
import ecosystem.entities.animals.Animal;
import ecosystem.entities.plants.Plant;
import ecosystem.interfaces.Actable;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs the simulation one step at a time.
 *
 * <p>During each step, living entities act, dead entities are removed,
 * and the world is shown.
 */
public class SimulationEngine {

    private final Environment environment;
    private int tickCount;

    public SimulationEngine(Environment environment) {
        this.environment = environment;
        this.tickCount   = 0;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /** Advance the simulation by one tick. */
    public void tick() {
        tickCount++;

        // --- 1. Act phase (snapshot prevents ConcurrentModificationException) ---
        List<AbstractEntity> snapshot = new ArrayList<>(environment.getEntitiesList());
        for (AbstractEntity entity : snapshot) {
            if (entity instanceof Actable && entity.isAlive()) {
                ((Actable) entity).act(environment);
            }
        }

        // --- 2. Cleanup phase ---
        List<AbstractEntity> dead = new ArrayList<>();
        for (AbstractEntity entity : environment.getEntitiesList()) {
            if (!entity.isAlive()) {
                dead.add(entity);
            }
        }
        for (AbstractEntity entity : dead) {
            environment.removeEntity(entity);
        }

        // --- 3. Render phase ---
        printMap();
        printStats();
    }

    public int getTickCount() { return tickCount; }

    // -------------------------------------------------------------------------
    // Private rendering helpers
    // -------------------------------------------------------------------------

    private void printMap() {
        // Build a character grid, defaulting every cell to '.'
        char[][] grid = new char[Environment.HEIGHT][Environment.WIDTH];
        for (int row = 0; row < Environment.HEIGHT; row++) {
            java.util.Arrays.fill(grid[row], '.');
        }

        for (AbstractEntity entity : environment.getEntitiesList()) {
            if (entity.isAlive()) {
                Position p = entity.getPosition();
                grid[p.getY()][p.getX()] = entity.getSymbol();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Tick ").append(tickCount).append(" ===\n");
        sb.append("+").append("-".repeat(Environment.WIDTH)).append("+\n");
        for (int row = 0; row < Environment.HEIGHT; row++) {
            sb.append("|");
            for (int col = 0; col < Environment.WIDTH; col++) {
                sb.append(grid[row][col]);
            }
            sb.append("|\n");
        }
        sb.append("+").append("-".repeat(Environment.WIDTH)).append("+");
        System.out.println(sb);
    }

    private void printStats() {
        List<AbstractEntity> all = environment.getEntitiesList();

        long totalAlive = all.stream().filter(AbstractEntity::isAlive).count();
        long animals    = all.stream()
                             .filter(e -> e instanceof Animal && e.isAlive())
                             .count();
        long plants     = all.stream()
                             .filter(e -> e instanceof Plant && e.isAlive())
                             .count();

        System.out.printf("Stats: total_alive=%-4d | animals=%-4d | plants=%-4d%n",
                          totalAlive, animals, plants);
    }
}
