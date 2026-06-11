package ecosystem.entities;

import ecosystem.core.Position;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.LivingEntity;
import ecosystem.entities.animals.*;
import ecosystem.entities.plants.*;
import ecosystem.entities.resources.*;

public class EntityFactory {

    public static String[] getSupportedTypes() {
        return new String[] { "Lion", "Deer", "Rabbit", "Tree", "Flower", "Rock", "Water" };
    }

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

        if (initialEnergy >= 0 && entity instanceof LivingEntity) {
            ((LivingEntity) entity).setEnergy(initialEnergy);
        }

        return entity;
    }
}