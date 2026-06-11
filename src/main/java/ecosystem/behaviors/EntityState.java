package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.entities.animals.Animal;

/**
 * State behavior for animals.
 * doAction returns true if the state's logic executed successfully, false otherwise.
 */
public interface EntityState {
    boolean doAction(Animal animal, Environment env);
}