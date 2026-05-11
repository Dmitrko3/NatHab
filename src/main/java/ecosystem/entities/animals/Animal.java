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
    public void act(Environment environment) {
        super.act(environment);        // age++, energy -= 2, maybe die
        if (!alive) return;

        List<AbstractEntity> nearby = sense(environment);   // sense before moving
        movementStrategy.move(this, environment);           // move
        feedingBehavior.eat(this, nearby);                  // eat (pre-move snapshot)
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
        target.onConsumed();
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
    public void onConsumed() {
        alive = false;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public MovementStrategy getMovementStrategy() { return movementStrategy; }
    public FeedingBehavior  getFeedingBehavior()  { return feedingBehavior;  }
}
