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
 * Base class for all animals in the simulation.
 *
 * <p>An animal delegates its <em>how-to-move</em> logic to a
 * {@link MovementStrategy} and its <em>how-to-feed</em> logic to a
 * {@link FeedingBehavior}, following the Strategy design pattern.
 *
 * <p>Per-tick sequence (via {@link #act}):
 * <ol>
 *   <li>Age + energy drain ({@code super.act}).</li>
 *   <li>Sense nearby entities.</li>
 *   <li>Move (strategy).</li>
 *   <li>Eat from the pre-move nearby list (strategy).</li>
 * </ol>
 *
 * <p>Animals also implement {@link Consumable} so they can themselves be eaten
 * (yielding 80 % of their current energy).
 */
public abstract class Animal extends LivingEntity
        implements Movable, Eater, Sensory, Consumable {

    /** Fixed perception radius (Manhattan distance). */
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
     * Consumes {@code target}: invokes its {@code onConsumed()} callback then
     * credits this animal's energy account.
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

    /** Prey yields 80 % of its current energy as nutrition. */
    @Override
    public double getNutritionValue() {
        return energy * 0.8;
    }

    /** Being consumed kills this animal. */
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
