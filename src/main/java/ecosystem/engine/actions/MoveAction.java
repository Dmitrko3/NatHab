package ecosystem.engine.actions;

import ecosystem.engine.Environment;
import ecosystem.engine.Position;
import ecosystem.entities.base.AbstractEntity;

/**
 * MoveAction attempts to move an entity from its current position to a new position.
 * The actual move will use existing environment atomic helpers (e.g. updateEntityPosition
 * or tryMoveEntity).
 */
public class MoveAction implements SimulationAction {

    private final AbstractEntity entity;
    private final Position target;
    private final long lockTimeoutMillis;

    public MoveAction(AbstractEntity entity, Position target) {
        this(entity, target, 200);
    }

    public MoveAction(AbstractEntity entity, Position target, long lockTimeoutMillis) {
        this.entity = entity;
        this.target = target;
        this.lockTimeoutMillis = lockTimeoutMillis;
    }

    @Override
    public boolean execute(Environment environment) {
        if (entity == null || target == null || !entity.isAlive()) return false;
        // Prefer the atomic tryMoveEntity helper which uses position locks already.
        return environment.tryMoveEntity(entity, target, lockTimeoutMillis);
    }
}