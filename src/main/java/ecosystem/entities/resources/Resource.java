package ecosystem.entities.resources;

import ecosystem.core.Position;
import ecosystem.entities.StaticEntity;

/**
 * A static, non-living world resource (rocks, water, etc.).
 */
public abstract class Resource extends StaticEntity {

    protected Resource(Position position, char symbol) {
        super(position, symbol);
    }
}
