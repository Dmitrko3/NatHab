package ecosystem.entities;

import ecosystem.core.Environment;
import ecosystem.entities.animals.Animal;

import java.util.List;

/**
 * Poison effect applied to entities; operates by reducing energy if supported.
 * The decorator accepts any AbstractEntity; setEnergy/getEnergy are default to no-op
 * for non-living entities.
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