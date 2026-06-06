package ecosystem.entities.animals;

import ecosystem.behaviors.FeedingBehavior;
import ecosystem.behaviors.MovementStrategy;
import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.LivingEntity;
import ecosystem.interfaces.*;

import java.util.List;

/**
 * Base class for all animals.
 *
 * <p>Animals can move, eat, age, and lose energy each turn.
 * They use movement and feeding behaviors to decide how to act.
 *
 * <p>Animals can also be eaten by other animals.
 */
public abstract class Animal extends LivingEntity
        implements Movable, Eater, Sensory, Consumable {

    /** Distance an animal can sense nearby entities. */
    protected static final int VISION_RANGE = 2;

    protected MovementStrategy movementStrategy;
    protected FeedingBehavior  feedingBehavior;

    protected Animal(Position position, char symbol,
                     double initialEnergy, double maxEnergy,
                     MovementStrategy movementStrategy,
                     FeedingBehavior feedingBehavior) {
        super(position, symbol, initialEnergy, maxEnergy);
        this.movementStrategy = movementStrategy;
        this.feedingBehavior  = feedingBehavior;
    }

    // -------------------------------------------------------------------------
    // Actable
    // -------------------------------------------------------------------------

    @Override
    public boolean act(Environment environment) {
        super.act(environment);        // age++, energy -= 2, maybe die
        if (!alive) return false;

        List<AbstractEntity> nearby = sense(environment);   // sense before moving
        movementStrategy.move(this, environment);           // move
        feedingBehavior.eat(this, nearby, environment);     // eat (pre-move snapshot)
        return true;
    }

    // -------------------------------------------------------------------------
    // Movable  (delegates to strategy; also satisfies interface)
    // -------------------------------------------------------------------------

    @Override
    public boolean move(Environment environment) {
        return movementStrategy.move(this, environment);
    }

    // -------------------------------------------------------------------------
    // Eater
    // -------------------------------------------------------------------------

    /**
     * Eats the target and adds its energy to this animal.
     */
    @Override
    public boolean eat(Consumable target) {
        if (target == null) return false;
        double gain = target.getNutritionValue();   // read value before state changes
        if(target.onConsumed())
            setEnergy(energy + gain);
        return true;
    }

    // -------------------------------------------------------------------------
    // Sensory
    // -------------------------------------------------------------------------

    @Override
    public List<AbstractEntity> sense(Environment environment) {
        return environment.getNearbyEntities(position);
    }

    // -------------------------------------------------------------------------
    // Consumable  (animals can be prey)
    // -------------------------------------------------------------------------

    /** Returns how much energy this animal gives when eaten. */
    @Override
    public double getNutritionValue() {
        return energy * 0.8;
    }

    /** Kills this animal when it is eaten. */
    @Override
    public boolean onConsumed() {
        alive = false;
        return true;
    }
    /** * Determine whether a given eater can eat this animal.
     * For now: only consumers that behave as carnivores can eat animals. */
    @Override
    public boolean isEdibleBy(Eater consumer) {
        return consumer != null && consumer.isCarnivore();
    }
    @Override
    public boolean isAnimal() { return true; }

    @Override
    public boolean isCarnivore() { return feedingBehavior != null && feedingBehavior.isCarnivore(); }

    @Override
    public boolean isHerbivore() { return feedingBehavior != null && feedingBehavior.isHerbivore(); }


    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public MovementStrategy getMovementStrategy() { return movementStrategy; }
    public FeedingBehavior  getFeedingBehavior()  { return feedingBehavior;  }
}
