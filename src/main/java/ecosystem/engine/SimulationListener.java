package ecosystem.engine;

/**
 * Listens for simulation state changes.
 */
public interface SimulationListener {

    /**
     * Called after each simulation step.
     *
     * @param environment the current environment
     * @return true if handled successfully
     */
    boolean onSimulationUpdated(Environment environment);
}
