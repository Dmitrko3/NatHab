package ecosystem.entities;

import ecosystem.core.Environment;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.EntityDecorator;
import ecosystem.entities.animals.Animal;
import ecosystem.interfaces.Actable;

import java.util.List;

public class SpeedBoostDecorator extends EntityDecorator {
    public SpeedBoostDecorator(Actable decoratedEntity) {
        super(decoratedEntity);
    }

    @Override
    public boolean act(Environment environment) {
        if (duration <= 0) {
            removeEffect(environment);
            return decoratedEntity.act(environment);
        }

        // Executes twice per tick (Speed Boost)
        boolean r1 = decoratedEntity.act(environment);
        boolean r2 = decoratedEntity.act(environment);

        duration--;
        return r1 || r2;
    }

    @Override
    public boolean eat(Animal animal, List<AbstractEntity> nearby, Environment environment) {
        return false;
    }
}