package ecosystem.entities;

import ecosystem.core.Position;

/**
 * An entity that never changes state over time — it does not act, age, or
 * consume energy.
 *
 * <p>Examples: {@link resources.Rock}, {@link resources.Water}.
 */
public abstract class StaticEntity extends AbstractEntity {

    protected StaticEntity(Position position, char symbol) {
        super(position, symbol);
    }
}
