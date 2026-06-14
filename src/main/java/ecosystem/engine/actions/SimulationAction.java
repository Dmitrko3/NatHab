package ecosystem.engine.actions;

import ecosystem.engine.Environment;

/**
 * Represents an action that modifies the environment.
 * Executed by the SimulationEngine thread.
 */
public interface SimulationAction {
    /**
     * Executes this action on the given environment.
     *
     * @param environment the environment to modify
     * @return true if the action succeeds, false otherwise
     */
    boolean execute(Environment environment);
}