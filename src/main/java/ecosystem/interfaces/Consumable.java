package ecosystem.interfaces;

/**
 * Contract for entities that can be consumed by an {@link Eater}.
 */
public interface Consumable {
    /**
     * Returns the energy gained by the consumer when this entity is eaten.
     *
     * @return nutrition value (energy units)
     */
    double getNutritionValue();

    /**
     * Called when this entity is consumed.  Implementations decide whether
     * the entity dies, replenishes itself, etc.
     */
    boolean onConsumed();
}
