package ecosystem.entities;

import ecosystem.core.*;

/**
 * Base class for all entities.
 *
 * <p>Each entity has a position, a display symbol, and an alive state.
 */
public abstract class AbstractEntity {

    protected Position position;
    protected final char symbol;
    protected boolean alive;

    protected AbstractEntity(Position position, char symbol) {
        this.position = position;
        this.symbol   = symbol;
        this.alive    = true;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public Position getPosition() { return position; }

    /**
     * Updates this entity's position.
     */
    public void setPosition(Position position) { this.position = position; }

    public char getSymbol() { return symbol; }

    public boolean isAlive() { return alive; }

    public void setAlive(boolean alive) { this.alive = alive; }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    /**
     * Identity equality — no downcasting.
     */
    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return String.format("%s %s alive=%b", getClass().getSimpleName(), position, alive);
    }
}
