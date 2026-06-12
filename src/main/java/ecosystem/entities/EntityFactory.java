package ecosystem.entities;

import ecosystem.core.Position;
import ecosystem.entities.animals.*;
import ecosystem.entities.plants.*;
import ecosystem.entities.resources.*;

/**
 * Design Pattern: Factory Method
 * Factory for creating entities dynamically, ensuring the Open/Closed Principle.
 */
public class EntityFactory {
    /** @return Array of supported entity types. */
    public static String[] getSupportedTypes() {
        return new String[] { "Lion", "Deer", "Rabbit", "Tree", "Flower", "Rock", "Water" };
    }
    /**
     * Creates a new entity.
     * @param type Entity type (e.g., "Lion").
     * @param pos Starting position.
     * @param initialEnergy Starting energy (if applicable).
     * @return The created AbstractEntity.
     */
    public static AbstractEntity createEntity(String type, Position pos, int initialEnergy) {
        AbstractEntity entity = null;

        switch (type) {
            case "Lion":   entity = new Lion(pos); break;
            case "Deer":   entity = new Deer(pos); break;
            case "Rabbit": entity = new Rabbit(pos); break;
            case "Tree":   entity = new Tree(pos); break;
            case "Flower": entity = new Flower(pos); break;
            case "Rock":   entity = new Rock(pos); break;
            case "Water":  entity = new Water(pos); break;
            default: throw new IllegalArgumentException("Unknown entity type: " + type);
        }

        // If the created entity supports energy (getMaxEnergy() > 0), apply initialEnergy
        if (initialEnergy >= 0 && entity.getMaxEnergy() > 0.0) {
            entity.setEnergy(initialEnergy);
        }

        return entity;
    }
}