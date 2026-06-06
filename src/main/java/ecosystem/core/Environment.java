package ecosystem.core;

import ecosystem.entities.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * Represents the 2D world where entities live.
 *
 * <p>Keeps track of where entities are placed and helps find nearby entities.
 */
public class Environment {

    /** Width of the simulation grid in cells. */
    public static final int WIDTH  = 20;
    /** Height of the simulation grid in cells. */
    public static final int HEIGHT = 20;

    /** Spatial lookup: position → entity occupying that cell. */
    private final Map<Position, AbstractEntity> mapGrid;

    /** Flat list of all entities ever added (including dead ones until cleanup). */
    private final List<AbstractEntity> entitiesList;

    // -------------------------------------------------------------------------
    // Locking (fine-grained)
    // -------------------------------------------------------------------------
    // Locks keyed by Position and by entity identity; these allow thread-safe,
    // fine-grained operations without a global Environment lock.
    private final ConcurrentMap<Position, ReentrantLock> positionLocks;
    private final ConcurrentMap<AbstractEntity, ReentrantLock> entityLocks;
    // A shared monitor for resource availability notifications (animals wait on this)
    private final Object resourceMonitor = new Object();
    // Command queue to which entities can submit actions.
    // The SimulationEngine will drain and execute actions sequentially.
    private final BlockingQueue<SimulationAction> actionQueue = new LinkedBlockingQueue<>();
    public Environment() {
        this.mapGrid       = new HashMap<>();
        this.entitiesList  = new ArrayList<>();
        this.positionLocks = new ConcurrentHashMap<>();
        this.entityLocks   = new ConcurrentHashMap<>();
    }

    // -------------------------------------------------------------------------
    // Spatial helpers
    // -------------------------------------------------------------------------
    /**
     * Checks if a position is free.
     * @return true if in-bounds and empty (or occupant is dead).
     */
    public boolean isPositionFree(Position pos) {
        if (pos.getX() < 0 || pos.getX() >= WIDTH
                || pos.getY() < 0 || pos.getY() >= HEIGHT) {
            return false;
        }
        AbstractEntity occupant = mapGrid.get(pos);
        // A cell is free if it is empty or its previous occupant is already dead
        return occupant == null || !occupant.isAlive();
    }

    /**
     * Returns a list of living entities within a 2-tile radius of the given position.
     *
     * @param pos The central position.
     * @return List of nearby entities.
     */
    public List<AbstractEntity> getNearbyEntities(Position pos) {
        List<AbstractEntity> nearby = new ArrayList<>();
        for (AbstractEntity entity : entitiesList) {
            if (entity.isAlive()
                    && !entity.getPosition().equals(pos)
                    && entity.getPosition().distanceTo(pos) <= 2) {
                nearby.add(entity);
            }
        }
        return nearby;
    }

    // -------------------------------------------------------------------------
    // Entity management (unchanged API)
    // -------------------------------------------------------------------------
    /**
     * Adds an entity to the world.
     *
     * @return {@code true} if the entity was added successfully
     */
    public boolean addEntity(AbstractEntity entity) {
        if (entity == null) return false;
        if (!isPositionFree(entity.getPosition())) return false;
        mapGrid.put(entity.getPosition(), entity);
        entitiesList.add(entity);

        // No instanceof Consumable! Just check if it yields nutrition.
        if (entity.isAlive() && entity.getNutritionValue() > 0) {
            synchronized (resourceMonitor) {
                resourceMonitor.notifyAll();
            }
        }
        return true;
    }
    /**
     * Removes an entity from the world.
     *
     * @return {@code true} if the entity was removed successfully
     */
    public boolean removeEntity(AbstractEntity entity) {
        if (entity == null) return false;
        // Remove from spatial map only if the map still points to this entity
        mapGrid.remove(entity.getPosition(), entity);
        return entitiesList.remove(entity);
    }

    /** Updates the entity's position in the map after it moves.
     *@return true if the environment was updated successfully, false otherwise*/
    public boolean updateEntityPosition(AbstractEntity entity,
                                        Position oldPos, Position newPos) {
        if (entity == null || oldPos == null || newPos == null) return false;
        // If the old position mapping does not point to this entity, be defensive:
        AbstractEntity current = mapGrid.get(oldPos);
        if (current != null && current != entity) {
            // Someone else occupies oldPos; do not remove them.
            // Only allow placing into newPos if it's free.
            if (!isPositionFree(newPos)) return false;
        }

        // Remove oldPos only if it maps to this entity (avoid removing other occupants)
        mapGrid.remove(oldPos, entity);
        mapGrid.put(newPos, entity);
        return true;
    }

    /** * Clears the world of all entities.
     * * @return true if the environment was not already empty.
     */
    public boolean clear() {
        boolean hadEntries = !mapGrid.isEmpty() || !entitiesList.isEmpty();
        mapGrid.clear();
        entitiesList.clear();
        return hadEntries;
    }

