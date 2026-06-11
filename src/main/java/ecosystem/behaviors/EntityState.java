package ecosystem.behaviors;

import ecosystem.core.Environment;
import ecosystem.entities.LivingEntity;

public interface EntityState {
    void doAction(LivingEntity e, Environment env);
}