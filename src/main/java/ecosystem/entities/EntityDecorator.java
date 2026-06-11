package ecosystem.entities;

import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.AbstractEntity;
import ecosystem.interfaces.Actable;

public abstract class EntityDecorator extends AbstractEntity {
    protected Actable decoratedEntity; // Composition required by assignment
    protected int duration = 10;

    public EntityDecorator(Actable decoratedEntity) {
        super(((AbstractEntity) decoratedEntity).getPosition(), ((AbstractEntity) decoratedEntity).getSymbol());
        this.decoratedEntity = decoratedEntity;
    }

    @Override
    public boolean act(Environment environment) {
        if (duration <= 0) {
            removeEffect(environment);
            return decoratedEntity.act(environment);
        }
        duration--;
        return true; // Behavior injected in subclasses
    }

    protected void removeEffect(Environment environment) {
        AbstractEntity original = (AbstractEntity) decoratedEntity;
        original.setPosition(this.getPosition());
        environment.removeEntity(this);
        environment.addEntity(original);
    }

    // We delegate base operations so it continues to function normally in the grid
    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        if (decoratedEntity instanceof AbstractEntity) {
            ((AbstractEntity) decoratedEntity).setPosition(position);
        }
    }

    @Override
    public boolean isAlive() {
        if (decoratedEntity instanceof AbstractEntity) return ((AbstractEntity) decoratedEntity).isAlive();
        return super.isAlive();
    }

    @Override
    public void setAlive(boolean alive) {
        super.setAlive(alive);
        if (decoratedEntity instanceof AbstractEntity) ((AbstractEntity) decoratedEntity).setAlive(alive);
    }

    @Override
    public char getSymbol() {
        if (decoratedEntity instanceof AbstractEntity) return ((AbstractEntity) decoratedEntity).getSymbol();
        return super.getSymbol();
    }
}