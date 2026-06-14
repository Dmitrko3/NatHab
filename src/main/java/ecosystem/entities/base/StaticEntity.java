package ecosystem.entities.base;

import ecosystem.engine.*;

/**
 * An entity that does not change over time.
 *
 * <p>It does not act, age, or use energy.
 */
public abstract class StaticEntity extends AbstractEntity {

    protected StaticEntity(Position position, char symbol) {
        super(position, symbol);
    }
}
