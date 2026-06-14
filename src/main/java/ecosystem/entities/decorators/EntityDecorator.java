package ecosystem.entities.decorators;

import ecosystem.engine.Environment;
import ecosystem.engine.Position;
import ecosystem.entities.base.AbstractEntity;

/**
 * Base decorator for entities.
 *Abstract base for dynamically adding effects to entities.
 */
public abstract class EntityDecorator extends AbstractEntity {
    private final AbstractEntity decoratedEntity; // now concrete type
    private int duration = 10;

    public EntityDecorator(AbstractEntity decoratedEntity) {
        super(decoratedEntity.getPosition(), decoratedEntity.getSymbol());
        this.decoratedEntity = decoratedEntity;
    }

    // Protected accessor API for subclasses
    protected AbstractEntity getDecoratedEntity() {
        return decoratedEntity;
    }

    protected int getDuration() {
        return duration;
    }

    protected void setDuration(int newDuration) {
        this.duration = Math.max(0, newDuration);
    }

    protected void decrementDuration() {
        setDuration(this.duration - 1);
    }

    @Override
    public boolean act(Environment environment) {
        if (getDuration() <= 0) {
            removeEffect(environment);
            return getDecoratedEntity().act(environment);
        }
        decrementDuration();
        return true; // subclasses inject behavior in their overrides
    }
    /**
     * Removes the effect and restores the original entity.
     * @param environment The simulation grid.
     */
    protected void removeEffect(Environment environment) {
        AbstractEntity original = getDecoratedEntity();
        original.setPosition(this.getPosition());
        environment.removeEntity(this);
        environment.addEntity(original);
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        getDecoratedEntity().setPosition(position);
    }

    @Override
    public boolean isAlive() {
        return getDecoratedEntity().isAlive();
    }

    @Override
    public void setAlive(boolean alive) {
        super.setAlive(alive);
        getDecoratedEntity().setAlive(alive);
    }

    @Override
    public char getSymbol() {
        return getDecoratedEntity().getSymbol();
    }

    // Expose energy passthroughs (delegation)
    @Override
    public double getEnergy() {
        return getDecoratedEntity().getEnergy();
    }

    @Override
    public double getMaxEnergy() {
        return getDecoratedEntity().getMaxEnergy();
    }

    @Override
    public void setEnergy(double energy) {
        getDecoratedEntity().setEnergy(energy);
    }

    @Override
    public int getAge() {
        return getDecoratedEntity().getAge();
    }
}