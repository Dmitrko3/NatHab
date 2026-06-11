package ecosystem.entities;

import ecosystem.core.Environment;
import ecosystem.entities.LivingEntity;
import ecosystem.entities.animals.Animal;
import ecosystem.interfaces.Actable;

import java.util.List;

public class PoisonedDecorator extends EntityDecorator {
    public PoisonedDecorator(Actable decoratedEntity) {
        super(decoratedEntity);
    }

    @Override
    public boolean act(Environment environment) {
        if (duration <= 0) {
            removeEffect(environment);
            return decoratedEntity.act(environment);
        }

        boolean result = decoratedEntity.act(environment);

        if (decoratedEntity instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) decoratedEntity;
            le.setEnergy(le.getEnergy() - 5); // Poison penalty
        }

        duration--;
        return result;
    }

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        return false;
    }
}