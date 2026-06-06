package ecosystem.entities.plants;

import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.LivingEntity;
import ecosystem.interfaces.Consumable;
import ecosystem.interfaces.Eater;
import ecosystem.interfaces.EdibleByHerbivore;
import ecosystem.interfaces.Reproducible;
/**
 * Base class for all plants.
 *
 * <p>Plants can be eaten by herbivores, grow over time,
 * and can create new plants in nearby cells.
 */
public abstract class Plant extends LivingEntity
        implements EdibleByHerbivore, Consumable, Reproducible {

    /** Energy gained each turn from sunlight. */
    protected double growthRate;

    /** Chance to create a new plant each turn. */
    protected double reproductionChance;

    protected Plant(Position position, char symbol,
                    double initialEnergy, double maxEnergy,
                    double growthRate, double reproductionChance) {
        super(position, symbol, initialEnergy, maxEnergy);
        this.growthRate        = growthRate;
        this.reproductionChance = reproductionChance;
    }

    // -------------------------------------------------------------------------
    // Actable  (does NOT call super.act — plants don't drain energy)
    // -------------------------------------------------------------------------

    @Override
    public boolean act(Environment environment) {
        age++;
        energy = Math.min(maxEnergy, energy + growthRate);
        // Plants don't call super.act(), so energy never drops to 0 from aging.
        reproduce(environment);
        return true;
    }

    // -------------------------------------------------------------------------
    // Consumable
    // -------------------------------------------------------------------------

    /** Plants yield all their current energy as nutrition. */
    @Override
    public double getNutritionValue() {
        return energy;
    }

    /** Being eaten kills the plant. */
    @Override
    public boolean onConsumed() {
        alive = false;
        return true;
    }
    /**
     * Plants are edible by herbivores (consumers whose feeding behavior is HerbivoreBehavior).
     */
    @Override
    public boolean isEdibleBy(Eater consumer) {
        return consumer != null && consumer.isHerbivore();
    }

    @Override
    public boolean isPlant() { return true; }
    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public double getGrowthRate()        { return growthRate; }
    public double getReproductionChance(){ return reproductionChance; }
}
