package ecosystem.behaviors.state;

import ecosystem.engine.Environment;
import ecosystem.entities.animals.Animal;

/**
 * State behavior for animals.
 * doAction returns true if the state's logic executed successfully, false otherwise.
 */
public interface EntityState {
    /**
     * Executes state-specific behavior.
     * @param e The acting entity.
     * @param env The simulation environment.
     */
    boolean doAction(Animal animal, Environment env);
}