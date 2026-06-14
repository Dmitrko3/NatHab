package ecosystem.entities.animals;

import ecosystem.behaviors.state.EntityState;
import ecosystem.behaviors.diet.FeedingBehavior;
import ecosystem.behaviors.state.IdleState;
import ecosystem.behaviors.movement.MovementStrategy;
import ecosystem.engine.Environment;
import ecosystem.engine.Position;
import ecosystem.entities.base.AbstractEntity;
import ecosystem.entities.base.LivingEntity;
import ecosystem.traits.*;

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
    protected EntityState currentState; // Added State property

    protected Animal(Position position, char symbol,
                     double initialEnergy, double maxEnergy,
                     MovementStrategy movementStrategy,
                     FeedingBehavior feedingBehavior) {
        super(position, symbol, initialEnergy, maxEnergy);
        this.movementStrategy = movementStrategy;
        this.feedingBehavior  = feedingBehavior;
        this.currentState = new IdleState(); // Start in IdleState
    }

    /**
     * Set the current state for this animal.
     * Returns true when the assignment was successful (state != null), false otherwise.
     */
    public boolean setState(EntityState state) {
        if (state == null) return false;
        this.currentState = state;
        return true;
    }
    // -------------------------------------------------------------------------
    // Actable
    // -------------------------------------------------------------------------
    @Override
    public boolean act(Environment environment) {
        this.age++;
        if (!alive) return false;

        // Delegate behavior to the current state and observe success/failure
        if (currentState != null) {
            boolean ok = currentState.doAction(this, environment);
            if (!ok) {
                // Log a warning — state action failed
                System.err.println("State " + currentState.getClass().getSimpleName() + " failed for " + this);
            }
        }

        if (this.energy <= 0) {
            this.alive = false;
        }
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