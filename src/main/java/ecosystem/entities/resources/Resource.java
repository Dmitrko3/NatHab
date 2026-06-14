package ecosystem.entities.resources;

import ecosystem.engine.Position;
import ecosystem.entities.base.StaticEntity;

/**
 * A fixed world resource, such as a rock or water.
 */
public abstract class Resource extends StaticEntity {

    protected Resource(Position position, char symbol) {
        super(position, symbol);
    }
}
