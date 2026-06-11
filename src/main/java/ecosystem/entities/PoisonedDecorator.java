package ecosystem.entities;

import ecosystem.core.Environment;
import ecosystem.entities.LivingEntity;
import ecosystem.entities.animals.Animal;
import ecosystem.interfaces.Actable;

import java.util.List;

/**
 * Poison effect applied to living entities.
 */
public class PoisonedDecorator extends EntityDecorator {
    private final LivingEntity livingTarget;

    /**
     * Construct with a LivingEntity target. This makes the decorator's intent explicit
     * and avoids instanceof checks during act().
     */
    public PoisonedDecorator(LivingEntity decoratedEntity) {
        super((Actable) decoratedEntity);
        this.livingTarget = decoratedEntity;
    }

    @Override
    public boolean act(Environment environment) {
        if (getDuration() <= 0) {
            removeEffect(environment);
            return getDecoratedEntity().act(environment);
        }

        boolean result = getDecoratedEntity().act(environment);

        if (livingTarget != null) {
            livingTarget.setEnergy(livingTarget.getEnergy() - 5); // Poison penalty
        }

        decrementDuration();
        return result;
    }

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        return false;
    }
}