    // -------------------------------------------------------------------------
    // Fine-grained locking API
    // -------------------------------------------------------------------------

    private ReentrantLock lockForPosition(Position p) {
        // computeIfAbsent is safe: equal Positions share map bucket
        return positionLocks.computeIfAbsent(p, k -> new ReentrantLock());
    }

    private ReentrantLock lockForEntity(AbstractEntity entity) {
        return entityLocks.computeIfAbsent(entity, k -> new ReentrantLock());
    }

    /**
     * Try to obtain the lock for an entity within the timeout (ms).
     *
     * @return true if lock acquired
     */
    public boolean tryLockEntity(AbstractEntity entity, long timeoutMillis) {
        if (entity == null) return false;
        ReentrantLock lock = lockForEntity(entity);
        try {
            return lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Unlocks the entity lock (if held by current thread).
     */
    public void unlockEntity(AbstractEntity entity) {
        if (entity == null) return;
        ReentrantLock lock = entityLocks.get(entity);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * Attempt to move entity from its current position to newPos atomically.
     *
     * Acquires locks on the two involved positions (old and new) in a deterministic
     * order, validates current map state, performs the update and releases locks.
     *
     * @param entity       the entity performing the move
     * @param newPos       the destination position
     * @param timeoutMillis how long to wait to acquire both locks
     * @return true on success, false if locks couldn't be acquired or validation failed
     */
    public boolean tryMoveEntity(AbstractEntity entity, Position newPos, long timeoutMillis) {
        if (entity == null || newPos == null) return false;

        Position oldPos = entity.getPosition();
        if (oldPos.equals(newPos)) return true; // no-op move

        // Determine canonical lock order to avoid deadlocks: compare by x then y.
        Position first = oldPos;
        Position second = newPos;
        if (comparePositions(first, second) > 0) {
            Position tmp = first; first = second; second = tmp;
        }

        ReentrantLock lock1 = lockForPosition(first);
        ReentrantLock lock2 = lockForPosition(second);

        boolean locked1 = false;
        boolean locked2 = false;

        long deadline = System.currentTimeMillis() + timeoutMillis;
        try {
            // Try lock first
            long timeLeft = Math.max(0, deadline - System.currentTimeMillis());
            locked1 = lock1.tryLock(timeLeft, TimeUnit.MILLISECONDS);
            if (!locked1) return false;

            // Try lock second with remaining time
            timeLeft = Math.max(0, deadline - System.currentTimeMillis());
            locked2 = lock2.tryLock(timeLeft, TimeUnit.MILLISECONDS);
            if (!locked2) return false;

            // Validate state: oldPos should be mapped to this entity (or absent).
            AbstractEntity currentAtOld = mapGrid.get(oldPos);
            if (currentAtOld != null && currentAtOld != entity) {
                // Someone else occupies oldpos — cannot move
                return false;
            }
            // Validate newPos is free (empty or occupant dead)
            AbstractEntity currentAtNew = mapGrid.get(newPos);
            if (currentAtNew != null && currentAtNew.isAlive()) {
                return false;
            }

            // All good, perform the move
            mapGrid.remove(oldPos, entity);
            mapGrid.put(newPos, entity);
            entity.setPosition(newPos);
            return true;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (locked2 && lock2.isHeldByCurrentThread()) lock2.unlock();
            if (locked1 && lock1.isHeldByCurrentThread()) lock1.unlock();
        }

    }
    /**
 * Submit an action to be executed by the engine thread.
 * Non-blocking: returns immediately whether action was accepted.
 * Called by entity threads from any location.
 */
    public boolean submitAction(SimulationAction action) {
        if (action == null) return false;
        return actionQueue.offer(action);  // Non-blocking offer
}

/**
 * Drain all pending actions from the queue and execute them sequentially.
 * Called ONLY by the engine thread at a deterministic point in tick().
 */
public void drainAndExecuteActions() {
    SimulationAction action;
    while ((action = actionQueue.poll()) != null) {
        try {
            action.execute(this);
        } catch (Exception ex) {
            System.err.println("Action execution error: " + ex.getMessage());
            ex.printStackTrace();
            // Continue processing remaining actions
        }
    }
}
    private int comparePositions(Position a, Position b) {
        if (a.getX() != b.getX()) return Integer.compare(a.getX(), b.getX());
        return Integer.compare(a.getY(), b.getY());
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns a read-only list of entities. */
    public List<AbstractEntity> getEntitiesList() {
        return Collections.unmodifiableList(entitiesList);
    }

    /** Returns a read-only view of the spatial map. */
    public Map<Position, AbstractEntity> getMapGrid() {
        return Collections.unmodifiableMap(mapGrid);
    }
    /** Returns the monitor object animals can wait on for new resources. */
    public Object getResourceMonitor() {
        return resourceMonitor;
    }
}