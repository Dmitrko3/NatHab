package ecosystem.core;

import ecosystem.entities.AbstractEntity;

/** * EatAction removes a target (resource/plant) from the environment and * optionally changes the eater's energy (done by the eater entity itself * or by the action). * * This action should be created by the eater entity and enqueued. */
public class EatAction implements SimulationAction {
    private final AbstractEntity eater;
    private final AbstractEntity target;

    public EatAction(AbstractEntity eater, AbstractEntity target) {
        this.eater = eater;
        this.target = target;
    }

    @Override
    public boolean execute(Environment environment) {
        if (target == null || eater == null || !target.isAlive() || !eater.isAlive()) {
            return false;
        }
        // Example: remove target from environment and credit eater energy.
        boolean removed = environment.removeEntity(target);
        if (removed) {
            // Optionally update eater energy (uses its setter)
            if (eater instanceof ecosystem.entities.LivingEntity) {
                // safe cast; adjust energy amount as needed
                ecosystem.entities.LivingEntity le = (ecosystem.entities.LivingEntity) eater;
                le.setEnergy(Math.min(le.getMaxEnergy(), le.getEnergy() + 10.0));
            }
        }
        return removed;
    }
}