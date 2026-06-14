package ecosystem.engine;

import ecosystem.entities.base.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs the simulation step-by-step, updating entities and the environment.
 */
public class SimulationEngine {

    private final Environment environment;
    private final List<SimulationListener> listeners;
    private int tickCount;

    /**
     * Creates a new simulation engine.
     *
     * @param environment the ecosystem environment to simulate
     */
    public SimulationEngine(Environment environment) {
        this.environment = environment;
        this.listeners = new ArrayList<>();
        this.tickCount = 0;
    }

    /**
     * Adds a listener to watch the simulation.
     *
     * @param listener the listener to add
     * @return true if added successfully, false if the listener is null
     */
    public boolean addSimulationListener(SimulationListener listener) {
        if (listener == null) return false;
        listeners.add(listener);
        return true;
    }

    /**
     * Removes a listener from the simulation.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed
     */
    public boolean removeSimulationListener(SimulationListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Moves the simulation forward by one step.
     *
     * @return true if all listeners were updated successfully
     */
    public boolean tick() {
        tickCount++;
        environment.drainAndExecuteActions();

        // 1. Act phase (No instanceof Actable!)
        List<AbstractEntity> snapshot = new ArrayList<>(environment.getEntitiesList());
        for (AbstractEntity entity : snapshot) {
            if (entity.isAlive()) {
                try {
                    boolean ok = entity.act(environment);
                    if (!ok) System.err.println("Warning: entity " + entity + " act() returned false");
                } catch (Exception ex) {
                    System.err.println("entity.act() threw: " + ex.getMessage());
                }
            }
        }

        // Cleanup phase
        List<AbstractEntity> dead = new ArrayList<>();
        for (AbstractEntity entity : environment.getEntitiesList()) {
            if (!entity.isAlive()) {
                dead.add(entity);
            }
        }
        for (AbstractEntity entity : dead) {
            environment.removeEntity(entity);
        }

        //Render phase
        printMap();
        printStats();

        //Notify observers
        return notifySimulationListeners();
    }

    /**
     * Gets the current step number.
     *
     * @return the number of elapsed ticks
     */
    public int getTickCount() { return tickCount; }

    /**
     * Restarts the tick counter and broadcasts the initial state.
     *
     * @return true if all listeners were updated successfully
     */
    public boolean reset() {
        this.tickCount = 0;
        return notifySimulationListeners();
    }

    /**
     * Sends the current state to listeners without advancing time.
     *
     * @return true if all listeners were updated successfully
     */
    public boolean publishUpdate() {
        return notifySimulationListeners();
    }

    /**
     * Tells all listeners that the simulation has updated.
     *
     * @return true if no listener threw an exception and all returned success
     */
    private boolean notifySimulationListeners() {
        boolean allOk = true;
        for (SimulationListener listener : listeners) {
            try {
                boolean ok = listener.onSimulationUpdated(environment);
                if (!ok) allOk = false;
            } catch (Exception ex) {
                allOk = false;
            }
        }
        return allOk;
    }

    /**
     * Draws the current map grid in the console.
     *
     * @return true upon successful rendering
     */
    private boolean printMap() {
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
        return true;
    }

    /**
     * Prints the number of living animals and plants.
     *
     * @return true upon successful printing
     */
    private boolean printStats() {
        List<AbstractEntity> all = environment.getEntitiesList();

        long totalAlive = all.stream().filter(AbstractEntity::isAlive).count();
        // Pure polymorphism, no instanceof Animal/Plant
        long animals = all.stream().filter(e -> e.isAnimal() && e.isAlive()).count();
        long plants  = all.stream().filter(e -> e.isPlant() && e.isAlive()).count();

        System.out.printf("Stats: total_alive=%-4d | animals=%-4d | plants=%-4d%n",
                totalAlive, animals, plants);
        return true;
    }
}