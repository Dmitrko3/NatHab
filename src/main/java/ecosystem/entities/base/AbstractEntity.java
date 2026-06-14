package ecosystem.entities.base;

import ecosystem.behaviors.diet.FeedingBehavior;
import ecosystem.engine.*;
import ecosystem.traits.*;

/**
 * Base class for all entities.
 *
 * <p>Each entity has a position, a display symbol, and an alive state.
 *
 * <p>This class exposes lightweight polymorphic accessors for "energy" and "age".
 * Non-living entities return neutral defaults (energy = 0, age = 0). LivingEntity
 * overrides these so callers can operate on AbstractEntity.
 */
public abstract class AbstractEntity implements FeedingBehavior, Consumable, Actable, Eater {

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
    // Identification & lightweight "living" API (defaults for non-living)
    // -------------------------------------------------------------------------
    public boolean isPlant() {
        return false;
    }
    public boolean isAnimal() { return false; }

    /**
     * Returns the current energy. Default for non-living entities = 0.0.
     * LivingEntity overrides this.
     */
    public double getEnergy() {
        return 0.0;
    }

    /**
     * Returns the maximum energy supported by this entity. Default 0.0.
     * LivingEntity overrides this.
     */
    public double getMaxEnergy() {
        return 0.0;
    }

    /**
     * Sets energy; default no-op for non-living entities.
     * LivingEntity overrides with meaningful behavior.
     */
    public void setEnergy(double energy) {
    }

    /**
     * Returns age (default 0 for non-living).
     */
    public int getAge() {
        return 0;
    }

    @Override
    public double getNutritionValue() {
        return 0.0;
    }

    @Override
    public boolean onConsumed() {
        return false;
    }

    @Override
    public boolean isEdibleBy(Eater consumer) {
        return false;
    }

    @Override
    public boolean eat(Consumable target) {
        return false;
    }

    @Override
    public void addEnergy(double amount) {
    }

    @Override
    public boolean act(Environment environment) {
        return true;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
    @Override
    public boolean isCarnivore() { return false; }
    @Override
    public boolean isHerbivore() { return false; }
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