package ecosystem.network;

import ecosystem.engine.Environment;
import ecosystem.engine.actions.SimulationAction;

/**
 * Small adapter so NetworkCommand objects can be submitted to Environment.submitAction(...)
 * and run on the SimulationEngine thread.
 */
public class NetworkSimulationAction implements SimulationAction {

    private final NetworkCommand command;

    public NetworkSimulationAction(NetworkCommand command) {
        this.command = command;
    }

    @Override
    public boolean execute(Environment environment) {
        try {
            return command.execute(environment);
        } catch (Exception ex) {
            System.err.println("NetworkSimulationAction execution failed: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}