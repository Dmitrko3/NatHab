package ecosystem.entities;

import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.AbstractEntity;
import ecosystem.interfaces.Actable;

/**
 * Base decorator for entities.
 */
public abstract class EntityDecorator extends AbstractEntity {
    private final Actable decoratedEntity; // now private
    private int duration = 10;             // now private

    public EntityDecorator(Actable decoratedEntity) {
        super(((AbstractEntity) decoratedEntity).getPosition(), ((AbstractEntity) decoratedEntity).getSymbol());
        this.decoratedEntity = decoratedEntity;
    }

    // --------- Protected accessor API for subclasses (validation/encapsulation) ---------
    protected Actable getDecoratedEntity() {
        return decoratedEntity;
    }

    protected int getDuration() {
        return duration;
    }

    /**
     * Set duration with validation.
     */
    protected void setDuration(int newDuration) {
        this.duration = Math.max(0, newDuration);
    }

    /**
     * Decrement duration by 1 .
     */
    protected void decrementDuration() {
        setDuration(this.duration - 1);
    }

    // -----------------------------------------------------------------------------------
    @Override
    public boolean act(Environment environment) {
        if (getDuration() <= 0) {
            removeEffect(environment);
            return getDecoratedEntity().act(environment);
        }
        decrementDuration();
        return true; // Subclasses inject behavior in their overrides
    }

    protected void removeEffect(Environment environment) {
        AbstractEntity original = (AbstractEntity) getDecoratedEntity();
        // put the original back into the grid at this decorator's position
        original.setPosition(this.getPosition());
        environment.removeEntity(this);
        environment.addEntity(original);
    }

    // Delegate base operations to the wrapped entity where possible using accessors
    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        if (getDecoratedEntity() instanceof AbstractEntity) {
            ((AbstractEntity) getDecoratedEntity()).setPosition(position);
        }
    }

    @Override
    public boolean isAlive() {
        if (getDecoratedEntity() instanceof AbstractEntity) return ((AbstractEntity) getDecoratedEntity()).isAlive();
        return super.isAlive();
    }

    @Override
    public void setAlive(boolean alive) {
        super.setAlive(alive);
        if (getDecoratedEntity() instanceof AbstractEntity) ((AbstractEntity) getDecoratedEntity()).setAlive(alive);
    }

    @Override
    public char getSymbol() {
        if (getDecoratedEntity() instanceof AbstractEntity) return ((AbstractEntity) getDecoratedEntity()).getSymbol();
        return super.getSymbol();
    }
}