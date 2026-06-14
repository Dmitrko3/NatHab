package ecosystem.network;

import ecosystem.engine.*;
import ecosystem.entities.base.*;


/**
 * Command that creates a new entity in the environment.
 */
public class SpawnCommand implements NetworkCommand {

    private final String type;
    private final int x;
    private final int y;
    private final int energy;

    public SpawnCommand(String type, int x, int y, int energy) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.energy = energy;
    }

    @Override
    public boolean execute(Environment env) {
        Position pos = new Position(x, y);
        // EntityFactory will throw IllegalArgumentException for unknown types
        AbstractEntity entity = EntityFactory.createEntity(type, pos, energy);
        return env.addEntity(entity);
    }

    @Override
    public String toString() {
        return "SpawnCommand{" + type + " x=" + x + " y=" + y + " energy=" + energy + "}";
    }
}