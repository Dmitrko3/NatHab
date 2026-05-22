package ecosystem.core;

/**
 * Observer interface for receiving updates when the simulation state changes.
 */
public interface SimulationListener {

    /**
     * Called when the simulation state changes, such as at the end of a tick.
     *
     * @param environment the current simulation environment
     */
    void onSimulationUpdated(Environment environment);
}
