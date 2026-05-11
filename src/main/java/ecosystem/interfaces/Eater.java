package ecosystem.interfaces;

/**
 * Contract for entities capable of consuming a {@link Consumable}.
 */
public interface Eater {
    /**
     * Consume the specified target, gaining its nutrition value.
     *
     * @param target the entity to eat
     * @return {@code true} if eating succeeded
     */
    boolean eat(Consumable target);
}
