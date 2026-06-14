package ecosystem.engine.actions;

import ecosystem.engine.Environment;
import ecosystem.entities.base.AbstractEntity;

/**
 * SimulationAction that removes an entity when executed by the engine thread.
 */
public class RemoveEntityAction implements SimulationAction {

    private final AbstractEntity entity;

    public RemoveEntityAction(AbstractEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean execute(Environment environment) {
        if (entity == null || environment == null) return false;
        return environment.removeEntity(entity);
    }
}