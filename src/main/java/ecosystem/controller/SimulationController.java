package ecosystem.controller;

import ecosystem.core.*;
import ecosystem.entities.*;
import ecosystem.entities.animals.*;
import ecosystem.entities.plants.*;
import ecosystem.entities.resources.*;

import javax.swing.Timer;

/**
 Controller for the GUI to interact with the simulation.
 Hides the SimulationEngine and Environment from the UI.
 */
public class SimulationController {

    private final SimulationEngine engine;
    private final Environment environment;

    private Timer runTimer;
    /**
     * Constructs the controller.
     * @param engine The main simulation engine.
     * @param environment The environment model.
     */
    public SimulationController(SimulationEngine engine, Environment environment) {
        this.engine = engine;
        this.environment = environment;
    }

    /**
     * Executes a single simulation step.
     * @return true if the tick and listener updates succeed.
     */
    public boolean executeSingleTick() {
        return engine.tick();
    }

    /**
     * Starts the continuous simulation timer.
     *
     * @param delayMillis Milliseconds between ticks.
     * @return true if started, false if already running.
     */
    public boolean startContinuousRun(int delayMillis) {
        if (runTimer != null && runTimer.isRunning()) {
            return false;
        }

        runTimer = new Timer(delayMillis, event -> engine.tick());
        runTimer.start();
        return true;
    }

    /**
     * Stops the continuous simulation run.
     *
     * @return true if a running timer was stopped, false if there was not running.
     */
    public boolean stopContinuousRun() {
        if (runTimer != null) {
            runTimer.stop();
            runTimer = null;
            return true;
        }
        return false;
    }

    /**
     * Adds a new entity to the simulation world.
     *
     * @param entity the entity to add
     * @return true if successfull
     */
    public boolean addNewEntity(AbstractEntity entity) {
        if (entity == null) return false;
        return environment.addEntity(entity);
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
     * Fully resets the simulation, stopping the timer and clearing the environment.
     *
     * @return true if successfully reset and listeners were notified.
     */
    public boolean resetSimulation() {
        boolean stopped = stopContinuousRun();
        boolean cleared = environment.clear();
        boolean resetOk = engine.reset();
        // Re-seed the environment with the original starting entities so running after reset will produce activity
        boolean seeded = seedInitialWorld();
        // Notify listeners so the UI shows the newly seeded map (engine.reset already did notify; we ensure publish/update)
        boolean published = engine.publishUpdate();
        // Aggregate: consider success if engine listeners succeeded (resetOk/published) and clearing/seed didn't crash
        return resetOk && published;
    }

    /**
     * Populates the world with the default startup layout, skipping occupied positions.
     *
     * @return true when the seeding process finishes.
     */
    private boolean seedInitialWorld() {
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
        return true;
    }
}