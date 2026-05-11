package ecosystem.entities;

import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.interfaces.Actable;

/**
 * An entity that is alive and changes each turn.
 *
 * <p>Each turn, it gets older and loses energy.
 * If its energy reaches 0, it dies.
 *
 * <p>Subclasses can add their own behavior after this basic action.
 */
public abstract class LivingEntity extends AbstractEntity implements Actable {

    protected int    age;
    protected double energy;
    protected double maxEnergy;

    protected LivingEntity(Position position, char symbol,
                           double initialEnergy, double maxEnergy) {
        super(position, symbol);
        this.age       = 0;
        this.energy    = initialEnergy;
        this.maxEnergy = maxEnergy;
    }

    // -------------------------------------------------------------------------
    // Actable
    // -------------------------------------------------------------------------

    @Override
    public void act(Environment environment) {
        age++;
        energy -= 2.0;
        if (energy <= 0) {
            alive = false;
        }
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public int    getAge()       { return age; }
    public double getEnergy()    { return energy; }
    public double getMaxEnergy() { return maxEnergy; }

    /**
     * Sets energy, clamping to [0, maxEnergy].
     */
    public void setEnergy(double energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
        if (this.energy <= 0) this.alive = false;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return String.format("%s %s energy=%.1f age=%d alive=%b",
                             getClass().getSimpleName(), position, energy, age, alive);
    }
}
