package ecosystem.entities.decorators;

import ecosystem.engine.Environment;
import ecosystem.entities.base.AbstractEntity;
import ecosystem.entities.animals.Animal;

/**
 * Applies a speed boost, allowing two actions per tick.
 */
public class SpeedBoostDecorator extends EntityDecorator {
    public SpeedBoostDecorator(AbstractEntity decoratedEntity) {
        super(decoratedEntity);
    }

    @Override
    public boolean act(Environment environment) {
        if (getDuration() <= 0) {
            removeEffect(environment);
            return getDecoratedEntity().act(environment);
        }

        // Executes twice per tick (Speed Boost)
        boolean r1 = getDecoratedEntity().act(environment);
        boolean r2 = getDecoratedEntity().act(environment);

        decrementDuration();
        return r1 || r2;
    }

    @Override
    public boolean eat(Animal animal, java.util.List<AbstractEntity> nearby, Environment environment) {
        return false;
    }
}