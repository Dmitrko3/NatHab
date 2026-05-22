package ecosystem.controller;

import ecosystem.core.*;
import ecosystem.entities.*;
import ecosystem.entities.animals.*;
import ecosystem.entities.plants.*;
import ecosystem.entities.resources.*;

import javax.swing.Timer;

/**
 * Controller layer between the GUI and the simulation model.
 *
 * The GUI should call this class instead of directly controlling
 * the SimulationEngine or Environment.
 */
public class SimulationController {

    private final SimulationEngine engine;
    private final Environment environment;

    private Timer runTimer;

    public SimulationController(SimulationEngine engine, Environment environment) {
        this.engine = engine;
        this.environment = environment;
    }

    /**
     * Executes exactly one simulation tick.
     * Useful for a "Step" or "Next Tick" GUI button.
     */
    public void executeSingleTick() {
        engine.tick();
    }

    /**
     * Starts running the simulation continuously using a Swing timer.
     *
     * @param delayMillis time between ticks in milliseconds
     */
    public void startContinuousRun(int delayMillis) {
        if (runTimer != null && runTimer.isRunning()) {
            return;
        }

        runTimer = new Timer(delayMillis, event -> engine.tick());
        runTimer.start();
    }

    /**
     * Stops the continuous simulation run.
     */
    public void stopContinuousRun() {
        if (runTimer != null) {
            runTimer.stop();
            runTimer = null;
        }
    }

    /**
     * Adds a new entity to the simulation world.
     *
     * @param entity the entity to add
     */
    public void addNewEntity(AbstractEntity entity) {
        if (entity != null) {
            environment.addEntity(entity);
        }
    }

    /**
     * Tells the GUI whether the simulation is currently running.
     *
     * @return true if continuous run is active, false otherwise
     */
    public boolean isRunning() {
        return runTimer != null && runTimer.isRunning();
    }

    /**
     * Gives access to the current tick count.
     *
     * @return current simulation tick count
     */
    public int getTickCount() {
        return engine.getTickCount();
    }

    /**
     * Resets the whole simulation: stops any running timer, clears the environment
     * and resets the engine state so the UI shows an empty map.
     */
    public void resetSimulation() {
        stopContinuousRun();
        environment.clear();
        engine.reset();
        // Re-seed the environment with the original starting entities so
        // running after reset will produce activity (reproduction, movement, etc.)
        seedInitialWorld();
        // Notify listeners so the UI shows the newly seeded map
        engine.publishUpdate();
    }

    /**
     * Place the default initial entities into the world (same layout as startup).
     * Attempts to add each entity; if a position is occupied it's skipped.
     */
    private void seedInitialWorld() {
        // ---- Static resources ----
        environment.addEntity(new Rock(new ecosystem.core.Position(3, 3)));
        environment.addEntity(new Rock(new ecosystem.core.Position(4, 3)));
        environment.addEntity(new Rock(new ecosystem.core.Position(5, 3)));
        environment.addEntity(new Rock(new ecosystem.core.Position(10, 10)));
        environment.addEntity(new Rock(new ecosystem.core.Position(11, 10)));

        environment.addEntity(new Water(new ecosystem.core.Position(8,  8)));
        environment.addEntity(new Water(new ecosystem.core.Position(9,  8)));
        environment.addEntity(new Water(new ecosystem.core.Position(8,  9)));

        // ---- Plants ----
        environment.addEntity(new Tree(new ecosystem.core.Position(2,  2)));
        environment.addEntity(new Tree(new ecosystem.core.Position(15, 15)));
        environment.addEntity(new Tree(new ecosystem.core.Position(1,  12)));

        environment.addEntity(new Flower(new ecosystem.core.Position(6,  6)));
        environment.addEntity(new Flower(new ecosystem.core.Position(12, 4)));
        environment.addEntity(new Flower(new ecosystem.core.Position(7,  14)));
        environment.addEntity(new Flower(new ecosystem.core.Position(17, 7)));

        // ---- Herbivores ----
        environment.addEntity(new Deer(new ecosystem.core.Position(5,  5)));
        environment.addEntity(new Deer(new ecosystem.core.Position(14, 14)));

        environment.addEntity(new Rabbit(new ecosystem.core.Position(6,  7)));
        environment.addEntity(new Rabbit(new ecosystem.core.Position(11, 11)));
        environment.addEntity(new Rabbit(new ecosystem.core.Position(3,  12)));
        environment.addEntity(new Rabbit(new ecosystem.core.Position(16, 3)));

        // ---- Carnivore ----
        environment.addEntity(new Lion(new ecosystem.core.Position(10, 5)));
    }
}