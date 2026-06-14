package ecosystem.entities.decorators;

import ecosystem.engine.Environment;
import ecosystem.entities.base.AbstractEntity;
import ecosystem.entities.animals.Animal;

import java.util.List;

/**
 * Poison effect applied to entities; operates by reducing energy if supported.
 * Applies a poison effect, draining extra energy.
 */
public class PoisonedDecorator extends EntityDecorator {
    public PoisonedDecorator(AbstractEntity decoratedEntity) {
        super(decoratedEntity);
    }

    @Override
    public boolean act(Environment environment) {
        if (getDuration() <= 0) {
            removeEffect(environment);
            return getDecoratedEntity().act(environment);
        }

        boolean result = getDecoratedEntity().act(environment);

        // Apply poison penalty using polymorphic accessors. Non-living entities will ignore.
        double current = getDecoratedEntity().getEnergy();
        getDecoratedEntity().setEnergy(current - 5);

        decrementDuration();
        return result;
    }

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        return false;
    }
}