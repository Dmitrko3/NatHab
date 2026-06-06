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

        boolean removed = environment.removeEntity(target);
        if (removed) {
            // Polymorphic call - no instanceof LivingEntity check or casting!
            eater.addEnergy(10.0);
        }
        return removed;
    }
}