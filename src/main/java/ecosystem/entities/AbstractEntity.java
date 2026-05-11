package ecosystem.entities;

import ecosystem.core.Position;

/**
 * Root of the entity hierarchy.
 *
 * <p>All concrete entity types live on the simulation grid and carry at least a
 * position, a display symbol, and an alive flag.
 *
 * <p><b>equals contract:</b> two entities are equal only when they are the same
 * object (reference / identity equality).  No downcasting is performed.
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
     * Updates this entity's position field.  Callers must also call
     * {@link ecosystem.core.Environment#updateEntityPosition} to keep the
     * spatial map consistent.
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
