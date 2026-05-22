package ecosystem.controller;

import ecosystem.core.Environment;
import ecosystem.core.SimulationEngine;
import ecosystem.entities.AbstractEntity;

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
}